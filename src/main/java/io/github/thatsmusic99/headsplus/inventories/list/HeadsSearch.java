package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHead;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsSearch extends BaseInventory {
    public HeadsSearch(Player player, HashMap<String, String> context) {
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
        return "headsection";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        HeadsPlusConfigCustomHeads hpch = HeadsPlus.getInstance().getHeadsXConfig();
        String search = context.get("search").toLowerCase();
        List<Content> contents = new ArrayList<>();
        for (String head : hpch.headsCache.keySet()) {
            final String name;
            try {
                name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', hpch.getConfig().getString("heads." + head + ".displayname"))).toLowerCase().replaceAll("[^a-z]", "");
            } catch (NullPointerException ex) {
                hp.getLogger().warning("Null display name for " + head + "!");
                continue;
            }
            if (name.contains(search)) {
                CustomHead head1 = new CustomHead(head);
                head1.initNameAndLore(head, player);
                contents.add(head1);
            }
        }
        return contents;
    }
}
