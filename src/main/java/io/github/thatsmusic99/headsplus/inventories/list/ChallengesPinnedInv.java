package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.Challenge;
import io.github.thatsmusic99.headsplus.managers.ChallengeManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChallengesPinnedInv extends BaseInventory {

    public ChallengesPinnedInv(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public ChallengesPinnedInv() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: Pinned";
    }

    @Override
    public String getDefaultItems() {
        return "PGGGGGGGGGCCCCCCCGGCCCCCCCGGCCCCCCCGGCCCCCCCG<{[BMN]}>";
    }

    @Override
    public String getId() {
        return "pinned-challenges";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        HPPlayer hpPlayer = HPPlayer.getHPPlayer(player.getUniqueId());
        List<Content> contents = new ArrayList<>();
        for (String challenge : hpPlayer.getPinnedChallenges()) {
            contents.add(new Challenge(ChallengeManager.get().getChallengeByName(challenge), player));
        }
        return contents;
    }
}
