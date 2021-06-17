package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.DebugManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;

public class DebugVerbose {

    public static void fire(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String event = args[2];
            if (event.equalsIgnoreCase("off")) {
                DebugManager.removeListener(sender);
                sender.sendMessage(HeadsPlusMessagesManager.get().getString("commands.debug.verbose.disabled", sender));
            } else {
                String[] arguments = new String[0];
                if (args.length > 3) {
                    arguments = args[3].split(",");
                }
                DebugManager.addListener(sender, event, stringToConditions(arguments));
                sender.sendMessage(HeadsPlusMessagesManager.get().getString("commands.debug.verbose.enabled", sender)
                        .replaceAll("\\{event}", args[2]).replaceAll("\\{args}", arguments.length == 0 ? "(none)" : args[3]));
            }

        }
    }

    // hp debug verbose EntityDeathEvent spawn-cause=SPAWN_EGG,entity-type=PIG
    public static List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        switch (args.length) {
            case 3:
                List<String> events = new ArrayList<>(HeadsPlusEventExecutor.getEvents().keySet());
                events.add("off");
                StringUtil.copyPartialMatches(args[2], events, results);
                break;
            case 4:
                HeadsPlusListener<?> event = HeadsPlusEventExecutor.getEvents().get(args[2]);
                if (event == null) return results;
                String[] currentArgs = args[3].split(",");
                HashMap<String, String> conditions = stringToConditions(currentArgs);
                int modifyingArg = currentArgs.length - 1;
                String currentArg;
                if (args[3].indexOf(",") == args[3].length() - 1) {
                    if (args[3].length() > 0) {
                        modifyingArg = currentArgs.length;
                    }
                    currentArg = "";
                } else {
                    currentArg = currentArgs[modifyingArg];
                }
                StringBuilder builder = new StringBuilder();
                Set<String> possibleSelections = new HashSet<>();
                // No equal sign, selecting key
                if (currentArg.contains("=")) {
                    String arg = currentArg.split("=")[0];
                    for (int i = 0; i < modifyingArg; i++) {
                        builder.append(currentArgs[i]).append(",");
                    }
                    for (String key : event.getPossibleData(arg)) {
                        possibleSelections.add(builder.toString() + arg + "=" + key + ",");
                    }
                } else {
                    for (int i = 0; i < modifyingArg; i++) {
                        builder.append(currentArgs[i]).append(",");
                    }
                    for (String key : event.getPossibleValues().keySet()) {
                        if (!conditions.containsKey(key)) {
                            possibleSelections.add(builder.toString() + key + "=");
                        }
                    }
                }
                StringUtil.copyPartialMatches(args[3], possibleSelections, results);
                break;

        }
        return results;
    }

    private static HashMap<String, String> stringToConditions(String[] args) {
        HashMap<String, String> conditions = new HashMap<>();
        for (String argument : args) {
            try {
                String[] option = argument.split("=");
                conditions.put(option[0], option[1]);
            } catch (IndexOutOfBoundsException ignored) {

            }

        }
        return conditions;
    }
}
