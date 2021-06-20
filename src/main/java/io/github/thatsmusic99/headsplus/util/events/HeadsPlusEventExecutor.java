package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.managers.DebugManager;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.logging.Logger;

public class HeadsPlusEventExecutor implements EventExecutor {

    //
    private Class<? extends Event> eventClass;
    private HeadsPlusTimingsHandler handler;
    private String name;
    private static HashMap<String, HeadsPlusListener<?>> events = new HashMap<>();

    public HeadsPlusEventExecutor(Class<? extends Event> clazz, String name, HeadsPlusListener<?> listener) {
        eventClass = clazz;
        handler = new HeadsPlusTimingsHandler(name);
        this.name = name;
        events.put(name, listener);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (listener instanceof HeadsPlusListener) {
            // Make sure it's a class we're listening for
            if (eventClass.isAssignableFrom(event.getClass())) {
                HeadsPlusListener<Event> hpListener = (HeadsPlusListener<Event>) listener;
                try {
                    handler.start();
                    hpListener.onEvent(event);
                    DebugManager.checkForConditions(name, hpListener.getData());
                    handler.finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    HeadsPlusException ex1 = new HeadsPlusException(ex, hpListener.getData());
                    String s = DebugFileCreator.createReport(ex1);
                    handler.finish();
                    Logger log = HeadsPlus.get().getLogger();
                    log.severe("HeadsPlus has failed to execute this task. An error report has been made in /plugins/HeadsPlus/debug");
                    log.severe("Report name: " + s);
                    log.severe("Please submit this report to the developer at one of the following links:");
                    log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
                    log.severe("https://discord.gg/eu8h3BG");
                    log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");
                }
            }
        }
    }

    public static HashMap<String, HeadsPlusListener<?>> getEvents() {
        return events;
    }
}
