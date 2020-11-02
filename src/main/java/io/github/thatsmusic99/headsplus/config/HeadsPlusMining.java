package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;

public class HeadsPlusMining extends ConfigSettings {

    private static HeadsPlusMessagesManager messages;

    public HeadsPlusMining() {
        this.conName = "mining";
        messages = HeadsPlus.getInstance().getMessagesConfig();
        enable();
    }


}
