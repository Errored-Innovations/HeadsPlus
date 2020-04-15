package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
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

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Heads: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return null;
    }

    @Override
    public String getDefaultId() {
        return "favourites";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        HeadsPlusConfigCustomHeads hpch = HeadsPlus.getInstance().getHeadsXConfig();
        HPPlayer hpPlayer = HPPlayer.getHPPlayer(player);
        List<Content> contents = new ArrayList<>();
        for (String head : hpch.headsCache.keySet()) {
            if (hpPlayer.hasHeadFavourited(head)) {
                CustomHead head1 = new CustomHead(head);
                head1.initNameAndLore(head, player);
                contents.add(head1);
            }
        }
        return contents;
    }
}
