package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Head;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeadsPlusConfigHeads extends ConfigSettings {
	public final List<String> mHeads = new ArrayList<>(Arrays.asList("blaze", "cavespider", "chicken", "cow", "creeper", "enderman", "ghast", "guardian", "irongolem", "mushroomcow", "rabbit", "pig", "sheep", "skeleton", "slime", "spider", "squid", "villager", "witch", "zombie"));
	public final List<String> uHeads = new ArrayList<>(Arrays.asList("bat", "bee", "cat", "cod", "dolphin", "donkey", "drowned", "enderdragon", "elderguardian", "endermite", "evoker", "fox", "horse", "husk", "llama", "magmacube", "mule", "ocelot", "panda", "parrot", "phantom", "pigzombie", "pillager", "polarbear", "pufferfish", "ravager", "salmon", "shulker", "silverfish", "skeletonhorse", "snowman", "stray", "trader_llama", "tropicalfish", "turtle", "vex", "vindicator", "wandering_trader", "wither", "witherskeleton", "wolf", "zombiehorse", "zombievillager"));
	public final List<String> eHeads = new ArrayList<>(Arrays.asList("apple", "cake", "chest", "cactus", "melon", "pumpkin"));
	public final List<String> ieHeads = new ArrayList<>(Arrays.asList("coconutB", "coconutG", "oaklog", "present1", "present2", "tnt", "tnt2", "arrowUp", "arrowDown", "arrowQuestion", "arrowLeft", "arrowRight", "arrowExclamation"));

	public HeadsPlusConfigHeads() {
	    this.conName = "mobs";
	    enable(false);
    }

	@Override
	public void reloadC(boolean nullp) {
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
		super.reloadC(nullp);
	}

	@Override
	public void load(boolean ehhLolIDontNeedThisButJavaIsMakingMeAnywaysSoHiHowAreYou) {
		try {
			getConfig().options().header("HeadsPlus by Thatsmusic99");
			getConfig().addDefault("version", 0);
			getConfig().addDefault("defaults.price", 10.0);
			getConfig().addDefault("defaults.lore", new ArrayList<>(Arrays.asList("&7Price: &6{price}", "&7Type: &a{type}")));
			getConfig().addDefault("defaults.display-name", "{type} Head");
			getConfig().addDefault("defaults.interact-name", "{type}");
		    addMHFHeads();
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
    	for (String key : uHeads) {
            switch (key) {
				case "bee":
					if (getConfig().get("bee.name") instanceof List) {
						List<String> h = getConfig().getStringList("bee.name");
						getConfig().set("bee.name", null);
						getConfig().addDefault("bee.name.default", h);
					}
					getConfig().addDefault("bee.name.default", new ArrayList<>(Collections.singletonList("HP#bee_mc")));
					getConfig().addDefault("bee.name.ANGRY", new ArrayList<>(Collections.singletonList("HP#bee_angry")));
					getConfig().addDefault("bee.name.ANGRY,NECTAR", new ArrayList<>(new ArrayList<>(Collections.singletonList("HP#bee_pollinated_angry"))));
					getConfig().addDefault("bee.name.NECTAR", new ArrayList<>(new ArrayList<>(Collections.singletonList("HP#bee_pollinated"))));
				case "cat":
					if (getConfig().get("cat.name") instanceof List) {
						List<String> h = getConfig().getStringList("cat.name");
						getConfig().set("cat.name", null);
						getConfig().addDefault("cat.name.default", h);
					}
					getConfig().addDefault("cat.name.default", new ArrayList<>());
					getConfig().addDefault("cat.name.TABBY", new ArrayList<>(Collections.singletonList("HP#tabby_cat")));
					getConfig().addDefault("cat.name.BLACK", new ArrayList<>(Collections.singletonList("HP#black_cat")));
					getConfig().addDefault("cat.name.RED", new ArrayList<>(Collections.singletonList("HP#red_cat")));
					getConfig().addDefault("cat.name.SIAMESE", new ArrayList<>(Collections.singletonList("HP#siamese_cat")));
					getConfig().addDefault("cat.name.BRITISH_SHORTHAIR", new ArrayList<>(Collections.singletonList("HP#british_shorthair_cat")));
					getConfig().addDefault("cat.name.CALICO", new ArrayList<>(Collections.singletonList("HP#calico_cat")));
					getConfig().addDefault("cat.name.PERSIAN", new ArrayList<>(Collections.singletonList("HP#persian_cat")));
					getConfig().addDefault("cat.name.RAGDOLL", new ArrayList<>(Collections.singletonList("HP#ragdoll_cat")));
					getConfig().addDefault("cat.name.WHITE", new ArrayList<>(Collections.singletonList("HP#white_cat")));
					getConfig().addDefault("cat.name.JELLIE", new ArrayList<>(Collections.singletonList("HP#jellie_cat")));
					getConfig().addDefault("cat.name.ALL_BLACK", new ArrayList<>(Collections.singletonList("HP#all_black_cat")));
					break;
				case "fox":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
					getConfig().addDefault(key + ".name.RED", new ArrayList<>(Collections.singleton("HP#fox_mc")));
					getConfig().addDefault(key + ".name.SNOW", new ArrayList<>(Collections.singleton("HP#snow_fox")));
					break;
				case "horse":
					if (getConfig().get("horse.name") instanceof List) {
						List<String> h = getConfig().getStringList("horse.name");
						getConfig().set("horse.name", null);
						getConfig().addDefault("horse.name.default", h);
					}
					getConfig().addDefault("horse.name.default", new ArrayList<>());
					for (Horse.Color variant : Horse.Color.values()) {
						getConfig().addDefault("horse.name." + variant.name(), new ArrayList<>(Collections.singleton("HP#" + variant.name().toLowerCase() + "_horse")));
					}
					break;
                case "llama":
                case "trader_llama":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
                	getConfig().addDefault(key + ".name.CREAMY", new ArrayList<>(Collections.singleton("HP#creamy_" + key)));
					getConfig().addDefault(key + ".name.WHITE", new ArrayList<>(Collections.singleton("HP#white_" + key)));
					getConfig().addDefault(key + ".name.BROWN", new ArrayList<>(Collections.singleton("HP#brown_" + key)));
					getConfig().addDefault(key + ".name.GRAY", new ArrayList<>(Collections.singleton("HP#gray_" + key)));
                    break;

				case "ocelot":
					if (getConfig().get("ocelot.name") instanceof List) {
						List<String> h = getConfig().getStringList("ocelot.name");
						getConfig().set("ocelot.name", null);
						getConfig().addDefault("ocelot.name.default", h);
					}
					getConfig().addDefault("ocelot.name.WILD_OCELOT", new ArrayList<>(Collections.singleton("HP#ocelot")));
					getConfig().addDefault("ocelot.name.BLACK_CAT", new ArrayList<>(Collections.singleton("HP#black_cat")));
					getConfig().addDefault("ocelot.name.RED_CAT", new ArrayList<>(Collections.singleton("HP#red_cat")));
					getConfig().addDefault("ocelot.name.SIAMESE_CAT", new ArrayList<>(Collections.singleton("HP#siamese_cat")));
					break;
				case "panda":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					for (String gene : Arrays.asList("NORMAL", "PLAYFUL", "LAZY", "WORRIED", "BROWN", "WEAK", "AGGRESSIVE")) {
						getConfig().addDefault("panda.name." + gene, new ArrayList<>(Collections.singleton("HP#panda_" + gene.toLowerCase())));
					}
					break;
                case "parrot":
                	if (getConfig().get("parrot.name") instanceof List) {
                		List<String> h = getConfig().getStringList("parrot.name");
                		getConfig().set("parrot.name", null);
                		getConfig().addDefault("parrot.name.default", h);
                	}
                	getConfig().addDefault("parrot.name.default", new ArrayList<>());
					getConfig().addDefault("parrot.name.RED", new ArrayList<>(Collections.singleton("HP#red_parrot")));
					getConfig().addDefault("parrot.name.BLUE", new ArrayList<>(Collections.singleton("HP#blue_parrot")));
					getConfig().addDefault("parrot.name.GREEN", new ArrayList<>(Collections.singleton("HP#green_parrot")));
					getConfig().addDefault("parrot.name.CYAN", new ArrayList<>(Collections.singleton("HP#cyan_parrot")));
					getConfig().addDefault("parrot.name.GRAY", new ArrayList<>(Collections.singleton("HP#gray_parrot")));
                    break;
				case "tropicalfish":
					if (getConfig().get("tropicalfish.name") instanceof List) {
						List<String> h = getConfig().getStringList("tropicalfish.name");
						getConfig().set("tropicalfish.name", null);
						getConfig().addDefault("tropicalfish.name.default", h);
					}
					getConfig().addDefault("tropicalfish.name.default", new ArrayList<>());
					break;
				case "zombievillager":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
					for (String biome : new ArrayList<>(Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE"))) {
						getConfig().addDefault(key + ".name." + biome, new ArrayList<>(Collections.singleton("HP#zombie_villager_" + biome.toLowerCase())));
					}
					break;
                case "witherskeleton":
                	getConfig().addDefault(key + ".chance", 2.5);
                	// Don't stop there
                case "enderdragon":
                    getConfig().addDefault(key + ".name", new ArrayList<>(Collections.singleton("{mob-default}")));
                    break;
                default:
                    getConfig().addDefault(key + ".name", new ArrayList<>(Collections.singleton("HP#" + key)));
                    break;
            }

    		getConfig().addDefault(key + ".chance", 10);
    	    getConfig().addDefault(key + ".display-name", "{default}");
    	    getConfig().addDefault(key + ".price", "{default}");

            getConfig().addDefault(key + ".interact-name", "{default}");
    		getConfig().addDefault(key + ".mask-effects", new ArrayList<>());
    		getConfig().addDefault(key + ".mask-amplifiers", new ArrayList<>());
    		getConfig().addDefault(key + ".lore", "{default}");
    	}
    }
    private void addMHFHeads() {
    	
    	for (String key : mHeads) {
    		switch (key) {
				case "creeper":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault("creeper.name.default", new ArrayList<>(Collections.singleton("MHF_Creeper")));
					getConfig().addDefault("creeper.name.POWERED", new ArrayList<>(Collections.singleton("HP#charged_creeper")));
					break;
				case "irongolem":
					getConfig().addDefault(key + ".name", new ArrayList<>(Collections.singleton("MHF_Golem")));
					break;
				case "mushroomcow":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
					getConfig().addDefault(key + ".name.RED", new ArrayList<>(Collections.singleton("HP#red_mooshroom")));
					getConfig().addDefault(key + ".name.BROWN", new ArrayList<>(Collections.singleton("HP#brown_mooshroom")));
					break;
				case "rabbit":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
					for (Rabbit.Type type : Rabbit.Type.values()) {
						getConfig().addDefault(key + ".name." + type.name(), new ArrayList<>(Collections.singleton("HP#" + type.name().toLowerCase() + "_rabbit")));
					}
					break;
				case "sheep":
					getConfig().addDefault("sheep.name.default", new ArrayList<>(Collections.singleton("MHF_Sheep")));
					for (DyeColor dc : DyeColor.values()) {
						try {
							if (dc == DyeColor.valueOf("LIGHT_GRAY")) {
								getConfig().addDefault("sheep.name." + dc.name(), new ArrayList<>(Collections.singleton("HP#silver_sheep")));
							} else {
								getConfig().addDefault("sheep.name." + dc.name(), new ArrayList<>(Collections.singleton("HP#" + dc.name().toLowerCase() + "_sheep")));
							}
						} catch (NoSuchFieldError | IllegalArgumentException ex) {
							getConfig().addDefault("sheep.name." + dc.name(), new ArrayList<>(Collections.singleton("HP#silver_sheep")));
						}
					}
					break;
				case "villager":
					if (getConfig().get(key + ".name") instanceof List) {
						List<String> h = getConfig().getStringList(key + ".name");
						getConfig().set(key + ".name", null);
						getConfig().addDefault(key + ".name.default", h);
					}
					getConfig().addDefault(key + ".name.default", new ArrayList<>());
					for (String biome : new ArrayList<>(Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA", "JUNGLE"))) {
						getConfig().addDefault(key + ".name." + biome, new ArrayList<>(Collections.singleton("HP#villager_" + biome.toLowerCase())));
					}
					break;
				default:
					getConfig().addDefault(key + ".name", new ArrayList<>(Collections.singleton("MHF_" + HeadsPlus.capitalize(key))));

			}
			getConfig().addDefault(key + ".interact-name", "{default}");
			getConfig().addDefault(key + ".display-name", "{default}");
			getConfig().addDefault(key + ".chance", 25);
            getConfig().addDefault(key + ".price", "{default}");
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
}
