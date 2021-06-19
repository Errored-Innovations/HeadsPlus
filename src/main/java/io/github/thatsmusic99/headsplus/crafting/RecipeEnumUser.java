package io.github.thatsmusic99.headsplus.crafting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
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
        ConfigurationSection headSec = crafting.getConfigurationSection(id);
        if (headSec.getStringList("ingredients").isEmpty()) return;
	    // Get the Central ID, firstly
	    String headId = headSec.getString("head");
        Head head;
        try {
            // Create the head object
            head = new EntityHead(headSec.getString("sellhead-id"), Material.PLAYER_HEAD);
            // Register the Sellhead ID
            SellHead.registerHeadID(headSec.getString("sellhead-id"));
            // Apply the price
            head.withPrice(hp.getCraftingConfig().getPrice(id));
        } catch (NullPointerException ignored) {
            // If there's no sellhead ID, stop there
	        head = new Head(id);
        }

        head.withDisplayName(hp.getCraftingConfig().getDisplayName(id))
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
        if (headSec.getBoolean("shaped")) {
            List<String> ingredients = headSec.getStringList("ingredients");
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(HeadsPlus.getInstance(), "crafting_" + id), head.getItemStack());
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
            if (shapeBuilder.length() != 1 && shapeBuilder.length() != 4 && shapeBuilder.length() != 9) {
                if (!map.containsKey("AIR")) {
                    ch++;
                    map.put("AIR", (char) ch);
                }
                int limit = 4;
                if (shapeBuilder.length() > 4) {
                    limit = 9;
                }
                for (int length = shapeBuilder.length(); length < limit; length++) {
                    shapeBuilder.append(map.get("AIR"));
                }
            }
            int length = (int) Math.pow(shapeBuilder.length(), 0.5);
            String[] shape = new String[length];
            for (int i = 0; i < length; i++) {
                StringBuilder shapeL = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    shapeL.append(shapeBuilder.charAt(i * length + j));

                }
                shape[i] = shapeL.toString();
            }
            recipe.shape(shape);
            int ingCount = 0;
            for (String ingredient : map.keySet()) {
                try {
                    String[] ingData = ingredient.split(":");
                    if (ingData.length > 1) {
                        recipe.setIngredient(map.get(ingredient), Material.getMaterial(ingData[0]), Byte.parseByte(ingData[1]));
                    } else {
                        recipe.setIngredient(map.get(ingredient), Material.getMaterial(ingredient));
                    }
                    if (!ingredient.equalsIgnoreCase("AIR")) {
                        ingCount++;
                    }
                } catch (NullPointerException e) {
                    HeadsPlus.getInstance().getLogger().warning("Unknown material " + ingredient + " when crafting " + id + " head.");
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
            List<String> ingredients = headSec.getStringList("ingredients");
            int ingCount = 0;
            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(HeadsPlus.getInstance(), "crafting_" + id), head.getItemStack());
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
            if (crafting.getBoolean("base-item.use-base-item")) {
                recipe.addIngredient(new ItemStack(Material.getMaterial(crafting.getString("base-item.material")), 1, (short) crafting.getInt("base-item.data")).getType());
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
        }
    }
}
