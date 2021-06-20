package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
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

    public HeadsSection() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Heads: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "FGGGSGGGKGCCCCCCCGGCCCCCCCGGCCCCCCCGGCCCCCCCG<{[BMN]}>";
    }

    @Override
    public String getId() {
        return "headsection";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<String> heads = ConfigCustomHeads.get().sections.get(context.get("section"));
        List<Content> contents = new ArrayList<>();
        for (String head : heads) {
            CustomHead head1 = new CustomHead(head);
            head1.initNameAndLore(head, player);
            contents.add(head1);
        }
        return contents;
    }
}
