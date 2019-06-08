package io.github.thatsmusic99.headsplus.locale;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class LocaleManager {

    private static LocaleManager instance;
    private static Locale locale;
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    public void setupLocale() {
        instance = this;
        try {
            try {
                try {
                    setLocale((Locale) Class.forName("io.github.thatsmusic99.headsplus.locale." + hpc.getString("locale").toLowerCase()).getConstructor().newInstance());
                } catch (InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (!locale.active()) {
                    HeadsPlus.getInstance().getLogger().info("[HeadsPlus] Language requested is being developed. Setting to English whilst it is.");
                    setLocale(new en_uk());
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Logger log = HeadsPlus.getInstance().getLogger();
                log.severe("Failed to load locale!");
                ConfigurationSection c = HeadsPlus.getInstance().getConfiguration().getMechanics();
                if (c.getBoolean("debug.print-stacktraces-in-console")) {
                    e.printStackTrace();
                }
                if (c.getBoolean("debug.create-debug-files")) {
                    log.severe("HeadsPlus has failed to set the locale. An error report has been made in /plugins/HeadsPlus/debug");
                    try {
                        String s = new DebugFileCreator().createReport(e, "Locale setup");
                        log.severe("Report name: " + s);
                        log.severe("Please submit this report to the developer at one of the following links:");
                        log.severe("https://github.com/Thatsmusic99/HeadsPlus/issues");
                        log.severe("https://discord.gg/nbT7wC2");
                        log.severe("https://www.spigotmc.org/threads/headsplus-1-8-x-1-12-x.237088/");
                    } catch (IOException e1) {
                        if (c.getBoolean("debug.print-stacktraces-in-console")) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            HeadsPlus.getInstance().getLogger().warning("Failed to load the locale settings! This is caused by an invalid name provided. Setting locale to en_uk...");
            setLocale(new en_uk());
        }
    }

    public static LocaleManager getInstance() {
        return instance;
    }

    public static Locale getLocale() {
        return locale;
    }
    private static void setLocale(Locale l) {
        locale = l;
    }

}
