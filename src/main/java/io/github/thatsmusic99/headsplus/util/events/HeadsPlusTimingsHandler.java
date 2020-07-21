package io.github.thatsmusic99.headsplus.util.events;

import co.aikar.timings.Timings;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.papermc.lib.PaperLib;
import org.spigotmc.CustomTimingsHandler;

public class HeadsPlusTimingsHandler {

    private final String name;
    private CustomTimingsHandler handler;

    public HeadsPlusTimingsHandler(String name) {
        this.name = name;

    }

    public void start() {
        if (PaperLib.isPaper()) {
            if (Timings.isTimingsEnabled()) {
                Timings.of(HeadsPlus.getInstance(), name).startTiming();
            }
        } else {
            handler = new CustomTimingsHandler("CHRONOS: " + name);
            handler.startTiming();
        }
    }

    public void finish() {
        if (PaperLib.isPaper()) {
            if (Timings.isTimingsEnabled()) {
                Timings.of(HeadsPlus.getInstance(), name).stopTiming();
            }
        } else {
            handler.stopTiming();
        }
    }
}
