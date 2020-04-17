package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.SellheadHead;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellheadCategory extends BaseInventory {

    public SellheadCategory(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public SellheadCategory() {}
    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Sellhead: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "GGGGGGGGGGCCCCCCCGGCCCCCCCGGCCCCCCCGGCCCCCCCG<{[BMN]}>";
    }

    @Override
    public String getId() {
        return "sellhead-category";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        switch (context.get("section")) { // ignore
            case "mobs":
                for (String str : DeathEvents.getSellheadCache().keySet()) {
                    contents.add(new SellheadHead(DeathEvents.getSellheadCache().get(str), str));
                }
                break;
            case "mining": // Guess what
            case "farming":
            case "fishing":
            case "crafting":
                break;
        }
        return contents;
    }
}
