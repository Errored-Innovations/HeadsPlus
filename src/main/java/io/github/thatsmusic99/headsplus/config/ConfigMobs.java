package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Rabbit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigMobs extends CMFile {
	public final List<String> eHeads = new ArrayList<>(Arrays.asList("apple", "cake", "chest", "cactus", "melon", "pumpkin"));
	public final List<String> ieHeads = new ArrayList<>(Arrays.asList("coconutB", "coconutG", "oaklog", "present1", "present2", "tnt", "tnt2", "arrowUp", "arrowDown", "arrowQuestion", "arrowLeft", "arrowRight", "arrowExclamation"));

	public ConfigMobs() {
	    super(HeadsPlus.getInstance(), "mobs");
    }

	@Override
	public void loadDefaults() {
		addDefault("defaults.price", 10.0);
		addDefault("defaults.lore", new ArrayList<>(Arrays.asList("&7Price &8» &c{price}", "&7Type &8» &c{type}")));
		addDefault("defaults.display-name", "{type} Head");
		addDefault("defaults.interact-name", "{type}");
		addDefault("defaults.chance", 5);
		addHeads();
		addPlayerHeads();
	}

	@Override
	public void moveToNew() {
		for (String key : getConfig().getKeys(false)) {
			if (!(getConfig().get(key) instanceof ConfigurationSection)) continue;
			ConfigurationSection section = getConfig().getConfigurationSection(key);
			if (section == null || !section.contains("interact-name") || !section.contains("name") || !(section.get("name") instanceof ConfigurationSection)) continue;
			ConfigurationSection name = section.getConfigurationSection("name");
			if (name == null) continue;
			for (String nameKey : name.getKeys(false)) {
				if (name.get(nameKey) instanceof List) {
					for (String actualName : name.getStringList(nameKey)) {
						if (actualName.startsWith("HP#")) {
							moveTo(key + ".interact-name", "special.textures." + actualName + ".name", ConfigInteractions.get());
						} else {
							moveTo(key + ".interact-name", "special.names." + actualName + ".name", ConfigInteractions.get());
						}
					}
				}
			}
		}

		for (String head : Arrays.asList("brownCoconutHead", "greenCoconutHead", "oakLogHead", "present1Head",
				"present2Head", "tntHead", "tnt2Head", "arrowUpHead", "arrowDownHead", "arrowRightHead", "arrowLeftHead",
				"excalamationHead", "questionHead")) {
			moveTo(head + "EN", "special.names." + getConfig().getString(head + "N") + ".name", ConfigInteractions.get());
			set(head + "N", null);
		}

		for (String entity : Arrays.asList("blaze", "bee", "cat", "chicken", "cod", "cow", "creeper", "dolphin", "donkey", "drowned",
				"enderman", "endermite", "evoker", "fox", "ghast", "giant", "guardian", "hoglin", "horse", "husk", "llama", "mule",
				"ocelot", "panda", "parrot", "phantom", "pig", "piglin", "pillager", "pufferfish", "rabbit", "ravager", "salmon", "sheep",
				"shulker", "silverfish", "skeleton", "slime", "snowman", "spider", "squid", "stray", "strider", "turtle", "vex", "villager",
				"vindicator", "witch", "wither", "wolf", "zoglin", "zombie")) {
			moveTo(entity, entity.toUpperCase());
		}

		for (String entity : Arrays.asList("CAVE_SPIDER", "ELDER_GUARDIAN", "ENDER_DRAGON", "IRON_GOLEM", "MAGMA_CUBE",
				"MUSHROOM_COW", "PIGLIN_BRUTE", "PIG_ZOMBIE", "POLAR_BEAR", "SKELETON_HORSE", "TROPICAL_FISH", "WITHER_SKELETON",
				"ZOMBIE_HORSE", "ZOMBIE_VILLAGER", "ZOMBIFIED_PIGLIN")) {
			moveTo(entity.toLowerCase().replaceAll("_", ""), entity);
		}

		moveTo("trader_llama", "TRADER_LLAMA");
		moveTo("wandering_trader", "WANDERING_TRADER");
	}

	private void addHeads() {
    	for (String key : EntityDataManager.ableEntities) {
            switch (key) {
				case "BEE":
					addDefault(key + ".name.default", initSingleton("HP#bee_mc"));
					addDefault(key + ".name.ANGRY", initSingleton("HP#bee_angry"));
					addDefault(key + ".name.ANGRY,NECTAR", initSingleton("HP#bee_pollinated_angry"));
					addDefault(key + ".name.NECTAR", initSingleton("HP#bee_pollinated"));
					break;
				case "CREEPER":
					addDefault(key + ".name.default", initSingleton("{mob-default}"));
					addDefault(key + ".name.POWERED", initSingleton("HP#charged_creeper"));
					break;
				case "CAT":
					addDefault(key + ".name.default", initSingleton("HP#tabby_cat"));
					for (String type : Arrays.asList("tabby", "black", "red", "siamese", "british_shorthair", "calico", "persian", "ragdoll", "white", "jellie", "all_black")) {
						addDefault(key + ".name." + type.toUpperCase(), initSingleton("HP#" + type + "_cat"));
					}
					break;
				case "FOX":
					addDefault(key + ".name.default", initSingleton("HP#fox_mc"));
					addDefault(key + ".name.RED", initSingleton("HP#fox_mc"));
					addDefault(key + ".name.SNOW", initSingleton("HP#snow_fox"));
					break;
				case "HORSE":
					addDefault(key + ".name.default", initSingleton("HP#brown_horse"));
					for (Horse.Color variant : Horse.Color.values()) {
						addDefault(key + ".name." + variant.name(), initSingleton("HP#" + variant.name().toLowerCase() + "_horse"));
					}
					break;
                case "LLAMA":
                case "TRADER_LLAMA":
					addDefault(key + ".name.default", initSingleton("HP#creamy_" + key.toLowerCase()));
                	addDefault(key + ".name.CREAMY", initSingleton("HP#creamy_" + key.toLowerCase()));
					addDefault(key + ".name.WHITE", initSingleton("HP#white_" + key.toLowerCase()));
					addDefault(key + ".name.BROWN", initSingleton("HP#brown_" + key.toLowerCase()));
					addDefault(key + ".name.GRAY", initSingleton("HP#gray_" + key.toLowerCase()));
                    break;
				case "MUSHROOM_COW":
					addDefault(key + ".name.default", initSingleton("HP#red_mooshroom"));
					addDefault(key + ".name.RED", initSingleton("HP#red_mooshroom"));
					addDefault(key + ".name.BROWN", initSingleton("HP#brown_mooshroom"));
					break;
				case "OCELOT":
					addDefault( key + ".name.default", initSingleton("HP#ocelot"));
					addDefault(key + ".name.WILD_OCELOT", initSingleton("HP#ocelot"));
					addDefault(key + ".name.BLACK_CAT", initSingleton("HP#black_cat"));
					addDefault(key + ".name.RED_CAT", initSingleton("HP#red_cat"));
					addDefault(key + ".name.SIAMESE_CAT", initSingleton("HP#siamese_cat"));
					break;
				case "PANDA":
					for (String gene : Arrays.asList("NORMAL", "PLAYFUL", "LAZY", "WORRIED", "BROWN", "WEAK", "AGGRESSIVE")) {
						addDefault(key + ".name." + gene, initSingleton("HP#panda_" + gene.toLowerCase()));
					}
					break;
                case "PARROT":
                	addDefault(key + ".name.default", initSingleton("HP#red_parrot"));
                	for (String colour : new String[]{"RED", "BLUE", "GREEN", "CYAN", "GRAY"}) {
                		addDefault(key + ".name." + colour, initSingleton("HP#" + colour.toLowerCase() + "_parrot"));
					}
                    break;
				case "RABBIT":
					addDefault(key + ".name.default", initSingleton("HP#brown_rabbit"));
					for (Rabbit.Type type : Rabbit.Type.values()) {
						addDefault(key + ".name." + type.name(), initSingleton("HP#" + type.name().toLowerCase() + "_rabbit"));
					}
					break;
				case "SHEEP":
					addDefault(key + ".name.default", initSingleton("HP#white_sheep"));
					for (DyeColor dc : DyeColor.values()) {
						try {
							if (dc == DyeColor.valueOf("LIGHT_GRAY")) {
								addDefault(key + ".name." + dc.name(), initSingleton("HP#silver_sheep"));
							} else {
								addDefault(key + ".name." + dc.name(), initSingleton("HP#" + dc.name().toLowerCase() + "_sheep"));
							}
						} catch (NoSuchFieldError | IllegalArgumentException ex) {
							addDefault(key + ".name." + dc.name(), initSingleton("HP#silver_sheep"));
						}
					}
					break;
				case "TROPICAL_FISH":
				case "PIG_ZOMBIE":
				case "ELDER_GUARDIAN":
				case "ZOMBIE_HORSE":
				case "SKELETON_HORSE":
					addDefault(key + ".name.default", initSingleton("HP#" + key.toLowerCase().replaceAll("_", "")));
					break;
				case "VILLAGER":
					addDefault(key + ".name.default", initSingleton("HP#villager_plains"));
					for (String biome : Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE")) {
						addDefault(key + ".name." + biome, initSingleton("HP#villager_" + biome.toLowerCase()));
					}
					break;
				case "ZOMBIE_VILLAGER":
					addDefault(key + ".name.default", initSingleton("HP#zombie_villager_plains"));
					for (String biome : new ArrayList<>(Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE"))) {
						addDefault(key + ".name." + biome, initSingleton("HP#zombie_villager_" + biome.toLowerCase()));
					}
					break;
				case "GIANT":
					addDefault(key + ".name.default", initSingleton("HP#zombie"));
					break;
				case "CHICKEN":
					addDefault(key + ".name.default", initSingleton("HP#" + key.toLowerCase() + "_mc"));
					break;
                case "WITHER_SKELETON":
                	addDefault(key + ".chance", 2.5);
                	// Don't stop there
                case "ENDER_DRAGON":
				case "ZOMBIE":
				case "SKELETON":
                    addDefault(key + ".name.default", initSingleton("{mob-default}"));
                    break;
				case "STRIDER":
					addDefault(key + ".name.COLD", initSingleton("HP#cold_strider"));
                default:
                    addDefault(key + ".name.default", initSingleton("HP#" + key.toLowerCase()));
                    break;
            }

    		addDefault(key + ".chance", "{default}");
    	    addDefault(key + ".display-name", "{default}");
    	    addDefault(key + ".price", "{default}");

    		addDefault(key + ".mask-effects", new ArrayList<>());
    		addDefault(key + ".mask-amplifiers", new ArrayList<>());
    		addDefault(key + ".lore", "{default}");
    	}
    }

    private void addPlayerHeads() {
    	getConfig().addDefault("player.chance", 100);
    	getConfig().addDefault("player.display-name", "{player}'s head");
    	getConfig().addDefault("player.price", "{default}");
        getConfig().addDefault("player.mask-effects", new ArrayList<>());
        getConfig().addDefault("player.mask-amplifiers", new ArrayList<>());
        getConfig().addDefault("player.lore", new ArrayList<>(Arrays.asList("&7Price: &6{price}", "&7Player: &a{player}")));
    }

    public double getPrice(String type) {
		return getDouble(type + ".price", getDouble("defaults.price"));
    }

    public String getDisplayName(String type) {
        if (getConfig().get(type + ".display-name").equals("{default}")) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString("defaults.display-name").replaceAll("\\{type}", WordUtils.capitalize(type)));
        } else {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(type + ".display-name").replaceAll("\\{type}", WordUtils.capitalize(type)));
        }
    }

    public double getChance(String type) {
		return getDouble(type + ".chance", getDouble("defaults.chance"));
	}

    public List<String> getLore(String type) {
		List<String> lore = new ArrayList<>();
		List<String> configLore = getStringList(type + ".lore", getStringList("defaults.lore"));
		for (String l : configLore) {
			lore.add(ChatColor.translateAlternateColorCodes('&', l)
					.replace("{type}", type)
					.replace("{price}", String.valueOf(getPrice(type))));
		}
		return lore;
    }

    public List<String> getLore(String name, double price) {
		List<String> lore = new ArrayList<>();
		List<String> configLore = getStringList("player.lore", getStringList("defaults.lore"));

		for (String l : configLore) {
			lore.add(ChatColor.translateAlternateColorCodes('&', l)
					.replace("{type}", "Player")
					.replace("{price}", String.valueOf(price))
					.replace("{player}", name));
		}
		return lore;
	}

	private List<String> initSingleton(String s) {
		return new ArrayList<>(Collections.singleton(s));
	}
}
