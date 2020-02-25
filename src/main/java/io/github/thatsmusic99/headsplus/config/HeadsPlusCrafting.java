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
import java.util.Collections;
import java.util.List;

public class HeadsPlusCrafting extends ConfigSettings {

	public HeadsPlusCrafting() {
        this.conName = "crafting";
	    enable(false);
    }
	
	private void loadCrafting() {
		getConfig().options().header("HeadsPlus by Thatsmusic99 - due to the way Bukkit works, this config can only be reloaded on restart.\nInstructions for setting up can be found at: https://github.com/Thatsmusic99/HeadsPlus/wiki");
		getConfig().options().copyDefaults(true);
		save();
	}

	@Override
	public void reloadC(boolean nnn) {
		if (configF == null) {
			configF = new File(HeadsPlus.getInstance().getDataFolder(), "crafting.yml");
		}
		config = YamlConfiguration.loadConfiguration(configF);
		loadCrafting();
		checkCrafting();
		save();
	}

	private void checkCrafting() {
        NMSManager nms = HeadsPlus.getInstance().getNMS();
	    getConfig().addDefault("base-item.material", nms.getSkull(0).getType().name());
	    getConfig().addDefault("base-item.data", 0);
		for (RecipeEnums key : RecipeEnums.values()) {
			if (key == RecipeEnums.SHEEP) {
				for (DyeColor d : DyeColor.values()) {
				    checkForOldFormat(key.str);
                    getConfig().addDefault(key.str + "." + d.name() + ".ingredients", new ArrayList<>(Collections.singletonList(nms.getColouredBlock(MaterialTranslator.BlockType.WOOL, d.ordinal()).getType().name())));
                    checkOverload(key.str);
                }
			} else {
                checkForOldFormat(key.str);
                getConfig().addDefault(key.str + ".ingredients", new ArrayList<>(Collections.singletonList(key.mat)));
                checkOverload(key.str);
            }

		}
		for (RecipeUndefinedEnums key : RecipeUndefinedEnums.values()) {
            List<String> a = checkForOldFormat(key.str);

			getConfig().addDefault(key.str + ".ingredients", a);
            checkOverload(key.str);
		}
	}

	private List<String> checkForOldFormat(String key) {
        List<String> a = new ArrayList<>();
        if (getConfig().getStringList(key + "I") != null) {
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
}
