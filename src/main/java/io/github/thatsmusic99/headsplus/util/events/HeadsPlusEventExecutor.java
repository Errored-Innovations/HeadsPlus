package io.github.thatsmusic99.headsplus.util.events;

import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

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
                    HeadsPlusException ex1 = new HeadsPlusException(ex, hpListener.getData());
                    DebugFileCreator.createReport(ex1);
                }
            }
        }
    }
}
