package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHeadSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import org.bukkit.entity.Player;

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
        ConfigHeadsSelector selector = ConfigHeadsSelector.get();
        for (ConfigHeadsSelector.SectionInfo section : selector.getSections().values()) {
            try {
                contents.add(new CustomHeadSection(section.buildSection(), section.getId()));
            } catch (IllegalStateException ex) {
                HeadsPlus.get().getLogger().warning(ex.getMessage());
            }
        }
        return contents;
    }
}