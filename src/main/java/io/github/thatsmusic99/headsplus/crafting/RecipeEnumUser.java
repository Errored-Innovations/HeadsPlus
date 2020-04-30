package io.github.thatsmusic99.headsplus.crafting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeEnumUser {

    private final HeadsPlus hp = HeadsPlus.getInstance();
	private final FileConfiguration crafting = hp.getCraftingConfig().getConfig();

	public RecipeEnumUser() {
	    addEnumToConfig();
    }

	private void addEnumToConfig() {
	    for (String key : crafting.getKeys(false)) {
	        if (!key.equalsIgnoreCase("base-item")) {
	            if (key.equalsIgnoreCase("sheep")) {
	                for (String key2 : crafting.getConfigurationSection("sheep").getKeys(false)) {
	                    addRecipe("sheep." + key2);
	                }
	            } else {
	                addRecipe(key);
	            }
	        }
	    }
	}

	private void addRecipe(String id) {
	    // Get the Central ID, firstly
	    String headId = crafting.getString(id + ".head");
        EntityHead head;
        try {
            // Create the head object
            head = new EntityHead(crafting.getString(id + ".sellhead-id"));
        } catch (NullPointerException ex) {
            // If there's no sellhead ID, stop there
	        HeadsPlus.getInstance().getLogger().warning("Missing Sellhead ID for " + id + "!");
	        return;
        }
        // Register the Sellhead ID
        SellHead.registerHeadID(crafting.getString(id + ".sellhead-id"));
        // Apply the price, display name and lore
	    head.withPrice(hp.getCraftingConfig().getPrice(id))
                .withDisplayName(hp.getCraftingConfig().getDisplayName(id))
                .withLore(hp.getCraftingConfig().getLore(id));
	    // If using a custom texture
        if (headId.startsWith("HP#")) {
            try {
                head.withTexture(HeadsPlus.getInstance().getHeadsXConfig().getTextures(headId));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                HeadsPlus.getInstance().getLogger().warning("Null texture: " + headId);
                return;
            }
        } else {
            head.withPlayerName(headId);
        }
        if (crafting.getBoolean(id + ".shaped")) {
            List<String> ingredients = crafting.getStringList(id + ".ingredients");
            ShapedRecipe recipe = HeadsPlus.getInstance().getNMS().getShapedRecipe(head.getItemStack(), "hp_" + id);
            int ch = 70;
            Map<String, Character> map = new HashMap<>();
            StringBuilder shapeBuilder = new StringBuilder();
            for (String ingredient : ingredients) {
                if (!map.containsKey(ingredient)) {
                    map.put(ingredient, (char) ch);
                    ch++;
                }
                shapeBuilder.append(map.get(ingredient));
            }
            if (shapeBuilder.length() == 4 || shapeBuilder.length() == 9) {
                int length = (int) Math.pow(shapeBuilder.length(), 0.5);
                String[] shape = new String[length];
                for (int i = 0; i < length; i++) {
                    shape[i] = shapeBuilder.charAt(i * 3) + String.valueOf(shapeBuilder.charAt(i * 3 + 1)) + shapeBuilder.charAt(i * 3 + 2);
                }
                recipe.shape(shape);
                int ingCount = 0;
                for (String ingredient : map.keySet()) {
                    try {
                        ingCount++;
                        String[] ingData = ingredient.split(":");
                        if (ingData.length > 1) {
                            recipe.setIngredient(map.get(ingredient), Material.getMaterial(ingData[0]), Byte.parseByte(ingData[1]));
                        } else {
                            recipe.setIngredient(map.get(ingredient), Material.getMaterial(ingredient));
                        }

                    } catch (IllegalArgumentException | NullPointerException e) {
                        HeadsPlus.getInstance().getLogger().warning("Unknown material " + ingredient + " when crafting " + id + " head.");
                        ingCount--;
                    }
                }
                if (ingCount > 0) {
                    try {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.addRecipe(recipe);
                            }
                        }.runTask(HeadsPlus.getInstance());
                    } catch (IllegalStateException ignored) {
                    }
                }
            } else {
                HeadsPlus.getInstance().getLogger().warning("Recipe " + id + " has a length of " + shapeBuilder.length() + " ingredients; 4 or 9 are required!");
            }

        } else {
            List<String> ingredients = crafting.getStringList(id + ".ingredients");
            int ingCount = 0;
            ShapelessRecipe recipe = HeadsPlus.getInstance().getNMS().getRecipe(head.getItemStack(), "hp_" + id);
            for (String ingredient : ingredients) {
                try {
                    ingCount++;
                    String[] ingData = ingredient.split(":");
                    if (ingData.length > 1) {
                        recipe.addIngredient(Material.getMaterial(ingData[0]), Byte.parseByte(ingData[1]));
                    } else {
                        recipe.addIngredient(Material.getMaterial(ingredient));
                    }

                } catch (IllegalArgumentException | NullPointerException e) {
                    HeadsPlus.getInstance().getLogger().warning("Unknown material " + ingredient + " when crafting " + id + " head.");
                    ingCount--;
                }
            }
            recipe.addIngredient(new ItemStack(Material.getMaterial(crafting.getString("base-item.material")), 1, (short) crafting.getInt("base-item.data")).getType());
            if (ingCount > 0) {
                try {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.addRecipe(recipe);
                        }
                    }.runTask(HeadsPlus.getInstance());
                } catch (IllegalStateException ignored) {
                }
            }
        }
    }
}
