package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChallengesSection extends BaseInventory {
    public ChallengesSection(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return null;
    }

    @Override
    public String getDefaultId() {
        return "challenge-section";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        String section = context.get("section");
        List<Content> content = new ArrayList<>();
        for (Challenge challenge : HeadsPlus.getInstance().getSectionByName(section).getChallenges()) {
            io.github.thatsmusic99.headsplus.inventories.icons.content.Challenge icon = new io.github.thatsmusic99.headsplus.inventories.icons.content.Challenge(challenge, player);
            icon.initNameAndLore("challenge", player);
            content.add(icon);
        }
        return content;
    }
}
