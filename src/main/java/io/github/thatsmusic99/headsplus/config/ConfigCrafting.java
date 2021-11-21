package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.defaults.CraftingDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigCrafting extends FeatureConfig {

	private static ConfigCrafting instance;

	public ConfigCrafting() {
		super("crafting.yml");
		instance = this;
    }

	public static ConfigCrafting get() {
		return instance;
	}

	@Override
	public void loadDefaults() {
		HeadsPlus.get().getLogger().info(saveToString());
		addComment("");
		addDefault("defaults.price", 0.0);
		addExample("defaults.lore", Lists.newArrayList("&7Price &8» &c{price}"));
		addDefault("defaults.sellable", false);

		makeSectionLenient("recipes");
		for (CraftingDefaults defaultOption : CraftingDefaults.values()) {
			String key = defaultOption.name().toLowerCase();
			addExample("recipes." + key + ".recipe-type", defaultOption.getRecipeType().name());
			addExample("recipes." + key + ".ingredients", defaultOption.getMaterials());
			addExample("recipes." + key + ".result.head", defaultOption.getHead());
		}
	}

	@Override
	public void moveToNew() {
		HeadsPlus.get().getLogger().info("ee " + getKeys(false).size());
		HeadsPlus.get().getLogger().info(saveToString());
		if (getKeys(false).size() == 2) return;
		boolean usingBaseItem = getBoolean("base-item.use-base-item");
		String baseItemMaterial = getString("base-item.material");
		for (String str : getKeys(false)) {
			HeadsPlus.get().getLogger().info("a");
			ConfigSection section = getConfigSection(str);
			if (str.equals("recipes")) {
				HeadsPlus.get().getLogger().info("b");
				if (section.getKeys(false).size() == 0) continue;
				HeadsPlus.get().getLogger().info("c");
				if (section.get(section.getKeys(false).get(0)) instanceof ConfigSection) return;
				HeadsPlus.get().getLogger().info("d");
			}
			if (str.equals("base-item")) {
				moveTo("base-item.lore", "defaults.lore");
				moveTo("base-item.price", "defaults.price");
			} else {
				boolean shaped = section.getBoolean("shaped");
				set("recipes." + str + ".recipe-type", shaped ? "SHAPED" : "SHAPELESS");
				moveTo(str + ".head", "recipes." + str + ".result.head");
				for (String option : Arrays.asList("price", "display-name", "lore")) {
					if (!section.get(option).equals("{default}")) {
						moveTo(str + "." + option, "recipes." + str + "." + option);
					}
				}

				if (!shaped && usingBaseItem) {
					List<String> ingredients = section.getStringList("ingredients");
					ingredients.add(baseItemMaterial);
					set("recipes." + str + ".ingredients", ingredients);
				} else  {
					moveTo(str + ".ingredients", "recipes." + str + ".ingredients");
				}
			}

			set(str, null);
		}
	}

	public List<String> getLore(String key) {
		List<String> lore = new ArrayList<>();
		try {
			if (get(key + ".lore").equals("{default}")) {
				for (String str : getStringList("base-item.lore")) {
					lore.add(MessagesManager.get().formatMsg(str, null)
							.replaceAll("\\{type}", getString(key + ".display-type"))
							.replaceAll("\\{price}", String.valueOf(getPrice(key))));
				}
			} else {
				for (String str : getStringList(key + ".lore")) {
					lore.add(MessagesManager.get().formatMsg(str, null));
				}
			}
		} catch (NullPointerException ex) {
			for (String str : getStringList(key + ".lore")) {
				lore.add(MessagesManager.get().formatMsg(str, null));
			}
		}

		return lore;
	}

	public double getPrice(String key) {
		return getDouble(key + ".price", getDouble("base-item.price"));
	}

	public String getDisplayName(String key) {
		return MessagesManager.get().formatMsg(getString(key + ".display-name", getString("base-item.display-name")), null)
				.replaceAll("\\{type}", getString(key + ".display-type", getString("base-item.display-type")));
	}

	@Override
	public boolean shouldLoad() {
		return MainConfig.get().getMainFeatures().ENABLE_CRAFTING;
	}
}
