package io.github.thatsmusic99.headsplus.managers;

import com.google.common.collect.Sets;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigCrafting;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.*;

public class CraftingManager {

    private HashMap<NamespacedKey, Recipe> recipes;
    private static CraftingManager instance;
    private static final HashSet<Integer> VALID_RECIPE_SIZES = Sets.newHashSet(1, 4, 9);

    public CraftingManager() {
        instance = this;
        recipes = new LinkedHashMap<>();
        init();
    }

    public void reload() {
        clear();
        // innit
        init();
    }

    public void init() {
        clear();
        ConfigCrafting crafting = ConfigCrafting.get();
        // The crafting.yml file is empty/disabled
        if (crafting.getConfigSection("recipes") == null) return;
        for (String key : crafting.getConfigSection("recipes").getKeys(false)) {
            ConfigSection section = crafting.getConfigSection("recipes." + key);
            if (section == null) continue;
            try {
                addRecipe(key, section);
            } catch (NullPointerException | IllegalArgumentException ex) {
                HeadsPlus.get().getLogger().severe(ex.getMessage());
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void clear() {
        for (NamespacedKey key : recipes.keySet()) {
            Bukkit.removeRecipe(key);
        }
        recipes.clear();
    }

    public void addRecipe(String key, ConfigSection section) {
        // Get the recipe type
        String recipeTypeStr = section.getString("recipe-type");
        HPUtils.notNull(recipeTypeStr, "There is no recipe type for " + key + "!");
        RecipeType recipeType = RecipeType.getRecipe(recipeTypeStr.toUpperCase());
        HPUtils.notNull(recipeType, "Recipe type " + recipeTypeStr + " (recipe: " + key + ") does not exist!");
        // Get the ingredients
        List<RecipeChoice> choices = new ArrayList<>();
        for (String ingredientStr : section.getStringList("ingredients")) {
            // If it's a HP head and if the recipe allows such ingredients
            if (ingredientStr.startsWith("HP#")) {
                if (!recipeType.allowsHeads())
                    throw new IllegalArgumentException("Recipe type " + recipeType.name() + " (" + key + ") does not support head ingredients!");
                choices.add(new RecipeChoice.ExactChoice(HeadManager.get().getHeadInfo(ingredientStr).buildHead().join()));
            } else {
                Material material = HPUtils.notNull(Material.getMaterial(ingredientStr),
                        "Material " + ingredientStr + " was not found!");
                choices.add(new RecipeChoice.MaterialChoice(material));
            }
        }
        // Get the result section
        ConfigSection resultSection = section.getConfigSection("result");
        HPUtils.notNull(resultSection, "Recipe " + key + " does not have a result section!");
        // Get the head itself
        HeadManager.HeadInfo resultHead = HeadManager.get().getHeadInfo(resultSection.getString("head"));
        // Build the resulting item/wait for it
        ItemStack item = resultHead.buildHead().join();
        //
        Recipe recipe;
        NamespacedKey namespacedKey = new NamespacedKey(HeadsPlus.get(), "crafting_" + key);
        switch (recipeType) {
            case FURNACE:
                // TODO - make EXP and cooking time configurable
                recipe = new FurnaceRecipe(namespacedKey, item, choices.get(0), 0.1f, 200);
                break;
            case SHAPED:
                if (!VALID_RECIPE_SIZES.contains(choices.size()))
                    throw new IllegalArgumentException("Recipe size must be 1, 4 or 9 for shaped recipes (" + key + "), not " + choices.size() + "!");
                int dimensions = (int) Math.sqrt(choices.size());
                // Honestly, why can't I just drop materials in a hashmap and boom recipe made? wtf, minecraft!?
                int startChar = 50;
                ShapedRecipe tempRecipe = new ShapedRecipe(namespacedKey, item);
                HashMap<RecipeChoice, Character> map = new HashMap<>();
                String[] rows = new String[dimensions];
                for (int i = 0; i < dimensions; i++) {
                    for (int j = 0; j < dimensions; j++) {
                        int usedChar;
                        RecipeChoice choice = choices.get(i * dimensions + j);
                        if (!map.containsKey(choice)) {
                            map.put(choice, (char) ++startChar);
                            usedChar = startChar;
                        } else {
                            usedChar = (int) map.get(choice);
                        }
                        if (rows[i] == null) {
                            rows[i] = "";
                        }
                        rows[i] += (char) usedChar;
                    }
                }
                tempRecipe.shape(rows);
                for (RecipeChoice choice : map.keySet()) {
                    if (tempRecipe.getChoiceMap().containsValue(choice)) continue;
                    tempRecipe.setIngredient(map.get(choice), choice);
                }
                recipe = tempRecipe;
                break;
            case SHAPELESS:
                recipe = new ShapelessRecipe(namespacedKey, item);
                for (RecipeChoice choice : choices) {
                    ((ShapelessRecipe) recipe).addIngredient(choice);
                }
                break;
            case SMOKING:
                recipe = new SmokingRecipe(namespacedKey, item, choices.get(0), 0.1f, 200);
                break;
            case BLASTING:
                recipe = new BlastingRecipe(namespacedKey, item, choices.get(0), 0.1f, 200);
                break;
            case CAMPFIRE:
                recipe = new CampfireRecipe(namespacedKey, item, choices.get(0), 0.1f, 200);
                break;
            case MERCHANT:
                // why does this not require a namespaced key lmao
                // TODO - configure max uses, EXP reward, villager experience, price multiplier and whether to ignore discounts
                recipe = new MerchantRecipe(item, 5);
                break;
            case SMITHING:
                if (choices.size() != 2) throw new IllegalArgumentException("A smithing recipe (" + key + ") needs 2 ingredients!");
                recipe = new SmithingRecipe(namespacedKey, item, choices.get(0), choices.get(1));
                break;
            case STONECUTTING:
                // TODO - configure groups
                recipe = new StonecuttingRecipe(namespacedKey, item, choices.get(0));
                break;
            default:
                // I GOT ALL NINE RECIPE TYPES WHAT THE HELL ARE YOU ON ABOUT??
                throw new IllegalStateException("Unexpected value (" + key + "): " + recipeType);
        }
        recipes.put(namespacedKey, recipe);
        Bukkit.getScheduler().runTask(HeadsPlus.get(), () -> Bukkit.addRecipe(recipe));
    }

    public static CraftingManager get() {
        return instance;
    }
}
