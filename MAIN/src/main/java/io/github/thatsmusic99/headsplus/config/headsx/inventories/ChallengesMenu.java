package io.github.thatsmusic99.headsplus.config.headsx.inventories;

import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;

public class ChallengesMenu extends HeadInventory {
    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: Menu";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGGGGGG" +
                "GAAAAAAAG" +
                "GAEARAZAG" +
                "GAAVAJAAG" +
                "GAAAAAAAG" +
                "GGGGXNGGG:" +
                "GGGGGGGGG" +
                "GAAAAAAAG" +
                "GATAIAPAG" +
                "GAAOADAAG" +
                "GAAAAAAAG" +
                "GGGBXNGGG";
    }

    @Override
    public String getDefaultId() {
        return "challenges-menu";
    }

    @Override
    public String getName() {
        return "challenges-menu";
    }
}
