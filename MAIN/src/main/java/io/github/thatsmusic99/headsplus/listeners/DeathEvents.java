package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathEvents implements Listener {

    public DeathEvents() {
        new BukkitRunnable() {
            @Override
            public void run() {
                NMSIndex index = HeadsPlus.getInstance().getNMSVersion();
                Class[] horseData;
                if (index.getOrder() < 6) {
                    horseData = new Class[]{Horse.Color.class, Horse.Variant.class};
                } else {
                    horseData = new Class[]{Horse.Color.class};
                }
                coloredData.put("HORSE", horseData);
                coloredData.put("SHEEP", new Class[]{DyeColor.class});
                coloredData.put("WOLF", new Class[]{DyeColor.class});
                coloredData.put("RABBIT", new Class[]{Rabbit.Type.class});
                if (index.getOrder() > 7) {
                    coloredData.put("LLAMA", new Class[]{Llama.Color.class});
                    coloredData.put("PARROT", new Class[]{Parrot.Variant.class});
                }
                if (index.getOrder() > 8) {
                    coloredData.put("TROPICAL_FISH", new Class[]{TropicalFish.Pattern.class});
                }
                if (index.getOrder() > 10) {
                    coloredData.put("FOX", new Class[]{Fox.Type.class});
                    coloredData.put("CAT", new Class[]{Cat.Type.class});
                    coloredData.put("TRADER_LLAMA", new Class[]{TraderLlama.Color.class});
                    coloredData.put("VILLAGER", new Class[]{Villager.Profession.class, Villager.Type.class});
                    coloredData.put("MUSHROOM_COW", new Class[]{MushroomCow.Variant.class});
                } else {
                    coloredData.put("OCELOT", new Class[]{Ocelot.Type.class});
                    coloredData.put("VILLAGER", new Class[]{Villager.Profession.class});
                }
                coloredData.put("ZOMBIE_VILLAGER", new Class[]{Villager.Profession.class});
                setupHeads();
            }
        }.runTaskAsynchronously(HeadsPlus.getInstance());

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
    private final HeadsPlusConfigCustomHeads hpchx = HeadsPlus.getInstance().getHeadsXConfig();
    private final HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
    public static HashMap<String, HashMap<String, List<ItemStack>>> heads = new HashMap<>();
    private static HashMap<String, Class[]> coloredData = new HashMap<>();
    public static boolean ready = false;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        final HeadsPlus hp = HeadsPlus.getInstance();
        if (!hp.isDropsEnabled() || checkForMythicMob(hp, e.getEntity())) {
            return;
        }
        if (runAcceptTests(e.getEntity())) {
            try {
                String entity = e.getEntityType().toString().toLowerCase().replaceAll("_", "");
                Random rand = new Random();
                double chance1 = hpch.getConfig().getDouble(entity + ".chance");
                double chance2 = rand.nextDouble() * 100;
                int amount = 1;
                if (e.getEntity().getKiller() != null) {
                    if (hp.getNMS().getItemInHand(e.getEntity().getKiller()).containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                            && hp.getConfiguration().getMechanics().getBoolean("allow-looting-enchantment")
                            && !(hp.getConfiguration().getMechanics().getStringList("looting.ignored-entities").contains(e.getEntityType().name().replaceAll("_", "").toLowerCase()))) {
                        if (hp.getConfiguration().getMechanics().getBoolean("looting.use-old-system")) {
                            amount += hp.getNMS().getItemInHand(e.getEntity().getKiller()).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1;
                        } else {
                            chance1 *= (hp.getNMS().getItemInHand(e.getEntity().getKiller()).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1);
                            amount = ((int) chance1 / 100) == 0 ? 1 : (int) (chance1 / 100);
                        }

                    }
                }
                if (chance1 == 0.0) {
                    return;
                }
                if (chance2 <= chance1) {

                    if (entity.equalsIgnoreCase("sheep")
                            || entity.equalsIgnoreCase("parrot")
                            || entity.equalsIgnoreCase("horse")
                            || entity.equalsIgnoreCase("llama")) {
                        dropHead(e.getEntity(), e.getEntity().getKiller(), amount);
                    } else {
                        if (hpch.getConfig().getStringList(entity + ".name").isEmpty()) {
                            return;
                        }
                        dropHead(e.getEntity(), e.getEntity().getKiller(), amount);
                    }
                }
            } catch (Exception ex) {
                DebugPrint.createReport(ex, "Event (DeathEvents)", false, null);
            }
        }
    }

    @EventHandler
	public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent ep) {
	    try {

            HeadsPlus hp = HeadsPlus.getInstance();
            if (!hp.isDropsEnabled()) return;
            HeadsPlusMainConfig c = hp.getConfiguration();
            if (runAcceptTests(ep.getEntity())) {
                Random rand = new Random();
                double chance1 = hpch.getConfig().getDouble("player.chance");
                double chance2 = rand.nextDouble() * 100;
                NMSManager nms = hp.getNMS();
                int a = 1;
                if (ep.getEntity().getKiller() != null) {
                    if (nms.getItemInHand(ep.getEntity().getKiller()).containsEnchantment(Enchantment.LOOT_BONUS_MOBS)
                            && c.getMechanics().getBoolean("allow-looting-enchantment")
                            && !(c.getMechanics().getStringList("looting.ignored-entities").contains("player"))) {
                        if (c.getMechanics().getBoolean("looting.use-old-system")) {
                            a = HeadsPlus.getInstance().getNMS().getItemInHand(ep.getEntity().getKiller()).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                        } else {
                            chance1 *= HeadsPlus.getInstance().getNMS().getItemInHand(ep.getEntity().getKiller()).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                            a = ((int) chance1 / 100) == 0 ? 1 : (int) (chance1 / 100);
                        }
                    }
                }

                if (chance1 == 0.0) return;
                if (chance2 <= chance1) {


                    ItemStack head = nms.getSkullMaterial(a);
                    SkullMeta headM = (SkullMeta) head.getItemMeta();
                    headM = nms.setSkullOwner(ep.getEntity().getName(), headM);
                    headM.setDisplayName(ChatColor.RESET + hpch.getDisplayName("player").replace("{player}", ep.getEntity().getName()));


                    Location entityLoc = ep.getEntity().getLocation();
                    double entityLocY = entityLoc.getY() + 1;
                    entityLoc.setY(entityLocY);
                    World world = ep.getEntity().getWorld();

                    double price = hpch.getPrice("player");
                    boolean b = hp.getConfiguration().getPerks().pvp_player_balance_competition;
                    double lostprice = 0.0;
                    if (b && HeadsPlus.getInstance().getEconomy().getBalance(ep.getEntity()) > 0.0) {
                        double playerprice = HeadsPlus.getInstance().getEconomy().getBalance(ep.getEntity());
                        price = playerprice * hp.getConfiguration().getPerks().pvp_balance_for_head;
                        lostprice = HeadsPlus.getInstance().getEconomy().getBalance(ep.getEntity()) * hp.getConfiguration().getPerks().pvp_percentage_lost;
                    }

                    List<String> strs = new ArrayList<>();
                    for (String str : hpch.getLore("player")) {
                        strs.add(ChatColor.translateAlternateColorCodes('&', str
								.replace("{player}", ep.getEntity().getName())
								.replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price)))));
                    }
                    headM.setLore(strs);
                    head.setItemMeta(headM);
                    NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
                    head = nbt.makeSellable(head);
                    head = nbt.setType(head, "player");
                    head = nbt.setPrice(head, price);
                    PlayerHeadDropEvent event = new PlayerHeadDropEvent(ep.getEntity(), ep.getEntity().getKiller(), head, world, entityLoc);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (b && ep.getEntity().getKiller() != null) {
                            hp.getEconomy().withdrawPlayer(ep.getEntity(), lostprice);
                            ep.getEntity().sendMessage(hp.getMessagesConfig().getString("event.lost-money")
									.replace("{player}", ep.getEntity().getKiller().getName())
									.replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price))));
                        }
                        world.dropItem(event.getLocation(), event.getSkull());
                    }
                }
            }
        } catch (Exception e) {
	        DebugPrint.createReport(e, "Event (DeathEvents)", false, null);
        }
	}

	private void setupHeads() {
	    NMSManager nms = HeadsPlus.getInstance().getNMS();
	    for (String name : ableEntities) {
	        try {
                HeadsPlus.getInstance().debug("Creating head for " + name + "...", 3);
                HashMap<String, List<ItemStack>> keys = new HashMap<>();
                List<ItemStack> heads = new ArrayList<>();
                if (name.equals("SHEEP")) {
                    a("sheep", keys);
                    DeathEvents.heads.put(name, keys);
                    continue;
                }
                if (HeadsPlus.getInstance().getNMSVersion().getOrder() > 7) {
                    if (name.equals("LLAMA")) {
                        a("llama", keys);
                        DeathEvents.heads.put(name, keys);
                        continue;
                    }
                    if (name.equals("PARROT")) {
                        a("parrot", keys);
                        DeathEvents.heads.put(name, keys);
                        continue;
                    }
                }
                if (name.equals("HORSE")) {
                    a("horse", keys);
                    DeathEvents.heads.put(name, keys);
                    continue;
                }
                String fancyName = name.toLowerCase().replaceAll("_", "");
                for (String name2 : hpch.getConfig().getStringList(fancyName + ".name")) {
                    ItemStack is;
                    boolean b = true;
                    if (hpchx.isHPXSkull(name2)) {
                        is = hpchx.getSkull(name2);
                    } else if (name2.equalsIgnoreCase("{mob-default}")) {
                        try {
                            if (name.equals("ENDER_DRAGON")
                                    || name.equals("WITHER_SKELETON")
                                    || name.equals("CREEPER")
                                    || name.equals("ZOMBIE")
                                    || name.equals("SKELETON")) {
                                is = new ItemStack(Material.BLAZE_ROD);
                                double price = hpch.getPrice(fancyName);
                                ItemMeta sm = is.getItemMeta();
                                sm.setDisplayName(ChatColor.RESET + hpch.getDisplayName(fancyName));
                                List<String> strs = new ArrayList<>();
                                List<String> lore = hpch.getLore(fancyName);
                                for (String str2 : lore) {
                                    strs.add(ChatColor.translateAlternateColorCodes('&', str2
                                            .replace("{type}", fancyName)
                                            .replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price)))));
                                }
                                sm.setLore(strs);
                                is.setItemMeta(sm);
                                NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
                                is = nbt.makeSellable(is);
                                is = nbt.setType(is, fancyName);
                                is = nbt.setPrice(is, price);
                                switch (name) {
                                    case "ENDER_DRAGON":
                                        is.setType(nms.getSkull(5).getType());
                                        break;
                                    case "ZOMBIE":
                                        is.setType(nms.getSkull(2).getType());
                                        break;
                                    case "CREEPER":
                                        is.setType(nms.getSkull(4).getType());
                                        break;
                                    case "SKELETON":
                                        is.setType(nms.getSkull(0).getType());
                                        break;
                                    default:
                                        is.setType(nms.getSkull(1).getType());
                                        break;
                                }

                                b = false;
                            } else {
                                is = nms.getSkull(3);
                            }
                        } catch (NoSuchFieldError ex) {
                            HeadsPlus.getInstance().getLogger().warning("Error thrown when trying to add a mob-default head. Setting to player skull...");
                            is = nms.getSkull(3);
                        }
                    } else {
                        is = nms.getSkullMaterial(1);
                        SkullMeta sm = (SkullMeta) is.getItemMeta();
                        sm = nms.setSkullOwner(name2, sm);
                        is.setItemMeta(sm);
                    }
                    if (b) {
                        double price = hpch.getPrice(fancyName);
                        SkullMeta sm = (SkullMeta) is.getItemMeta();
                        sm.setDisplayName(ChatColor.RESET + hpch.getDisplayName(fancyName));
                        List<String> strs = new ArrayList<>();
                        List<String> lore = hpch.getLore(fancyName);
                        for (String str2 : lore) {
                            strs.add(ChatColor.translateAlternateColorCodes('&', str2
                                    .replace("{type}", fancyName)
                                    .replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price)))));
                        }
                        sm.setLore(strs);
                        is.setItemMeta(sm);
                        NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
                        is = nbt.makeSellable(is);
                        is = nbt.setType(is, fancyName);
                        is = nbt.setPrice(is, price);
                    }
                    heads.add(is);
                }
                keys.put("default", heads);
                DeathEvents.heads.put(name, keys);
            } catch (Exception ex) {
	            HeadsPlus.getInstance().getLogger().severe("Error thrown when creating the head for " + name + ". If it's a custom head, please double check the name.");
	            ex.printStackTrace();
            }

        }
        ready = true;
    }

    private HashMap<String, List<ItemStack>> a(String en,  HashMap<String, List<ItemStack>> keys) {

        for (String str : hpch.getConfig().getConfigurationSection(en + ".name").getKeys(false)) {
            List<ItemStack> heads = new ArrayList<>();
            for (String name : hpch.getConfig().getStringList(en + ".name." + str)) {
                ItemStack is = null;
                NMSManager nms = HeadsPlus.getInstance().getNMS();
                if (HeadsPlus.getInstance().getHeadsXConfig().isHPXSkull(name)) {
                    try {
                        is = HeadsPlus.getInstance().getHeadsXConfig().getSkull(name);
                    } catch (NullPointerException ex) {
                        HeadsPlus.getInstance().getLogger().warning("WARNING: NPE thrown at " + str + ", " + name + ". If this is light_gray, please change HP#light_gray_sheep to HP#silver_sheep. If not, make sure your HP# head is valid.");
                    }
                }  else {
                    is = nms.getSkullMaterial(1);
                    SkullMeta sm = (SkullMeta) is.getItemMeta();
                    sm = nms.setSkullOwner(name, sm);
                    is.setItemMeta(sm);
                }


                double price = hpch.getPrice(en);
                SkullMeta sm = (SkullMeta) is.getItemMeta();
                sm.setDisplayName(ChatColor.RESET + hpch.getDisplayName(en));
                List<String> strs = new ArrayList<>();
                List<String> lore = hpch.getLore(en);
                for (String str2 : lore) {
                    strs.add(ChatColor.translateAlternateColorCodes('&', str2
							.replace("{type}", en)
							.replaceAll("\\{price}", String.valueOf(HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price)))));
                }
                sm.setLore(strs);
                is.setItemMeta(sm);
                NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
                is = nbt.makeSellable(is);
                is = nbt.setType(is, en);
                is = nbt.setPrice(is, price);
                heads.add(is);

            }
            keys.put(str, heads);
        }
        return keys;
    }

	private List<ItemStack> hasColor(Entity e) {
        if (e instanceof Sheep) {
            Sheep sheep = (Sheep) e;
            DyeColor dc = sheep.getColor();
            for (String str : hpch.getConfig().getConfigurationSection("sheep.name").getKeys(false)) {
                if (!str.equalsIgnoreCase("default")) {
                    if (dc.equals(DyeColor.valueOf(str))) {
                        return heads.get(e.getType().name()).get(str);
                    }
                }
            }
        }
        if (HeadsPlus.getInstance().getNMSVersion().getOrder() > 7) {
            if (e instanceof Llama) {
                Llama llama = (Llama) e;
                Llama.Color color = llama.getColor();
                for (String str : hpch.getConfig().getConfigurationSection("llama.name").getKeys(false)) {
                    if (!str.equalsIgnoreCase("default")) {
                        if (color.equals(Llama.Color.valueOf(str))) {
                            return heads.get(e.getType().name()).get(str);
                        }
                    }
                }
            } else if (e instanceof Parrot) {
                Parrot parrot = (Parrot) e;
                Parrot.Variant va = parrot.getVariant();
                for (String str : hpch.getConfig().getConfigurationSection("parrot.name").getKeys(false)) {
                    if (!str.equalsIgnoreCase("default")) {
                        if (va.equals(Parrot.Variant.valueOf(str))) {
                            return heads.get(e.getType().name()).get(str);
                        }
                    }
                }
            }
        }
         if (e instanceof Horse) {
            Horse horse = (Horse) e;
            Horse.Color c = horse.getColor();
            for (String str : hpch.getConfig().getConfigurationSection("horse.name").getKeys(false)) {
                if (!str.equalsIgnoreCase("default")) {
                    if (c.equals(Horse.Color.valueOf(str))) {
                        return heads.get(e.getType().name()).get(str);
                    }
                }
            }
        }
        return null;
    }

    private void dropHead(Entity e, Player k, int a) {
	    Random r = new Random();
	    int thing;
	    ItemStack i;
	    List<ItemStack> af = hasColor(e);
	    String name = e.getType().name();

	    String mobName = e.getType().name().replaceAll("_", "").toLowerCase();
	    try {
            if (af != null && !af.isEmpty()) {
                thing = r.nextInt(af.size());
                i = af.get(thing);
            } else {
                if (heads.get(name) == null) return;
                if (heads.get(name).get("default") == null) return;
                if (heads.get(name).get("default").size() < 1) return;
                if (e instanceof Sheep) {
                    thing = r.nextInt(hpch.getConfig().getStringList("sheep.name.default").size());
                } else if (e instanceof Horse) {
                    thing = r.nextInt(hpch.getConfig().getStringList("horse.name.default").size());
                } else if (HeadsPlus.getInstance().getNMSVersion().getOrder() > 7) {
                    if (e instanceof Parrot) {
                        thing = r.nextInt(hpch.getConfig().getStringList("parrot.name.default").size());
                    } else if (e instanceof Llama) {
                        thing = r.nextInt(hpch.getConfig().getStringList("llama.name.default").size());
                    } else {
                        thing = r.nextInt(hpch.getConfig().getStringList(mobName + ".name").size());
                    }
                } else {
                    thing = r.nextInt(hpch.getConfig().getStringList(mobName + ".name").size());
                }
                i = heads.get(name).get("default").get(thing);
            }
        } catch (IndexOutOfBoundsException ex) {
	        return;
        }
        i.setAmount(a);
        Location entityLoc = e.getLocation();
        double entityLocY = entityLoc.getY() + 1;
        entityLoc.setY(entityLocY);
        World world = e.getWorld();
        EntityHeadDropEvent event = new EntityHeadDropEvent(k, i, world, entityLoc, e.getType());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            world.dropItem(event.getLocation(), event.getSkull());
            if (k != null) {
                HPPlayer.getHPPlayer(k).addXp(10);
            }
        }
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
	    heads = new HashMap<>();
        setupHeads();
    }

    private boolean runAcceptTests(LivingEntity e) {
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