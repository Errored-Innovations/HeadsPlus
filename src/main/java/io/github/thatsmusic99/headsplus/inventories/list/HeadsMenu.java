package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHeadSection;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsMenu extends BaseInventory {

    public HeadsMenu(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public HeadsMenu() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Heads: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "FGGGSGGGK" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "<{[BXN]}>";
    }

    @Override
    public String getId() {
        return "headmenu";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        HeadsPlusConfigCustomHeads headsConfig = HeadsPlus.getInstance().getHeadsXConfig();
        for (HeadsPlusConfigCustomHeads.SectionInfo section : headsConfig.sectionsCache.values()) {
            if (section.isEnabled()) {
                try {
                    CustomHeadSection section1 = new CustomHeadSection(section);
                    if (section1.getItemStack() == null || section1.getItemStack().getType() == Material.AIR) continue;
                    section1.initNameAndLore(null, player);
                    contents.add(section1);
                } catch (NullPointerException ignored) {
                    ignored.printStackTrace();
                }

            }
        }
        return contents;
    }
}