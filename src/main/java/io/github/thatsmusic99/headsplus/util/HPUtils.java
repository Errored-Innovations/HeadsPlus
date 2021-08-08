package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class HPUtils {

    private static final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static void addBossBar(OfflinePlayer pl) {
        HPPlayer p = HPPlayer.getHPPlayer(pl.getUniqueId());
        if (!MainConfig.get().getLevels().ENABLE_BOSS_BARS) return;
        if (p.getNextLevel() == null) return;
        try {
            if (!bossBars.containsKey(p.getUuid())) {
                String title = ChatColor.translateAlternateColorCodes('&', MainConfig.get().getLevels().BOSS_BAR_TITLE);
                BossBar bossBar = Bukkit.getServer().createBossBar(title, BarColor.valueOf(MainConfig.get().getLevels().BOSS_BAR_COLOR), BarStyle.SEGMENTED_6);
                if (pl.getPlayer() != null)
                    bossBar.addPlayer(pl.getPlayer());
                double percentageProgress = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                percentageProgress = 1 - percentageProgress;
                bossBar.setProgress(percentageProgress);
                bossBar.setVisible(true);
                bossBars.put(p.getUuid(), bossBar);
                Bukkit.getScheduler().runTaskLater(HeadsPlus.get(), () -> {
                    bossBar.setVisible(false);
                    bossBar.removePlayer(pl.getPlayer());
                    bossBars.remove(pl.getPlayer().getUniqueId());
                }, MainConfig.get().getLevels().BOSS_BAR_LIFETIME * 20L);
            } else {
                double percentageProgress = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                percentageProgress = 1 - percentageProgress;
                bossBars.get(p.getUuid()).setProgress(percentageProgress);
            }
        } catch (NoClassDefFoundError | IllegalArgumentException ignored) {

        }
    }

    public static int matchCount(Matcher m) {
        int matches = 0;
        while (m.find()) {
            matches++;
        }
        return matches;
    }

    public static <T> T notNull(T object, String message) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static int isInt(String object) throws NumberFormatException {
        return Integer.parseInt(object);
    }

    public static double calculateChance(double chance, double randChance, Player killer) {
        if (!MainConfig.get().getMobDrops().ENABLE_LOOTING) return chance;
        ConfigSection lootingThresholds = MainConfig.get().getConfigSection("thresholds");
        if (lootingThresholds == null) return chance;
        double level = 0;
        if (killer.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
            ItemStack item = killer.getInventory().getItemInMainHand();
            level = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        if (level == 0) return chance;
        if (chance <= lootingThresholds.getDouble("rare")) {
            if (chance < 0) {
                chance *= level;
            } else {
                chance += level;
            }
        } else if (chance <= lootingThresholds.getDouble("uncommon")) {
            if (chance <= randChance) {
                chance = level / (level + 1);
            }
            return chance;
        } else if (chance <= lootingThresholds.getDouble("common")) {
            chance += level * 100;
        }
        return chance;
    }


    public static int getAmount(double fixedChance) {
        if (fixedChance <= 100) return 1;
        int fixedAmount = (int) Math.ceil(fixedChance / 100);
        double extraChance = fixedChance % 100;
        double randChance = new Random().nextDouble() * 100;
        if (randChance <= extraChance) {
            fixedAmount++;
        }
        return fixedAmount;
    }

    public static <T> void addIfAbsent(List<T> list, T element) {
        if (list.contains(element)) return;
        list.add(element);
    }

    public static boolean isMythicMob(Entity entity) {
        HeadsPlus hp = HeadsPlus.get();

        try {
            if (MainConfig.get().getMobDrops().DISABLE_FOR_MYTHIC_MOBS) {
                Plugin plugin = hp.getServer().getPluginManager().getPlugin("MythicMobs");
                if (plugin != null && plugin.isEnabled()) {
                    return MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId());
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static void parseLorePlaceholders(List<String> lore, String message, PlaceholderInfo... placeholders) {
        for (PlaceholderInfo placeholder : placeholders) {
            if (!placeholder.requirement) continue;
            if (!message.contains(placeholder.placeholder)) continue;
            lore.add(message.replace(placeholder.placeholder, String.valueOf(placeholder.replacement.get())));
        }
    }

    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(String name) {
        return CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(name), HeadsPlus.async)
                .thenApplyAsync(player -> player, HeadsPlus.sync);
    }

    public static class PlaceholderInfo {
        private final String placeholder;
        private final Supplier<Object> replacement;
        private final boolean requirement;

        public PlaceholderInfo(String placeholder, Object replacement, boolean requirement) {
            this.placeholder = placeholder;
            this.replacement = () -> replacement;
            this.requirement = requirement;
        }

        public PlaceholderInfo(String placeholder, Supplier<Object> replacement, boolean requirement) {
            this.placeholder = placeholder;
            this.replacement = replacement;
            this.requirement = requirement;
        }
    }
}
