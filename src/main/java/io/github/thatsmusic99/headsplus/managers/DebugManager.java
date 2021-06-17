package io.github.thatsmusic99.headsplus.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DebugManager {

    public static void checkForConditions(String name, HashMap<String, String> conditions) {

        for (UUID uuid : DebugConditions.openDebugTrackers.keySet()) {
            DebugConditions debugging = DebugConditions.openDebugTrackers.get(uuid);
            if (debugging.listener.equals(name)) {
                for (String key : debugging.conditions.keySet()) {
                    if (!conditions.get(key).equals(debugging.conditions.get(key))) {
                        return;
                    }
                }
                CommandSender listener = getSender(uuid);
                listener.sendMessage(ChatColor.GRAY + "━━━━━━━━━━━━" + ChatColor.DARK_GRAY + " ❰ " + ChatColor.RED + "HEADSPLUS OUTPUT" + ChatColor.DARK_GRAY + " ❱ " + ChatColor.GRAY + "━━━━━━━━━━━━");
                listener.sendMessage(ChatColor.RED + "Event " + ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + name);
                for (String key : conditions.keySet()) {
                    listener.sendMessage(ChatColor.RED + key + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + conditions.get(key));
                }
                listener.sendMessage(ChatColor.GRAY + "━━━━━━━━━━━━" + ChatColor.DARK_GRAY + " ❰ " + ChatColor.RED + "OUTPUT END" + ChatColor.DARK_GRAY + " ❱ " + ChatColor.GRAY + "━━━━━━━━━━━━");
            }
        }
    }

    public static void addListener(CommandSender listener, String listenerClass, HashMap<String, String> conditions) {
        new DebugConditions(getUUID(listener), listenerClass, conditions);
    }

    public static void removeListener(CommandSender listener) {
        DebugConditions.openDebugTrackers.remove(getUUID(listener));
    }

    public static class DebugConditions {
        private String listener;
        private HashMap<String, String> conditions;
        protected static HashMap<UUID, DebugConditions> openDebugTrackers = new HashMap<>();

        public DebugConditions(UUID sender, String listener, HashMap<String, String> conditions) {
            this.listener = listener;
            this.conditions = conditions;
            openDebugTrackers.put(sender, this);
        }

    }

    private static UUID getUUID(CommandSender listener) {
        UUID uuid;
        if (listener instanceof Player) {
            uuid = ((Player) listener).getUniqueId();
        } else {
            uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        }
        return uuid;
    }

    private static CommandSender getSender(UUID uuid) {
        if (uuid.toString().equals("00000000-0000-0000-0000-000000000001")) {
            return Bukkit.getConsoleSender();
        } else {
            return Bukkit.getPlayer(uuid);
        }
    }
}
