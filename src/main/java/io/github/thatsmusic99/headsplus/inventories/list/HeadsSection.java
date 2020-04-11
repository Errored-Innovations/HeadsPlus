package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHead;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsSection extends BaseInventory {
    public HeadsSection(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    @Override
    public String getDefaultTitle() {
        return null;
    }

    @Override
    public String getDefaultItems() {
        return null;
    }

    @Override
    public String getDefaultId() {
        return "headsection";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        HeadsPlusConfigCustomHeads hpch = HeadsPlus.getInstance().getHeadsXConfig();
        List<String> heads = hpch.sections.get(context.get("section"));
        List<Content> contents = new ArrayList<>();
        for (String head : heads) {
            contents.add(new CustomHead(hpch.getSkull(head)));
        }
        return contents;
    }
}
