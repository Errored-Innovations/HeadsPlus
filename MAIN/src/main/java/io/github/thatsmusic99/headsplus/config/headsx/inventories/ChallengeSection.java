package io.github.thatsmusic99.headsplus.config.headsx.inventories;

import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;

public class ChallengeSection extends HeadInventory {

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: {section}";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGGGGGG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "<{[BMN]}>";
    }

    @Override
    public String getDefaultId() {
        return "challenges:{section}";
    }

    @Override
    public String getName() {
        return "challenge-section";
    }
}
