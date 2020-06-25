package io.github.thatsmusic99.headsplus.config;

import com.google.common.io.Files;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Rabbit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeadsPlusConfigHeads extends ConfigSettings {
	public final List<String> eHeads = new ArrayList<>(Arrays.asList("apple", "cake", "chest", "cactus", "melon", "pumpkin"));
	public final List<String> ieHeads = new ArrayList<>(Arrays.asList("coconutB", "coconutG", "oaklog", "present1", "present2", "tnt", "tnt2", "arrowUp", "arrowDown", "arrowQuestion", "arrowLeft", "arrowRight", "arrowExclamation"));

	public HeadsPlusConfigHeads() {
	    this.conName = "mobs";
	    enable();
    }

	@Override
	public void reloadC() {
		if (configF == null) {
			File oldFile = new File(HeadsPlus.getInstance().getDataFolder(), "heads.yml");
			File newFile = new File(HeadsPlus.getInstance().getDataFolder(), "mobs.yml");
			if (oldFile.exists()) {
				try {
					Files.copy(oldFile, newFile);
					oldFile.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			configF = newFile;
		}
		super.reloadC();
	}

	@Override
	public void load() {
		try {
			getConfig().options().header("HeadsPlus by Thatsmusic99");
			getConfig().addDefault("version", 0);
			getConfig().addDefault("defaults.price", 10.0);
			getConfig().addDefault("defaults.lore", new ArrayList<>(Arrays.asList("&7Price &8» &c{price}", "&7Type &8» &c{type}")));
			getConfig().addDefault("defaults.display-name", "{type} Head");
			getConfig().addDefault("defaults.interact-name", "{type}");
			getConfig().addDefault("defaults.chance", 5);
		    addUndefinedHeads();
		    addPlayerHeads();
		    addENHeads();
		    addieHeads();
		    getConfig().options().copyDefaults(true);
		    save();
		} catch (Exception e) {
			if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public String getDefaultPath() {
		return "defaults.price";
	}

	private void addUndefinedHeads() {
    	for (String keyS : DeathEvents.ableEntities) {
    		String key = keyS.replaceAll("_", "").toLowerCase();
    		if (keyS.equalsIgnoreCase("TRADER_LLAMA") || keyS.equalsIgnoreCase("WANDERING_TRADER")) {
    			key = keyS.toLowerCase();
			}
			if (getConfig().get(key + ".name") instanceof List) {
				List<String> h = getConfig().getStringList(key + ".name");
				getConfig().addDefault(key + ".name.default", h);
				getConfig().set(key + ".name", null);
			}
			getConfig().options().copyDefaults(true);
			save();
            switch (key) {
				case "bee":
					getConfig().addDefault("bee.name.default", initSingleton("HP#bee_mc"));
					getConfig().addDefault("bee.name.ANGRY", initSingleton("HP#bee_angry"));
					getConfig().addDefault("bee.name.ANGRY,NECTAR", initSingleton("HP#bee_pollinated_angry"));
					getConfig().addDefault("bee.name.NECTAR", initSingleton("HP#bee_pollinated"));
					break;
				case "creeper":
					getConfig().addDefault("creeper.name.default", initSingleton("{mob-default}"));
					getConfig().addDefault("creeper.name.POWERED", initSingleton("HP#charged_creeper"));
					break;
				case "cat":
					getConfig().addDefault("cat.name.default", initSingleton("HP#tabby_cat"));
					for (String type : Arrays.asList("tabby", "black", "red", "siamese", "british_shorthair", "calico", "persian", "ragdoll", "white", "jellie", "all_black")) {
						getConfig().addDefault("cat.name." + type.toUpperCase(), initSingleton("HP#" + type + "_cat"));
					}
					break;
				case "fox":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#fox_mc"));
					getConfig().addDefault(key + ".name.RED", initSingleton("HP#fox_mc"));
					getConfig().addDefault(key + ".name.SNOW", initSingleton("HP#snow_fox"));
					break;
				case "horse":
					getConfig().addDefault("horse.name.default", initSingleton("HP#brown_horse"));
					for (Horse.Color variant : Horse.Color.values()) {
						getConfig().addDefault("horse.name." + variant.name(), initSingleton("HP#" + variant.name().toLowerCase() + "_horse"));
					}
					break;
                case "llama":
                case "trader_llama":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#creamy_" + key));
                	getConfig().addDefault(key + ".name.CREAMY", initSingleton("HP#creamy_" + key));
					getConfig().addDefault(key + ".name.WHITE", initSingleton("HP#white_" + key));
					getConfig().addDefault(key + ".name.BROWN", initSingleton("HP#brown_" + key));
					getConfig().addDefault(key + ".name.GRAY", initSingleton("HP#gray_" + key));
                    break;
				case "mushroomcow":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#red_mooshroom"));
					getConfig().addDefault(key + ".name.RED", initSingleton("HP#red_mooshroom"));
					getConfig().addDefault(key + ".name.BROWN", initSingleton("HP#brown_mooshroom"));
					break;
				case "ocelot":
					getConfig().addDefault("ocelot.name.default", initSingleton("HP#ocelot"));
					getConfig().addDefault("ocelot.name.WILD_OCELOT", initSingleton("HP#ocelot"));
					getConfig().addDefault("ocelot.name.BLACK_CAT", initSingleton("HP#black_cat"));
					getConfig().addDefault("ocelot.name.RED_CAT", initSingleton("HP#red_cat"));
					getConfig().addDefault("ocelot.name.SIAMESE_CAT", initSingleton("HP#siamese_cat"));
					break;
				case "panda":
					for (String gene : Arrays.asList("NORMAL", "PLAYFUL", "LAZY", "WORRIED", "BROWN", "WEAK", "AGGRESSIVE")) {
						getConfig().addDefault("panda.name." + gene, initSingleton("HP#panda_" + gene.toLowerCase()));
					}
					break;
                case "parrot":
                	getConfig().addDefault("parrot.name.default", initSingleton("HP#red_parrot"));
                	for (String colour : new String[]{"RED", "BLUE", "GREEN", "CYAN", "GRAY"}) {
                		getConfig().addDefault("parrot.name." + colour, initSingleton("HP#" + colour.toLowerCase() + "_parrot"));
					}
                    break;
				case "rabbit":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#brown_rabbit"));
					for (Rabbit.Type type : Rabbit.Type.values()) {
						getConfig().addDefault(key + ".name." + type.name(), initSingleton("HP#" + type.name().toLowerCase() + "_rabbit"));
					}
					break;
				case "sheep":
					getConfig().addDefault("sheep.name.default", initSingleton("HP#white_sheep"));
					for (DyeColor dc : DyeColor.values()) {
						try {
							if (dc == DyeColor.valueOf("LIGHT_GRAY")) {
								getConfig().addDefault("sheep.name." + dc.name(), initSingleton("HP#silver_sheep"));
							} else {
								getConfig().addDefault("sheep.name." + dc.name(), initSingleton("HP#" + dc.name().toLowerCase() + "_sheep"));
							}
						} catch (NoSuchFieldError | IllegalArgumentException ex) {
							getConfig().addDefault("sheep.name." + dc.name(), initSingleton("HP#silver_sheep"));
						}
					}
					break;
				case "tropicalfish":
				case "pigzombie":
				case "elderguardian":
				case "magmacube":
				case "zombiehorse":
				case "skeletonhorse":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#" + key));
					break;
				case "villager":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#villager_plains"));
					for (String biome : Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE")) {
						getConfig().addDefault(key + ".name." + biome, initSingleton("HP#villager_" + biome.toLowerCase()));
					}
					break;
				case "zombievillager":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#zombie_villager_plains"));
					for (String biome : new ArrayList<>(Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE"))) {
						getConfig().addDefault(key + ".name." + biome, initSingleton("HP#zombie_villager_" + biome.toLowerCase()));
					}
					break;
				case "giant":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#zombie"));
					break;
				case "chicken":
					getConfig().addDefault(key + ".name.default", initSingleton("HP#" + key + "_mc"));
					break;
                case "witherskeleton":
                	getConfig().addDefault(key + ".chance", 2.5);
                	// Don't stop there
                case "enderdragon":
				case "zombie":
				case "skeleton":
                    getConfig().addDefault(key + ".name.default", initSingleton("{mob-default}"));
                    break;
				case "strider":
					getConfig().addDefault(key + ".name.COLD", initSingleton("HP#cold_strider"));
                default:
                    getConfig().addDefault(key + ".name.default", initSingleton("HP#" + keyS.toLowerCase()));
                    break;
            }

    		getConfig().addDefault(key + ".chance", "{default}");
    	    getConfig().addDefault(key + ".display-name", "{default}");
    	    getConfig().addDefault(key + ".price", "{default}");

            getConfig().addDefault(key + ".interact-name", "{default}");
    		getConfig().addDefault(key + ".mask-effects", new ArrayList<>());
    		getConfig().addDefault(key + ".mask-amplifiers", new ArrayList<>());
    		getConfig().addDefault(key + ".lore", "{default}");
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
    private void addENHeads() {
    	for (String key : eHeads) {
    		getConfig().addDefault(key + "HeadN", WordUtils.capitalize(key));
    		getConfig().addDefault(key + "N", "MHF_" + key);
    	}
    }
    private void addieHeads() {
    	for (String key : ieHeads) {
    		if (key.equals("coconutB")) {
    			getConfig().addDefault("brownCoconutHeadEN", "Brown Coconut");
    			getConfig().addDefault("brownCoconutHeadN", "MHF_CoconutB");
    		}
    		if (key.equals("coconutG")) {
    			getConfig().addDefault("greenCoconutHeadEN", "Green Coconut");
    			getConfig().addDefault("greenCoconutHeadN", "MHF_CoconutG");
    		}
    		if (key.equals("oaklog")) {
    			getConfig().addDefault("oakLogHeadEN", "Oak Log");
    			getConfig().addDefault("oakLogHeadN", "MHF_OakLog");
    		}
    		if (key.equals("present1")) {
    			getConfig().addDefault("present1HeadEN", "Present");
    			getConfig().addDefault("present1HeadN", "MHF_Present1");
    		}
    		if (key.equals("present2")) {
    			getConfig().addDefault("present2HeadEN", "Present");
    			getConfig().addDefault("present2HeadN", "MHF_Present2");
    		}
    		if (key.equals("tnt")) {
    			getConfig().addDefault("tntHeadEN", "TNT");
    			getConfig().addDefault("tntHeadN", "MHF_TNT");
    		}
    		if (key.equalsIgnoreCase("tnt2")) {
    			getConfig().addDefault("tnt2HeadEN", "TNT");
    			getConfig().addDefault("tnt2HeadN", "MHF_TNT");
    		}
    		if (key.equalsIgnoreCase("arrowUp")) {
    			getConfig().addDefault("arrowUpHeadEN", "Arrow pointing up");
    			getConfig().addDefault("arrowUpHeadN", "MHF_ArrowUp");
    		}
    		if (key.equalsIgnoreCase("arrowDown")) {
    			getConfig().addDefault("arrowDownHeadEN", "Arrow pointing down");
    			getConfig().addDefault("arrowDownHeadN", "MHF_ArrowDown");
    		}
    		if (key.equalsIgnoreCase("arrowRight")) {
    			getConfig().addDefault("arrowRightHeadEN", "Arrow pointing right");
    			getConfig().addDefault("arrowRightHeadN", "MHF_ArrowRight");
    		}
    		if (key.equalsIgnoreCase("arrowLeft")) {
    			getConfig().addDefault("arrowLeftHeadEN", "Arrow pointing left");
    			getConfig().addDefault("arrowLeftHeadN", "MHF_ArrowLeft");
    		}
    		if (key.equalsIgnoreCase("arrowExclamation")) {
    			getConfig().addDefault("exclamationHeadEN", "Exclamation");
    			getConfig().addDefault("exclamationHeadN", "MHF_Exclamation");
    		}
    		if (key.equalsIgnoreCase("arrowQuestion")) {
    			getConfig().addDefault("questionHeadEN", "Question");
    			getConfig().addDefault("questionHeadN", "MHF_Question");
    		}
    	}
    }

    public double getPrice(String type) {
		return getDouble(type + ".price");
    }

    public String getDisplayName(String type) {
        if (getConfig().get(type + ".display-name").equals("{default}")) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString("defaults.display-name").replaceAll("\\{type}", WordUtils.capitalize(type)));
        } else {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(type + ".display-name").replaceAll("\\{type}", WordUtils.capitalize(type)));
        }
    }

    public String getInteractName(String type) {
        if (getConfig().get(type + ".interact-name").equals("{default}")) {
            return getConfig().getString("defaults.interact-name");
        } else {
            return getConfig().getString(type + ".interact-name").replaceAll("\\{type}", type);
        }
    }

    public double getChance(String type) {
		if (getConfig().get(type + ".chance").equals("{default}")) {
			return getDouble("defaults.chance");
		} else {
			return getDouble(type + ".chance");
		}
	}

    public List<String> getLore(String type) {
		List<String> lore = new ArrayList<>();
		List<String> configLore;
        if (getConfig().get(type + ".lore").equals("{default}")) {
        	configLore = getConfig().getStringList("defaults.lore");
        } else {
        	configLore = getConfig().getStringList(type + ".lore");
        }
		for (String l : configLore) {
			lore.add(ChatColor.translateAlternateColorCodes('&', l)
					.replace("{type}", type)
					.replace("{price}", String.valueOf(getPrice(type))));
		}
		return lore;
    }

    public List<String> getLore(String name, double price) {
		List<String> lore = new ArrayList<>();
		List<String> configLore;
		if (getConfig().get("player.lore").equals("{default}")) {
			configLore = getConfig().getStringList("defaults.lore");
		} else {
			configLore = getConfig().getStringList("player.lore");
		}
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
