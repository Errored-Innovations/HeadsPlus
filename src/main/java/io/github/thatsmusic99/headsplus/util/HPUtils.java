package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.lumine.mythic.bukkit.MythicBukkit;
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
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
                BossBar bossBar = Bukkit.getServer().createBossBar(title,
                        BarColor.valueOf(MainConfig.get().getLevels().BOSS_BAR_COLOR), BarStyle.SEGMENTED_6);
                if (pl.getPlayer() != null)
                    bossBar.addPlayer(pl.getPlayer());
                double percentageProgress =
                        (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
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
                double percentageProgress =
                        (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
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
        if (killer.getInventory().getItemInMainHand().containsEnchantment(getLooting())) {
            ItemStack item = killer.getInventory().getItemInMainHand();
            level = item.getEnchantmentLevel(getLooting());
        }
        if (level == 0) return chance;
        if (chance <= lootingThresholds.getDouble("rare")) {
            if (chance < 1) {
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

    private static Enchantment getLooting() {
        try {
            return Enchantment.LOOT_BONUS_MOBS;
        } catch (NoSuchFieldError e) {
            return Enchantment.getByName("LOOTING");
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

    public static <T> void addIfAbsent(List<T> list, T element) {
        if (list.contains(element)) return;
        list.add(element);
    }

    @NotNull
    public static <T> T ifNull(T object, @NotNull T alternative) {
        return object == null ? alternative : object;
    }

    public static boolean isMythicMob(Entity entity) {
        HeadsPlus hp = HeadsPlus.get();

        try {
            if (MainConfig.get().getMobDrops().DISABLE_FOR_MYTHIC_MOBS) {
                Plugin plugin = hp.getServer().getPluginManager().getPlugin("MythicMobs");
                if (plugin != null && plugin.isEnabled()) {
                    return MythicBukkit.inst().getMobManager().isActiveMob(entity.getUniqueId());
                }
            }
        } catch (NoClassDefFoundError ex) {
        }
        return false;
    }

    public static @NotNull UUID getUUID(@NotNull String name) {

        UUID uuid;
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        if (player.getUniqueId().equals(offlineUUID) && CachedValues.PLAYER_NAME.matcher(name).matches()) {
            String uuidStr = AutograbManager.grabUUID(name, 0, null);
            if (uuidStr == null) {
                uuid = offlineUUID;
            } else {
                uuid = UUID.fromString(uuidStr.substring(0, 8) +
                        "-" + uuidStr.substring(8, 12) +
                        "-" + uuidStr.substring(12, 16) +
                        "-" + uuidStr.substring(16, 20) +
                        "-" + uuidStr.substring(20));
            }
        } else {
            uuid = player.getUniqueId();
        }
        return uuid;
    }

    public static @NotNull String toBase64Texture(@NotNull String texture) {
        if (CachedValues.BASE64_PATTERN.matcher(texture).matches()) return texture;

        // If it's a hash, amend the URL
        if (texture.matches("^[0-9a-fA-F]+$")) {
            texture = "https://textures.minecraft.net/texture/" + texture;
        }

        // Encode the texture
        texture = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", texture);
        return new String(Base64.getEncoder().encode(texture.getBytes(StandardCharsets.UTF_8)));
    }

    public static @NotNull URL toSkinURL(final @NotNull String texture) throws MalformedURLException {
        if (CachedValues.MINECRAFT_TEXTURES_PATTERN.matcher(texture).matches()) return new URL(texture);

        // If it's a hash, just amend the URL and return it
        if (texture.matches("^[0-9a-fA-F]+$")) {
            return new URL("https://textures.minecraft.net/texture/" + texture);
        }

        // Decode
        String decodedTexture = new String(Base64.getDecoder().decode(texture.getBytes(StandardCharsets.UTF_8)));
        Matcher matcher = CachedValues.MINECRAFT_TEXTURES_PATTERN_LENIENT.matcher(decodedTexture);
        if (matcher.find()) return new URL(matcher.group(0));
        throw new IllegalArgumentException("Failed to get skin URL from " + texture + " (" + decodedTexture + ")");
    }

    public static @NotNull String toSkinHash(@NotNull String texture) {
        if (texture.matches("^[0-9a-fA-F]+$")) return texture;

        Matcher matcher = CachedValues.MINECRAFT_TEXTURES_PATTERN_LENIENT.matcher(texture);
        if (matcher.find()) return matcher.group(3);

        // Decode
        String decodedTexture = new String(Base64.getDecoder().decode(texture.getBytes(StandardCharsets.UTF_8)));
        matcher = CachedValues.MINECRAFT_TEXTURES_PATTERN_LENIENT.matcher(decodedTexture);
        if (matcher.find()) return matcher.group(3);
        throw new IllegalArgumentException("Failed to get skin URL from " + texture + " (" + decodedTexture + ")");
    }

    public static void parseLorePlaceholders(List<String> lore, String message, PlaceholderInfo... placeholders) {
        boolean contains = false;
        for (PlaceholderInfo placeholder : placeholders) {
            if (!message.contains(placeholder.placeholder)) continue;
            contains = true;
            if (!placeholder.requirement) continue;
            contains = false;
            message = message.replace(placeholder.placeholder, String.valueOf(placeholder.replacement.get()));
        }
        if (contains) return;
        lore.add(message);
    }

    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(String name) {
        return CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(name), HeadsPlus.async)
                .thenApplyAsync(player -> player, HeadsPlus.sync);
    }

    public static <T> T getValue(CompletableFuture<T> task, String object) {
        try {
            return task.get();
        } catch (InterruptedException e) {
            HeadsPlus.get().getLogger().severe("Failed to get data for " + object + ": interrupted thread. Please try" +
                    " again or restart the server. If none of the above works, please consult the necessary support" +
                    " services (e.g. hosting)..");
            e.printStackTrace();
        } catch (ExecutionException e) {
            HeadsPlus.get().getLogger().severe("Failed to get data for " + object + ": execution failed, " +
                    "an internal error occurred. Please send the console error to the developer.");
            e.printStackTrace();
        }
        return null;
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
