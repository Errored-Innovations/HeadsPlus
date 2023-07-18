package io.github.thatsmusic99.headsplus.hooks.shopguiplus;

import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import net.brcdev.shopgui.provider.item.ItemProvider;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class HeadsPlusItemProvider extends ItemProvider {

    public HeadsPlusItemProvider() {
        super("headsplus");
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return itemStack.getType() == Material.PLAYER_HEAD;
    }

    @Override
    public ItemStack loadItem(ConfigurationSection configurationSection) {

        // Get the HeadsPlus section
        if (!configurationSection.contains("headsPlus")) return null;
        ConfigurationSection section = configurationSection.getConfigurationSection("headsPlus");

        // Attempt to get a series of options
        String mob = section.getString("mob");
        String id = section.getString("id");
        String conditions = section.getString("conditions", "default");

        if (id == null) return null;

        if (mob == null) {
            return HeadManager.get().getHeadInfo(id).forceBuildHead();
        }

        ItemStack item = HeadManager.get().getHeadInfo(id).forceBuildHead();
        PersistenceManager.get().setSellType(item, "mobs_" + mob.toUpperCase() + ":" + conditions + ":" + id);
        PersistenceManager.get().setSellable(item, true);
        return item;
    }

    @Override
    public boolean compare(ItemStack itemStack, ItemStack itemStack1) {

        String id1 = PersistenceManager.get().getSellType(itemStack);
        String id2 = PersistenceManager.get().getSellType(itemStack1);

        // If they aren't equal, then nope
        if (!id1.equals(id2)) return false;

        // If both aren't empty, use those
        if (!id1.isEmpty()) return true;

        // Check mask IDs
        String mask1 = PersistenceManager.get().getMaskType(itemStack);
        String mask2 = PersistenceManager.get().getMaskType(itemStack1);

        // If they aren't equal, nopers
        if (!mask1.equals(mask2)) return false;

        // If both aren't empty, use those
        if (!mask1.isEmpty()) return true;

        // Compare texture using isSimilar
        return itemStack.isSimilar(itemStack1);
    }
}
