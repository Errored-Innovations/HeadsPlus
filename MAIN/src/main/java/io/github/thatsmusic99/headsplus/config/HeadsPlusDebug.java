package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

public class HeadsPlusDebug extends ConfigSettings {

    private File logTimings = new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "debug" + File.separator + "logs" + File.separator + "timings-" + new java.text.SimpleDateFormat("MM_dd_yyyy").format(new java.util.Date (System.currentTimeMillis())) + ".txt");
    private HashMap<String, Long> timings;

    public HeadsPlusDebug() {
        this.conName = "debug";
        timings = new HashMap<>();
        enable(false);
    }

    @Override
    protected void load(boolean nullp) {
        File dir = new File(HeadsPlus.getInstance().getDataFolder() + File.separator + "debug" + File.separator + "logs" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!logTimings.exists()) {
            try {
                logTimings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getConfig().options().header("HeadsPlus by Thatsmusic99");
        getConfig().addDefault("timings.enabled", false);
        getConfig().addDefault("timings.send-to-console", false);
        getConfig().addDefault("logging.enabled", false);
        getConfig().addDefault("logging.send-to-console", false);
        for (IHeadsPlusCommand command : HeadsPlus.getInstance().getCommands()) {
            String name = command.getClass().getAnnotation(CommandInfo.class).commandname();
            getConfig().addDefault("command." + name + ".timings", false);
            getConfig().addDefault("command." + name + ".logging", false);
        }
        getConfig().addDefault("cache.death-events.entity-heads", true);
        getConfig().addDefault("cache.heads.sections", true);
        getConfig().options().copyDefaults(true);
        save();
    }

    public void logTimings(String name, DebugType type, long ms) {
        if (getConfig().getBoolean("timings.enabled")) {
            String time = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
            if (getConfig().getBoolean(type.name().toLowerCase() + "." + name + ".timings")) {
                try (FileWriter fw = new FileWriter(logTimings);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("[" + time + "] " + type.name() + " " + name + ": Took a total of " + ms + "ms!\n");
                    out.close();
                    if (getConfig().getBoolean("timings.send-to-console")) {
                        HeadsPlus.getInstance().getLogger().info("DEBUG: " + type.name() + " " + name + ": Took a total of " + ms + "ms!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void startTimings(CommandSender cs, String commandName) {
        if (getConfig().getBoolean("timings.enabled")) {
            if (getConfig().getBoolean("command." + commandName + ".timings")) {
                timings.put(cs.getName() + ":" + commandName, System.nanoTime());
            }
        }
    }

    public void stopTimings(CommandSender cs, String commandName) {
        long ms = System.nanoTime();
        if (getConfig().getBoolean("timings.enabled")) {
            if (getConfig().getBoolean("command." + commandName + ".timings")) {
                if (timings.containsKey(cs.getName() + ":" + commandName)) {
                    long start = timings.get(cs.getName() + ":" + commandName);
                    logTimings(commandName, DebugType.COMMAND, (ms - start) / 1000000);
                }
            }
        }
    }
    public void log() {

    }

    public enum DebugType {
        COMMAND(),
        EVENT()
    }
}
