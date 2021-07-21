package io.github.thatsmusic99.headsplus.crafting;

public enum RecipeEnums {
	
	BLAZE("BLAZE_POWDER", "blaze"),
	CAVE_SPIDER("FERMENTED_SPIDER_EYE", "cavespider"),
	CHICKEN("FEATHER", "chicken"),
    COD("COD", "cod"),
    COW("LEATHER", "cow"),
	CREEPER("GUNPOWDER", "creeper"),
	ENDERMAN("ENDER_PEARL", "enderman"),
	GHAST("FIRE_CHARGE", "ghast"),
	GUARDIAN("PRISMARINE_SHARD", "guardian"),
    IRON_GOLEM("IRON_BLOCK", "irongolem"),
    MUSHROOM_COW("RED_MUSHROOM", "mushroomcow"),
    PHANTOM("PHANTOM_MEMBRANE", "phantom"),
    PIG("PORKCHOP", "pig"),
    PUFFERFISH("PUFFERFISH", "pufferfish"),
    RABBIT("RABBIT_FOOT", "rabbit"),
    SHEEP("WHITE_WOOL", "sheep"),
    SHULKER("SHULKER_SHELL", "shulker"),
    SKELETON("BONE", "skeleton"),
    SLIME("SLIME_BALL", "slime"),
    SPIDER("SPIDER_EYE", "spider"),
    SQUID("INK_SAC", "squid"),
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

