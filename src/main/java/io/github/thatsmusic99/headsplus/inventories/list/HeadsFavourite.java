package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHead;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsFavourite extends BaseInventory {
    public HeadsFavourite(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public HeadsFavourite() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Heads: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "GGGGSGGGKGCCCCCCCGGCCCCCCCGGCCCCCCCGGCCCCCCCG<{[BMN]}>";
    }

    @Override
    public String getId() {
        return "favourites";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        HPPlayer hpPlayer = HPPlayer.getHPPlayer(player.getUniqueId());
        List<Content> contents = new ArrayList<>();
        for (String head : hpPlayer.getFavouriteHeads()) {
            if (ConfigHeadsSelector.get().getBuyableHead(head) == null) {
                HeadsPlus.get().getLogger().warning(head + " is not registered as a buyable head, skipping...");
                continue;
            }
            CustomHead head1 = new CustomHead(head);
            head1.initNameAndLore(head, player);
            contents.add(head1);
        }
        return contents;
    }
}
