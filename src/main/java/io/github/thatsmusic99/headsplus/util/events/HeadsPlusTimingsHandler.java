package io.github.thatsmusic99.headsplus.util.events;

import co.aikar.timings.Timings;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.spigotmc.CustomTimingsHandler;

public class HeadsPlusTimingsHandler {

    private static boolean paper = false;
    private String name;
    private CustomTimingsHandler handler;

    public HeadsPlusTimingsHandler(String name) {
        this.name = name;
    }

    public static void init() {
        HeadsPlus hp = HeadsPlus.getInstance();
        if (hp.getServer().getName().equals("Paper")) {
            paper = true;
            hp.getLogger().info("Successfully hooked with Paper!");
        }
    }

    public void start() {
        if (paper) {
            if (Timings.isTimingsEnabled()) {
                Timings.of(HeadsPlus.getInstance(), name).startTiming();
            }
        } else {
            handler = new CustomTimingsHandler("CHRONOS: " + name);
            handler.startTiming();
        }
    }

    public void finish() {
        if (paper) {
            if (Timings.isTimingsEnabled()) {
                Timings.of(HeadsPlus.getInstance(), name).stopTiming();
            }
        } else {
            handler.stopTiming();
        }
    }
}
