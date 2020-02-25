package io.github.thatsmusic99.headsplus.crafting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;

public enum RecipeEnums {
	
	BLAZE("BLAZE_POWDER", "blaze"),
	CAVESPIDER("FERMENTED_SPIDER_EYE", "cavespider"),
	CHICKEN("FEATHER", "chicken"),
    COD(HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.RAW_FISH).name(), "cod"),
    COW("LEATHER", "cow"),
	CREEPER(HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.SULPHUR).name(), "creeper"),
	ENDERMAN("ENDER_PEARL", "enderman"),
	GHAST(HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.FIREWORK_CHARGE).name(), "ghast"),
	GUARDIAN("PRISMARINE_SHARD", "guardian"),
    IRONGOLEM("IRON_BLOCK", "irongolem"),
    MUSHROOMCOW("RED_MUSHROOM", "mushroomcow"),
    PHANTOM("PHANTOM_MEMBRANE", "phantom"),
    PIG(HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.PORK).name(), "pig"),
    PUFFERFISH("PUFFERFISH", "pufferfish"),
    RABBIT("RABBIT_FOOT", "rabbit"),
    SHEEP(HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.WOOL, 0).getType().name(), "sheep"),
    SHULKER("SHULKER_SHELL", "shulker"),
    SKELETON("BONE", "skeleton"),
    SLIME("SLIME_BALL", "slime"),
    SPIDER("SPIDER_EYE", "spider"),
    SQUID(HeadsPlus.getInstance().getNMS().getNewItems(MaterialTranslator.ChangedMaterials.INK_SAC).name(), "squid"),
    TURTLE("TURTLE_HELMET", "turtle"),
    VILLAGER("EMERALD", "villager"),
    WITCH("POTION", "witch"),
    ZOMBIE("ROTTEN_FLESH", "zombie");
	
    public final String mat;
    public final String str;
	
	RecipeEnums(String mat, String str) {
		this.mat = mat;
		this.str = str;
	}
}

