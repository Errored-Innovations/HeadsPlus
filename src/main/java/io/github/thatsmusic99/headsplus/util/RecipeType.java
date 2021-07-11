package io.github.thatsmusic99.headsplus.util;

import org.bukkit.inventory.*;

public enum RecipeType {

    SHAPED(ShapedRecipe.class, false),
    SHAPELESS(ShapelessRecipe.class, false),
    BLASTING(BlastingRecipe.class, true),
    CAMPFIRE(CampfireRecipe.class, true),
    FURNACE(FurnaceRecipe.class, true),
    MERCHANT(MerchantRecipe.class, true),
    SMITHING(SmithingRecipe.class, true),
    SMOKING(SmokingRecipe.class, true),
    STONECUTTING(StonecuttingRecipe.class, true);

    private Class<?> clazz;
    private boolean allowsHeads;

    RecipeType(Class<?> clazz, boolean allowsHeads) {
        this.allowsHeads = allowsHeads;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean allowsHeads() {
        return allowsHeads;
    }

    public static RecipeType getRecipe(String str) {
        try {
            return RecipeType.valueOf(str);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
