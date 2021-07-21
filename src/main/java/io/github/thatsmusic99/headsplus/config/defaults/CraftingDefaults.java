package io.github.thatsmusic99.headsplus.config.defaults;

import io.github.thatsmusic99.headsplus.util.RecipeType;

public enum CraftingDefaults {

    // TODO: moar?
    LAVA_BUCKET(RecipeType.SHAPELESS, "HP#lava_bucket", "SKELETON_SKULL", "LAVA_BUCKET"),
    WATER_BUCKET(RecipeType.SHAPELESS, "HP#water_bucket", "SKELETON_SKULL", "WATER_BUCKET"),
    BUCKET(RecipeType.SHAPELESS, "HP#bucket", "SKELETON_SKULL", "BUCKET"),
    CCTV(RecipeType.SHAPED, "HP#cctv", "AIR", "AIR", "STONE_BUTTON",
            "IRON_INGOT", "IRON_INGOT", "IRON_INGOT",
            "GLASS_PANE", "REDSTONE", "IRON_INGOT"),
    TOASTER(RecipeType.SHAPED, "HP#toaster", "AIR", "AIR", "AIR",
            "IRON_INGOT", "IRON_INGOT", "IRON_INGOT",
            "IRON_INGOT", "LAVA_BUCKET", "IRON_INGOT"),
    BLACK_DICE(RecipeType.SHAPED, "HP#black_dice", "BLACK_TERRACOTTA", "STONE_BUTTON", "BLACK_TERRACOTTA",
            "STONE_BUTTON", "BLACK_TERRACOTTA", "STONE_BUTTON",
            "BLACK_TERRACOTTA", "STONE_BUTTON", "BLACK_TERRACOTTA"),
    RED_DICE(RecipeType.SHAPED, "HP#red_dice", "RED_TERRACOTTA", "STONE_BUTTON", "RED_TERRACOTTA",
            "STONE_BUTTON", "RED_TERRACOTTA", "STONE_BUTTON",
            "RED_TERRACOTTA", "STONE_BUTTON", "RED_TERRACOTTA"),
    WHITE_DICE(RecipeType.SHAPED, "HP#white_dice", "WHITE_TERRACOTTA", "STONE_BUTTON", "WHITE_TERRACOTTA",
            "STONE_BUTTON", "WHITE_TERRACOTTA", "STONE_BUTTON",
            "WHITE_TERRACOTTA", "STONE_BUTTON", "WHITE_TERRACOTTA"),
    GLOBE(RecipeType.SHAPED, "HP#globe", "AIR", "IRON_NUGGET", "AIR",
            "AIR", "GRASS_BLOCK", "STICK",
            "AIR", "IRON_BLOCK", "IRON_INGOT"),
    TNT(RecipeType.SHAPELESS, "HP#tnt", "SKELETON_SKULL", "TNT"),
    RED_CANDLE(RecipeType.SHAPED, "HP#red_candle", "STRING", "AIR", "RED_TERRACOTTA", "AIR"),
    WHITE_CANDLE(RecipeType.SHAPED, "HP#white_candle", "STRING", "AIR", "WHITE_TERRACOTTA", "AIR"),
    SNOW_GLOBE(RecipeType.SHAPED, "HP#snow_globe", "AIR", "GLASS", "AIR",
            "GLASS", "SNOW", "GLASS",
            "OAK_SLAB", "OAK_SLAB", "OAK_SLAB"),
    PAPERS(RecipeType.SHAPED, "HP#papers", "PAPER", "AIR", "PAPER", "AIR");

    private String[] materials;
    private String head;
    private RecipeType type;

    CraftingDefaults(RecipeType type, String head, String... materials) {
        this.type = type;
        this.head = head;
        this.materials = materials;
    }

    public String[] getMaterials() {
        return materials;
    }

    public RecipeType getRecipeType() {
        return type;
    }

    public String getHead() {
        return head;
    }
}
