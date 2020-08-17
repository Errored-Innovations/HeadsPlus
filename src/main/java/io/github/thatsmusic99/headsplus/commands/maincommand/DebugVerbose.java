package io.github.thatsmusic99.headsplus.commands.maincommand;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class DebugVerbose {

    private static final HashMap<String, DebugConditions> conditions = new HashMap<>();

    public static void fire(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String event = args[2];

        }
    }

    public static class DebugConditions extends HashMap<String, String> {

        private UUID uuid = null;

        public DebugConditions(HashMap<String, String> conditions, CommandSender sender) {
            if (sender instanceof Player) {
                uuid = ((Player) sender).getUniqueId();
            }
            for (String str : conditions.keySet()) {
                put(str, conditions.get(str));
            }
        }

        public boolean conditionsMet(HashMap<String, String> conditions) {
            for (String str : keySet()) {
                if (conditions.containsKey(str)) {
                    if (!conditions.get(str).equals(get(str))) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void sendResults(HashMap<String, String> conditions) {
            if (!conditionsMet(conditions)) return;
            List<String> results = new ArrayList<>();
            for (String str : conditions.keySet()) {
                results.add(str + ": " + conditions.get(str));
            }
            if (uuid != null) {
                FancyMessage message = new FancyMessage().text("Debug Results")
                        .tooltip(results);
                message.send(Bukkit.getPlayer(uuid));
            } else {
                Bukkit.getConsoleSender().sendMessage("Debug results:");
                for (String str : results) {
                    Bukkit.getConsoleSender().sendMessage(str);
                }
            }
        }
    }

    public static List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();
        switch (args.length) {
            case 3:
                StringUtil.copyPartialMatches(args[2], Arrays.asList("HPBlockPlaceEvent", "HPEntityDeathEvent",
                        "HPEntitySpawnEvent", "HPHeadInteractEvent", "HPPlayerJoinEvent"), results);
                break;
            case 4:

        }
        return results;
    }
}
