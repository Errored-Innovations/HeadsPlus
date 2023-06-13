package io.github.thatsmusic99.headsplus.managers;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class MiniMessageManager {

    public static boolean canUse() {
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            return PaperLib.isPaper();
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static void sendMessage(CommandSender sender, String message) {

        // Take the message and run it through the legacy translator
        TextComponent component = LegacyComponentSerializer.legacySection().deserialize(message);
        String miniMessage = MiniMessage.miniMessage().serialize(component);

        // Get rid of escaped brackets, you baffoon
        miniMessage = miniMessage.replace("\\<", "<").replace("\\>", ">");

        // Produce component to be sent
        Component newMessage = MiniMessage.miniMessage().deserialize(miniMessage);

        // Send the message
        sender.sendMessage(newMessage);
    }
}
