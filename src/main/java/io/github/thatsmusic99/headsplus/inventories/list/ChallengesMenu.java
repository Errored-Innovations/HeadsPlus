package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChallengesMenu extends BaseInventory {

    public ChallengesMenu(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public ChallengesMenu() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "GGGGGGGGGGAAAAAAAGGACACACAGGAACACAAGGAAAAAAAGGGGBXNGGG";
    }

    @Override
    public String getId() {
        return "challenges-menu";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> sections = new ArrayList<>();
        for (ChallengeSection section : HeadsPlus.get().getChallengeSections()) {
            io.github.thatsmusic99.headsplus.inventories.icons.content.ChallengeSection icon = new io.github.thatsmusic99.headsplus.inventories.icons.content.ChallengeSection(section);
            icon.initNameAndLore("challenge-section", player);
            sections.add(icon);
        }
        return sections;
    }
}
