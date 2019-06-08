package io.github.thatsmusic99.headsplus.crafting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeEnumUser {

    private final HeadsPlus hp = HeadsPlus.getInstance();
	private final FileConfiguration crafting = hp.getCraftingConfig().getConfig();

	public RecipeEnumUser() {
	    addEnumToConfig();
    }

	private void addEnumToConfig() {

            NMSManager nms = hp.getNMS();
            for (EntityType key : DeathEvents.heads.keySet()) {
                try {
                    String id = key.name().toLowerCase().replaceAll("_", "");
                    HashMap<String, List<ItemStack>> heads = DeathEvents.heads.get(key);
                    for (String str : heads.keySet()) {
                        if (!heads.get(str).isEmpty()) {
                            ItemStack i = heads.get(str).get(0);
                            ShapelessRecipe recipe;
                            List<String> ingredients;
                            if (str.equalsIgnoreCase("default")) {
                                recipe = nms.getRecipe(i, "hp_" + id);
                                ingredients = crafting.getStringList(id + ".ingredients");
                            } else {
                                recipe = nms.getRecipe(i, "hp_" + str + id);
                                ingredients = crafting.getStringList(id + "." + str + ".ingredients");
                            }
                            List<String> ingrs = new ArrayList<>();
                            for (String key2 : ingredients) {
                                try {
                                    recipe.addIngredient(Material.getMaterial(key2));
                                    ingrs.add(key2);
                                } catch (IllegalArgumentException | NullPointerException e) {
                                    HeadsPlus.getInstance().getLogger().warning("Unknown material " + key2 + " when crafting " + id + " head.");
                                }
                            }
                            recipe.addIngredient(new ItemStack(Material.getMaterial(crafting.getString("base-item.material")), 1, (short) crafting.getInt("base-item.data")).getType());
                            if (ingrs.size() > 0) {
                                try {
                                    Bukkit.addRecipe(recipe);
                                } catch (IllegalStateException ignored) {
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    HeadsPlus.getInstance().getLogger().severe("Error thrown creating head for " + key + ". Please check the report for details.");
                    new DebugPrint(e, "Startup (Crafting)", false, null);
                }
            }
	}
}
