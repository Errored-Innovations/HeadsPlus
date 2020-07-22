package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class HeadsPlusEventExecutor implements EventExecutor {

    //
    private Class<? extends Event> eventClass;
    private HeadsPlusTimingsHandler handler;

    public HeadsPlusEventExecutor(Class<? extends Event> clazz, String name) {
        eventClass = clazz;
        handler = new HeadsPlusTimingsHandler(name);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (listener instanceof HeadsPlusListener) {
            // Make sure it's a class we're listening for
            if (eventClass.isAssignableFrom(event.getClass())) {
                HeadsPlusListener<Event> hpListener = (HeadsPlusListener<Event>) listener;
                try {
                    handler.start();
                    hpListener.onEvent(event);
                    handler.finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    HeadsPlusException ex1 = new HeadsPlusException(ex, hpListener.getData());
                    String s = DebugFileCreator.createReport(ex1);
                    handler.finish();
                    Logger log = HeadsPlus.getInstance().getLogger();
                    log.severe("HeadsPlus has failed to execute this task. An error report has been made in /plugins/HeadsPlus/debug");
                    log.severe("Report name: " + s);
                    log.severe("Please submit this report to the developer at one of the following links:");
                    log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
                    log.severe("https://discord.gg/nbT7wC2");
                    log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");
                }
            }
        }
    }
}
