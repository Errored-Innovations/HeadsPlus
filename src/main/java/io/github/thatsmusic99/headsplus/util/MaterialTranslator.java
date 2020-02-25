package io.github.thatsmusic99.headsplus.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;

public class MaterialTranslator {

    // By brainsynder for 1.13

    public static Material toMaterial (BlockType type, int data) {
        if (type == null) throw new NullPointerException("BlockType can not be null");
        if ((data < 0) || (data > 15)) throw new IndexOutOfBoundsException("data value has to be between 0 -> 15");

        DyeColor dye = DyeColor.values()[data];
        String name = dye.name();
        return Material.valueOf(name + "_" + type.name());
    }

    public static Material getItem(ChangedMaterials type) {
        if (type == ChangedMaterials.FIREWORK_CHARGE) {
            return Material.FIRE_CHARGE;
        } else if (type == ChangedMaterials.PORK) {
            return Material.PORKCHOP;
        } else if (type == ChangedMaterials.SULPHUR){
            return Material.GUNPOWDER;
        } else if (type == ChangedMaterials.GRILLED_PORK) {
            return Material.COOKED_PORKCHOP;
        } else if (type == ChangedMaterials.RAW_FISH){
            return Material.COD;
        } else {
            return Material.INK_SAC;
        }
    }

    public enum BlockType {
        CONCRETE,
        CONCRETE_POWDER,
        STAINED_GLASS,
        STAINED_GLASS_PANE,
        SHULKER_BOX,
        TERRACOTTA,
        GLAZED_TERRACOTTA,
        CARPET,
        DYE,
        WOOL
    }

    public enum ChangedMaterials {
        FIREWORK_CHARGE,
        SULPHUR,
        PORK,
        GRILLED_PORK,
        INK_SAC,
        RAW_FISH
    }
}
