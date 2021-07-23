package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHead;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsSearch extends HeadsSection {
    public HeadsSearch(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        String search = context.get("search").toLowerCase();
        List<Content> contents = new ArrayList<>();
        for (String headName : ConfigHeadsSelector.get().getBuyableHeads().keySet()) {
            ConfigHeadsSelector.BuyableHeadInfo headInfo = ConfigHeadsSelector.get().getBuyableHead(headName);
            final String name;
            try {
                name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', headInfo.getDisplayName())).toLowerCase().replaceAll("[^a-z]", "");
            } catch (NullPointerException | IllegalArgumentException ex) {
                if (!suppressWarnings) {
                    hp.getLogger().warning("Null display name for " + headName + "! (Error code: 12)");
                }
                continue;
            }
            if (name.contains(search)) {
                CustomHead head1 = new CustomHead(headName);
                head1.initNameAndLore(headName, player);
                contents.add(head1);
            }
        }
        return contents;
    }
}
