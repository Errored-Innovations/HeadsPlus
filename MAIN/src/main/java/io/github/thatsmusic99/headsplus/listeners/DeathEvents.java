package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DeathEvents implements Listener {

    private HashMap<String, List<String>> storedData = new HashMap<>();
    private HashMap<String, List<EntityHead>> storedHeads = new HashMap<>();

    public DeathEvents() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    NMSIndex index = HeadsPlus.getInstance().getNMSVersion();
                    if (index.getOrder() < 6) {
                        storedData.put("HORSE", Arrays.asList("getColor", "getVariant"));
                    } else {
                        storedData.put("HORSE", Collections.singletonList("getColor"));
                    }
                    storedData.put("SHEEP", Collections.singletonList("getColor"));
                    storedData.put("RABBIT", Collections.singletonList("getRabbitType"));
                    if (index.getOrder() > 7) {
                        storedData.put("LLAMA", Collections.singletonList("getColor"));
                        storedData.put("PARROT", Collections.singletonList("getVariant"));
                    }
                    if (index.getOrder() > 8) {
                        storedData.put("TROPICAL_FISH", Arrays.asList("getPattern", "getBodyColor", "getPatternColor"));
                    }
                    if (index.getOrder() > 10) {
                        storedData.put("FOX", Collections.singletonList("getFoxType"));
                        storedData.put("CAT", Collections.singletonList("getCatType"));
                        storedData.put("TRADER_LLAMA", Collections.singletonList("getColor"));
                        storedData.put("VILLAGER", Arrays.asList("getVillagerType", "getProfession"));
                        storedData.put("MUSHROOM_COW", Collections.singletonList("getVariant"));
                        storedData.put("PANDA", Collections.singletonList("getMainGene"));
                    } else {
                        storedData.put("OCELOT", Collections.singletonList("getCatType"));
                        storedData.put("VILLAGER", Collections.singletonList("getProfession"));
                    }
                    if (index.getOrder() > 11) {
                        storedData.put("BEE", Arrays.asList("getAnger", "hasNectar"));
                    }
                    storedData.put("ZOMBIE_VILLAGER", Collections.singletonList("getProfession"));
                    storedData.put("CREEPER", Collections.singletonList("isPowered"));
                    setupHeads();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HeadsPlus.getInstance());

    }

    public HashMap<String, List<EntityHead>> getStoredHeads() {
        return storedHeads;
    }

    public static final List<String> ableEntities = new ArrayList<>(Arrays.asList("BAT",
            "BLAZE",
            "BEE",
            "CAT",
            "CAVE_SPIDER",
            "CHICKEN",
            "COD",
            "COW",
            "CREEPER",
            "DOLPHIN",
            "DONKEY",
            "DROWNED",
            "ELDER_GUARDIAN",
            "ENDER_DRAGON",
            "ENDERMAN",
            "ENDERMITE",
            "EVOKER",
            "FOX",
            "GHAST",
            "GUARDIAN",
            "HORSE",
            "HUSK",
            "IRON_GOLEM",
            "LLAMA",
            "MAGMA_CUBE",
            "MULE",
            "MUSHROOM_COW",
            "OCELOT",
            "PANDA",
            "PARROT",
            "PHANTOM",
            "PIG",
            "PIG_ZOMBIE",
            "PILLAGER",
            "POLAR_BEAR",
            "PUFFERFISH",
            "RABBIT",
            "RAVAGER",
            "SALMON",
            "SHEEP",
            "SHULKER",
            "SILVERFISH",
            "SKELETON",
            "SKELETON_HORSE",
            "SLIME",
            "SNOWMAN",
            "SPIDER",
            "SQUID",
            "STRAY",
            "TRADER_LLAMA",
            "TROPICAL_FISH",
            "TURTLE",
            "VEX",
            "VILLAGER",
            "VINDICATOR",
            "WANDERING_TRADER",
            "WITCH",
            "WITHER",
            "WITHER_SKELETON",
            "WOLF",
            "ZOMBIE",
            "ZOMBIE_HORSE",
            "ZOMBIE_VILLAGER"));
    private final HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
    public static boolean ready = false;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        final HeadsPlus hp = HeadsPlus.getInstance();
        if (!hp.isDropsEnabled() || checkForMythicMob(hp, e.getEntity())) {
            return;
        }
        if (!runBlacklistTests(e.getEntity())) return;
        try {
            String entity = e.getEntityType().toString().toLowerCase().replaceAll("_", "");
            Random rand = new Random();
            double fixedChance = hpch.getConfig().getDouble(entity + ".chance");
            double randChance = rand.nextDouble() * 100;
            int amount = 1;
            Player killer = e.getEntity().getKiller();
            if (killer != null) {
                ItemStack handItem = hp.getNMS().getItemInHand(e.getEntity().getKiller());
                ConfigurationSection mechanics = hp.getConfiguration().getMechanics();
                if (handItem != null) {
                    if (handItem.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                            && mechanics.getBoolean("allow-looting-enchantment")
                            && !(mechanics.getStringList("looting.ignored-entities").contains(entity))) {
                        if (mechanics.getBoolean("looting.use-old-system")) {
                            amount += handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1;
                        } else {
                            fixedChance *= handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1;
                            amount = ((int) fixedChance / 100) == 0 ? 1 : (int) (fixedChance / 100);
                        }

                    }
                }

            }
            if (fixedChance == 0.0) {
                return;
            }
            if (randChance <= fixedChance) {
                dropHead(e.getEntity(), e.getEntity().getKiller(), amount);
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (DeathEvents)", false, null);
        }
    }

    @EventHandler
	public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent ep) {
	    try {

            HeadsPlus hp = HeadsPlus.getInstance();
            if (!hp.isDropsEnabled()) return;
            Player victim = ep.getEntity();
            Player killer = ep.getEntity().getKiller();
            HeadsPlusMainConfig c = hp.getConfiguration();
            if (runBlacklistTests(victim)) {
                Random rand = new Random();
                double fixedChance = hpch.getConfig().getDouble("player.chance");
                double randChance = rand.nextDouble() * 100;
                NMSManager nms = hp.getNMS();
                int amount = 1;
                if (killer != null) {
                    if (nms.getItemInHand(killer).containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                            && c.getMechanics().getBoolean("allow-looting-enchantment")
                            && !(c.getMechanics().getStringList("looting.ignored-entities").contains("player"))) {
                        if (c.getMechanics().getBoolean("looting.use-old-system")) {
                            amount = HeadsPlus.getInstance().getNMS().getItemInHand(killer).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                        } else {
                            fixedChance *= HeadsPlus.getInstance().getNMS().getItemInHand(killer).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                            amount = ((int) fixedChance / 100) == 0 ? 1 : (int) (fixedChance / 100);
                        }
                    }
                }

                if (fixedChance == 0.0) return;
                if (randChance <= fixedChance) {
                    double lostprice = 0.0;
                    double price = 0.0;
                    Economy economy = HeadsPlus.getInstance().getEconomy();
                    if (hp.getConfiguration().getPerks().pvp_player_balance_competition
                            && killer != null
                            && economy.getBalance(killer) > 0.0) {
                        double playerprice = economy.getBalance(killer);
                        price = playerprice * hp.getConfiguration().getPerks().pvp_balance_for_head;
                        lostprice = economy.getBalance(killer) * hp.getConfiguration().getPerks().pvp_percentage_lost;
                    }
                    Head head = new Head("player").withAmount(amount)
                            .withDisplayName(ChatColor.RESET + hpch.getDisplayName("player").replace("{player}", ep.getEntity().getName()))
                            .withPlayerName(victim.getName())
                            .withPrice(price)
                            .withLore(hpch.getLore(victim.getName(), price));
                    Location location = victim.getLocation();
                    EntityHeadDropEvent event = new EntityHeadDropEvent(killer, head, location, EntityType.PLAYER);
                    if (!event.isCancelled()) {
                        location.getWorld().dropItem(location, head.getItemStack());
                        if (lostprice > 0.0) {
                            economy.withdrawPlayer(ep.getEntity(), lostprice);
                            victim.sendMessage(hp.getMessagesConfig().getString("event.lost-money", ep.getEntity())
                                    .replace("{player}", killer.getName())
                                    .replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price))));
                        }
                    }
                }
            }
        } catch (Exception e) {
	        DebugPrint.createReport(e, "Event (DeathEvents)", false, null);
        }
	}

	private void setupHeads() {
	    for (String name : ableEntities) {
	        try {
                HeadsPlus.getInstance().debug("Creating head for " + name + "...", 3);
                String fancyName = name.toLowerCase().replaceAll("_", "");
                HeadsPlusConfigHeads headsCon = HeadsPlus.getInstance().getHeadsConfig();
                if (headsCon.getConfig().get(fancyName + ".name") instanceof ConfigurationSection) {
                    for (String conditions : ((ConfigurationSection) headsCon.getConfig().get(fancyName + ".name")).getKeys(false)) {
                        List<EntityHead> heads = new ArrayList<>();
                        for (String head : headsCon.getConfig().getStringList(fancyName + ".name." + conditions)) {
                            EntityHead headItem;
                            if (head.equalsIgnoreCase("{mob-default}") && fancyName.equalsIgnoreCase("creeper")) {
                                headItem = new EntityHead(fancyName, 4);
                            } else {
                                headItem = new EntityHead(fancyName);
                            }
                            headItem.withDisplayName(headsCon.getDisplayName(fancyName))
                                    .withPrice(headsCon.getPrice(fancyName))
                                    .withLore(headsCon.getLore(fancyName));
                            if (head.startsWith("HP#")) {
                                headItem.withTexture(HeadsPlus.getInstance().getHeadsXConfig().getTextures(head));
                            } else {
                                headItem.withPlayerName(head);
                            }
                            heads.add(headItem);
                        }
                        storedHeads.put(name + ";" + conditions, heads);
                    }
                } else {
                    List<EntityHead> heads = new ArrayList<>();
                    for (String head : headsCon.getConfig().getStringList(fancyName + ".name")) {
                        EntityHead headItem;
                        if (head.equalsIgnoreCase("{mob-default}")) {
                            switch (fancyName) {
                                case "witherskeleton":
                                    headItem = new EntityHead(fancyName, 1);
                                    break;
                                case "enderdragon":
                                    headItem = new EntityHead(fancyName, 5);
                                    break;
                                case "zombie":
                                    headItem = new EntityHead(fancyName, 2);
                                    break;
                                case "creeper":
                                    headItem = new EntityHead(fancyName, 4);
                                    break;
                                case "skeleton":
                                    headItem = new EntityHead(fancyName, 0);
                                    break;
                                default:
                                    headItem = new EntityHead(fancyName);
                                    break;
                            }
                        } else {
                            headItem = new EntityHead(fancyName);
                        }
                        headItem.withDisplayName(headsCon.getDisplayName(fancyName))
                                .withPrice(headsCon.getPrice(fancyName))
                                .withLore(headsCon.getLore(fancyName));
                        if (head.startsWith("HP#")) {
                            headItem.withTexture(HeadsPlus.getInstance().getHeadsXConfig().getTextures(head));
                        } else {
                            headItem.withPlayerName(head);
                        }
                        heads.add(headItem);
                    }
                    storedHeads.put(name + ";default", heads);
                }
            } catch (Exception e) {
                HeadsPlus.getInstance().getLogger().severe("Error thrown when creating the head for " + name + ". If it's a custom head, please double check the name.");
                e.printStackTrace();
            }
        }
        ready = true;
    }

    private void dropHead(Entity entity, Player killer, int amount) {
        Random random = new Random();
        String name = entity.getType().name();
        List<EntityHead> heads = null;
        try {
            if (storedData.get(name) != null) {
                List<String> possibleConditions = new ArrayList<>();
                for (String methods : storedData.get(name)) {
                    if (entity.getType() == EntityType.CREEPER) {
                        boolean powered = Boolean.parseBoolean(callMethod(methods, entity));
                        heads = storedHeads.get(name + ";" + (powered ? "POWERED" : "default"));
                        break;
                    } else if (entity.getType() == EntityType.valueOf("BEE")) {
                        if (methods.equalsIgnoreCase("hasNectar")) {
                            possibleConditions.add(callMethod(methods, entity).equalsIgnoreCase("true") ? "NECTAR" : "default");
                        } else {
                            possibleConditions.add(Integer.parseInt(callMethod(methods, entity)) > 0 ? "ANGRY" : "default");
                        }
                    } else {
                        possibleConditions.add(callMethod(methods, entity));
                    }
                }
                if (possibleConditions.contains("default") && possibleConditions.size() == 1) {
                    heads = storedHeads.get(name + ";default");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String s : possibleConditions) {
                        if (storedHeads.containsKey(name + ";" + s)) {
                            heads = storedHeads.get(name + ";" + s);
                            break;
                        }
                        sb.append(s).append(",");
                    }
                    if (heads == null) {
                        sb.replace(sb.length() - 1, sb.length(), "");
                        if (storedHeads.containsKey(name + ";" + sb.toString()) && !storedHeads.get(name + ";" + sb.toString()).isEmpty()) {
                            heads = storedHeads.get(name + ";" + sb.toString());
                        } else {
                            heads = storedHeads.get(name + ";default");
                        }
                    }

                }
            } else {
                heads = storedHeads.get(entity.getType().name() + ";default");
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        if (heads.isEmpty()) return;
        EntityHead head = heads.get(random.nextInt(heads.size()));
        head.withAmount(amount);
        Location location = entity.getLocation();
        EntityHeadDropEvent event = new EntityHeadDropEvent(killer, head, location, entity.getType());
        if (!event.isCancelled()) {
            location.getWorld().dropItem(location, head.getItemStack());
        }

    }

    private String callMethod(String methodName, Entity entity) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = entity.getClass().getMethod(methodName);
        return String.valueOf(method.invoke(entity));
    }


    private boolean checkForMythicMob(HeadsPlus hp, Entity e) {
        try {
            if (hp.getConfiguration().getMechanics().getBoolean("mythicmobs.no-hp-drops")) {
                if (hp.getServer().getPluginManager().getPlugin("MythicMobs") != null) {
                    return MythicMobs.inst().getMobManager().isActiveMob(e.getUniqueId());
                }
            }
        } catch (NoClassDefFoundError ignored) {

        }


        return false;
    }

    public String prettyStringToUglyString(String s) {
	    for (String name : ableEntities) {
	        if (name.replaceAll("_", "").equalsIgnoreCase(s)) {
	            return name;
            }
        }
        return null;
    }

    public void reload() {
	    storedHeads = new HashMap<>();
        setupHeads();
    }

    private boolean runBlacklistTests(LivingEntity e) {
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
            return ableEntities.contains(e.getType().name());
        }

    }
}