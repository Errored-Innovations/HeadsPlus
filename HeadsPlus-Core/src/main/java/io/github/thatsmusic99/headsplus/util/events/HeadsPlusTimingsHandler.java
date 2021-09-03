package io.github.thatsmusic99.headsplus.util.events;

import io.papermc.lib.PaperLib;
import org.spigotmc.CustomTimingsHandler;

public class HeadsPlusTimingsHandler {

    private final String name;
    private CustomTimingsHandler handler;

    public HeadsPlusTimingsHandler(String name) {
        this.name = name;

    }

    public void start() {
        if (!PaperLib.isPaper()) {
            handler = new CustomTimingsHandler("HeadsPlus: " + name);
            handler.startTiming();
        }
    }

    public void finish() {
        if (!PaperLib.isPaper()) {
            handler.stopTiming();
        }
    }
}
