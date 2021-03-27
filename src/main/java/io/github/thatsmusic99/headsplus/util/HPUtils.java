package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
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
        ConfigurationSection mechanics = HeadsPlus.getInstance().getConfiguration().getMechanics();
        if (!mechanics.getBoolean("allow-looting-enchantment")) return chance;
        ConfigurationSection lootingThresholds = mechanics.getConfigurationSection("looting.thresholds");
        if (lootingThresholds == null) return chance;
        double level = 0;
        if (killer.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
            ItemStack item = killer.getInventory().getItemInHand();
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
            throw new NullPointerException("Found no heads list for " + id + "!");
        }
        if (heads.isEmpty()) return;
        EntityHead head = heads.get(random.nextInt(heads.size()));
        head.withAmount(amount);
        EntityHeadDropEvent event = new EntityHeadDropEvent(killer, head, location, EntityType.valueOf(id), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            head.getItemStackFuture().thenAccept(itemStack -> location.getWorld().dropItem(location, itemStack));
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

    public static boolean isMythicMob(Entity entity) {
        HeadsPlus hp = HeadsPlus.getInstance();
        try {
            if (hp.getConfiguration().getMechanics().getBoolean("mythicmobs.no-hp-drops")) {
                Plugin plugin = hp.getServer().getPluginManager().getPlugin("MythicMobs");
                if (plugin != null && plugin.isEnabled()) {
                    return MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId());
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @Deprecated
    public static boolean runBlacklistTests(LivingEntity e) {
        HeadsPlusMainConfig c = HeadsPlus.getInstance().getConfiguration();
        // Killer checks
        if (e.getKiller() == null) {
            if (c.getPerks().drops_needs_killer) {
                return false;
            } else if (c.getPerks().drops_entities_requiring_killer.contains(e.getName().replaceAll("_", "").toLowerCase())) {
                return false;
            } else if (e instanceof Player) {
                if (c.getPerks().drops_entities_requiring_killer.contains("player")) {
                    return false;
                }
            }
        }
        // Whitelist checks
        if (c.getWorldWhitelist().enabled) {
            if (!c.getWorldWhitelist().list.contains(e.getWorld().getName())) {
                if (e.getKiller() != null) {
                    if (!e.getKiller().hasPermission("headsplus.bypass.whitelistw")) {
                        return false;
                    }
                }
            }
        }
        // Blacklist checks
        if (c.getWorldBlacklist().enabled) {
            if (c.getWorldBlacklist().list.contains(e.getWorld().getName())) {
                if (e.getKiller() != null) {
                    if (!e.getKiller().hasPermission("headsplus.bypass.blacklistw")) {
                        return false;
                    }
                }
            }
        }
        if (e instanceof Player) {
            return !(c.getPerks().drops_ignore_players.contains(e.getUniqueId().toString())
                    || c.getPerks().drops_ignore_players.contains(e.getName()));
        } else {
            return true;
        }

    }

    public enum SkillType {
        HUNTING,
        CRAFTING
    }
}
