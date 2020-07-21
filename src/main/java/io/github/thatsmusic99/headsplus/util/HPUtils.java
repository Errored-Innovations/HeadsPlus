package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;

public class HPUtils {

    private static final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static void addBossBar(OfflinePlayer pl) {
        HPPlayer p = HPPlayer.getHPPlayer(pl);
        ConfigurationSection c = HeadsPlus.getInstance().getConfiguration().getMechanics();
        if (c.getBoolean("boss-bar.enabled")) {
            if (p.getNextLevel() != null) {
                try {
                    if (!bossBars.containsKey(pl.getPlayer().getUniqueId())) {
                        String s = ChatColor.translateAlternateColorCodes('&', c.getString("boss-bar.title"));
                        BossBar bossBar = Bukkit.getServer().createBossBar(s, BarColor.valueOf(c.getString("boss-bar.color")), BarStyle.SEGMENTED_6);
                        bossBar.addPlayer(pl.getPlayer());
                        Double d = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                        d = 1 - d;
                        bossBar.setProgress(d);
                        bossBar.setVisible(true);
                        bossBars.put(pl.getPlayer().getUniqueId(), bossBar);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                bossBar.setVisible(false);
                                bossBar.removePlayer(pl.getPlayer());
                                bossBars.remove(pl.getPlayer().getUniqueId());
                            }
                        }.runTaskLater(HeadsPlus.getInstance(), c.getInt("boss-bar.lifetime") * 20);
                    } else {
                        Double d = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                        d = 1 - d;
                        bossBars.get(pl.getPlayer().getUniqueId()).setProgress(d);
                    }
                } catch (NoClassDefFoundError | IllegalArgumentException | NullPointerException ignored) {

                }

            }
        }
    }

    public static int matchCount(Matcher m) {
        int i = 0;
        while (m.find()) {
            i++;
        }
        return i;
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
        ConfigurationSection lootingThresholds = HeadsPlus.getInstance().getConfiguration().getMechanics().getConfigurationSection("looting.thresholds");
        if (lootingThresholds == null) return chance;
        double level = 0;
        if (killer.getInventory().getItem(EquipmentSlot.HAND).containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
            ItemStack item = killer.getInventory().getItem(EquipmentSlot.HAND);
            level = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        if (level == 0) return chance;
        if (chance <= lootingThresholds.getDouble("rare")) {
            chance += level;
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

    public static void dropHead(String id, String meta, Location location, int amount, Player killer) {
        Random random = new Random();
        HashMap<String, List<EntityHead>> storedHeads = EntityDataManager.getStoredHeads();
        List<EntityHead> heads = storedHeads.get(id + ";" + meta);
        if (heads == null) {
            String[] possibleConditions = meta.split(",");
            for (String str : possibleConditions) {
                if ((heads = storedHeads.get(id + ";" + str)) != null) break;
            }
            if (heads == null) {
                heads = storedHeads.get(id + ";default");
            }
        }
        if (heads == null) {
            HeadsPlus.getInstance().getLogger().warning("Found no heads list for " + id + "!");
            return;
        }
        if (heads.isEmpty()) return;
        EntityHead head = heads.get(random.nextInt(heads.size()));
        head.withAmount(amount);
        EntityHeadDropEvent event = new EntityHeadDropEvent(killer, head, location, EntityType.valueOf(id), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            location.getWorld().dropItem(location, head.getItemStack());
        }
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

    public enum SkillType {
        HUNTING,
        CRAFTING
    }
}
