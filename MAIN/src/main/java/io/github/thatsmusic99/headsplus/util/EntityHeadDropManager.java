package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.reflection.EnumUtil;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EntityHeadDropManager {

    public EntityHeadDropManager() {
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
            coloredData.put("TROPICALFISH", new Class[]{TropicalFish.Pattern.class});
        }
        if (index.getOrder() > 10) {
            coloredData.put("FOX", new Class[]{Fox.Type.class});
            coloredData.put("CAT", new Class[]{Cat.Type.class});
            coloredData.put("TRADERLLAMA", new Class[]{TraderLlama.Color.class});
            coloredData.put("VILLAGER", new Class[]{Villager.Profession.class, Villager.Type.class});
            coloredData.put("MUSHROOMCOW", new Class[]{MushroomCow.Variant.class});
        } else {
            coloredData.put("OCELOT", new Class[]{Ocelot.Type.class});
            coloredData.put("VILLAGER", new Class[]{Villager.Profession.class});
        }
        coloredData.put("ZOMBIEVILLAGER", new Class[]{Villager.Profession.class});

    }

    private static HashMap<String, Class[]> coloredData = new HashMap<>();

    public static void setupHeadsConfig() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        HeadsPlusConfigHeads heads = HeadsPlus.getInstance().getHeadsConfig();
        if (heads.getConfig().getInt("version") < 1) {
            heads.getConfig().set("version", 1);
            // Entity names themselves
            for (String key : heads.getConfig().getKeys(false)) {
                if (key.equalsIgnoreCase("default")) continue;
                if (key.equalsIgnoreCase("version")) continue;
                heads.getConfig().set("mobs." + key, heads.getConfig().getConfigurationSection(key));
            }
            for (String key : coloredData.keySet()) {
                if (!(heads.getConfig().get("mobs." + key.toLowerCase() + ".name") instanceof ConfigurationSection)) {
                    heads.getConfig().set("mobs." + key.toLowerCase() + ".name.default", heads.getConfig().getStringList("mobs." + key.toLowerCase() + ".name"));
                    if (key.equalsIgnoreCase("VILLAGER") && HeadsPlus.getInstance().getNMSVersion().getOrder() > 10) {
                        try {
                            for (Object o : EnumUtil.getEnumResults(coloredData.get(key)[0])) {
                                for (Object o2 : EnumUtil.getEnumResults(coloredData.get(key)[1])) {
                                    heads.getConfig().set("mobs.villager.name." + o.toString() + "." + o2.toString(), "HP#villager_" + o.toString() + "_" + o.toString());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    for (Object o : EnumUtil.getEnumResults(coloredData.get(key)[0])) {

                    }
                }
            }
            heads.getConfig().addDefault("version", 1);
            heads.getConfig().addDefault("defaults.price", 10.0);
            heads.getConfig().addDefault("defaults.lore", new ArrayList<>(Arrays.asList("&7Price: &6{price}", "&7Type: &a{type}")));
            heads.getConfig().addDefault("defaults.display-name", "{type} Head");
            heads.getConfig().addDefault("defaults.interact-name", "{type}");
        }
    }

    public static void setupHeads() {
        for (String entity : DeathEvents.ableEntities) {

        }

    }
    public class Head {
        private ItemStack item;
        private String entityName;
        private Object condition;
    }
}
