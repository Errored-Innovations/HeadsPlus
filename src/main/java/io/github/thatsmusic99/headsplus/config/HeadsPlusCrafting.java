package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.crafting.RecipeEnums;
import io.github.thatsmusic99.headsplus.crafting.RecipeUndefinedEnums;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeadsPlusCrafting extends ConfigSettings {

	private static HeadsPlusMessagesManager messages;
	public HeadsPlusCrafting() {
        this.conName = "crafting";
        messages = HeadsPlus.getInstance().getMessagesConfig();
	    enable();
    }
	
	private void loadCrafting() {
		getConfig().options().header("HeadsPlus by Thatsmusic99 - due to the way Bukkit works, this config can only be reloaded on restart.\nInstructions for setting up can be found at: https://github.com/Thatsmusic99/HeadsPlus/wiki");
		getConfig().options().copyDefaults(true);
		save();
	}

	@Override
	public void reloadC() {
		performFileChecks();
		loadCrafting();
		checkCrafting();
		save();
	}

	private void checkCrafting() {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
	    getConfig().addDefault("base-item.material", nms.getSkull(0).getType().name());
	    getConfig().addDefault("base-item.data", 0);
	    getConfig().addDefault("base-item.price", 10.0);
	    getConfig().addDefault("base-item.display-name", "{type} Head");
	    getConfig().addDefault("base-item.lore", new ArrayList<>(Arrays.asList("&7Price &8» &c{price}", "&7Type &8» &c{type}")));
		for (RecipeEnums key : RecipeEnums.values()) {
			checkForOldFormat(key.str);
			if (key == RecipeEnums.SHEEP) {
				for (DyeColor d : DyeColor.values()) {
					if (d.name().equalsIgnoreCase("LIGHT_GRAY")) { // stupid move ngl
						getConfig().addDefault(key.str + "." + d.name() + ".head", "HP#silver_sheep");
					} else {
						getConfig().addDefault(key.str + "." + d.name() + ".head", "HP#" + d.name().toLowerCase() + "_sheep");
					}
                    getConfig().addDefault(key.str + "." + d.name() + ".ingredients", new ArrayList<>(Collections.singletonList(nms.getColouredBlock(MaterialTranslator.BlockType.WOOL, d.ordinal()).getType().name())));
					getConfig().addDefault(key.str + "." + d.name() + ".price", "{default}");
					getConfig().addDefault(key.str + "." + d.name() + ".display-name", "{default}");
					getConfig().addDefault(key.str + "." + d.name() + ".display-type", HeadsPlus.capitalize(key.name().toLowerCase().replaceAll("_", " ")));
					getConfig().addDefault(key.str + "." + d.name() + ".lore", "{default}");
					getConfig().addDefault(key.str + "." + d.name() + ".shaped", false);
					getConfig().addDefault(key.str + "." + d.name() + ".sellhead-id", key.name());
					checkOverload(key.str);
                }
				continue;
			} else {
                getConfig().addDefault(key.str + ".ingredients", new ArrayList<>(Collections.singletonList(key.mat)));
                switch (key) {
					case RABBIT:
						getConfig().addDefault(key.str + ".head", "HP#brown_" + key.name().toLowerCase());
						break;
					case MUSHROOM_COW:
						getConfig().addDefault(key.str + ".head", "HP#red_mooshroom");
						break;
					case VILLAGER:
						getConfig().addDefault(key.str + ".head", "HP#villager_plains");
						break;
					default:
						getConfig().addDefault(key.str + ".head", "HP#" + key.name().toLowerCase());
				}

            }
			getConfig().addDefault(key.str + ".price", "{default}");
			getConfig().addDefault(key.str + ".display-name", "{default}");
			getConfig().addDefault(key.str + ".display-type", HeadsPlus.capitalize(key.name().toLowerCase().replaceAll("_", " ")));
			getConfig().addDefault(key.str + ".lore", "{default}");
			getConfig().addDefault(key.str + ".shaped", false);
			getConfig().addDefault(key.str + ".sellhead-id", key.name());
			checkOverload(key.str);

		}
	}

	private List<String> checkForOldFormat(String key) {
        List<String> a = new ArrayList<>();
        if (getConfig().get(key + "I") != null) {
            a = getConfig().getStringList(key + "I");
            getConfig().set(key + "I", null);
        }
        return a;
    }

    private void checkOverload(String key) {
        List<String> keyl = getConfig().getStringList(key + ".ingredients");
        if (keyl.size() > 9) {
            getConfig().getStringList(key + ".ingredients").clear();
        }
    }

    public List<String> getLore(String key) {
		List<String> lore = new ArrayList<>();
		if (config.get(key + ".lore").equals("{default}")) {
			for (String str : config.getStringList("base-item.lore")) {
				lore.add(messages.formatMsg(str, null)
						.replaceAll("\\{type}", config.getString(key + ".display-type"))
						.replaceAll("\\{price}", String.valueOf(getPrice(key))));
			}
		} else {
			for (String str : config.getStringList(key + ".lore")) {
				lore.add(messages.formatMsg(str, null));
			}
		}
		return lore;
	}

	public double getPrice(String key) {
		try {
			if (config.get(key + ".price").equals("{default}")) {
				return getDouble("base-item.price");
			} else {
				return getDouble(key + ".price");
			}
		} catch (NullPointerException ex) {
			HeadsPlus.getInstance().getLogger().warning("No price found for " + key + ", using default...");
			return getDouble("base-item.price");
		}
	}

	public String getDisplayName(String key) {
		if (config.get(key + ".display-name").equals("{default}")) {
			return messages.formatMsg(config.getString("base-item.display-name"), null)
					.replaceAll("\\{type}", config.getString(key + ".display-type"));
		} else {
			return messages.formatMsg(config.getString(key + ".display-name"), null)
					.replaceAll("\\{type}", config.getString(key + ".display-type"));
		}
	}
}
