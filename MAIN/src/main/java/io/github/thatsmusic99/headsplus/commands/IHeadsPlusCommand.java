package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface IHeadsPlusCommand {

    String isCorrectUsage(String[] args, CommandSender sender);

    String getCmdDescription(CommandSender sender);

    boolean fire(String[] args, CommandSender sender);

    default String[] advancedUsages() {
        return new String[0];
    }

    default void printDebugResults(HashMap<String, Boolean> results, boolean success) {
        HeadsPlus hp = HeadsPlus.getInstance();
        if (getClass().isAnnotationPresent(CommandInfo.class)) {
            hp.debug("- Tests for " + getClass().getAnnotation(CommandInfo.class).commandname() + " were " + (success ? "" : "not ") + "passed!", 1);
            for (String r : results.keySet()) {
                hp.debug("- " + r + ": " + results.get(r), 3);
            }
        }

    }
}
