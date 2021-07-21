package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class ConfigInteractions extends HPConfig {

    private static ConfigInteractions instance;

    public ConfigInteractions() {
        super("interactions.yml");
        instance = this;
    }

    /*
    TODO Set up new interactions configuration.
    Sections include:
    Default message
    Per-player message
    Per-texture message
    Location-specified messages
     */
    @Override
    public void loadDefaults() {
        double version = getDouble("version");
        if (version < 0.1) {
            // Default values
            set("version", 0.1);
            addComment("defaults", "This section outlines the default values for heads not specified in the special section.");
            addDefault("defaults.message", "{msg_event.head-interact-message}");
            addDefault("defaults.consonant-message", "{msg_event.head-mhf-interact-message}");
            addDefault("defaults.vowel-message", "{msg_event.head-mhf-interact-message-2}");
            addDefault("defaults.commands", Lists.newArrayList());

            makeSectionLenient("special");
            addComment("special", "This is the section where you can specify unique interactions with heads.\n" +
                    "These can be specified with location, name and texture.\n" +
                    "Locations are placed at the highest priority and are formatted as 0x0y0zworld_name. (Replace the 0s with the coordinates you want and world_name with the world's name.)\n" +
                    "Names, or names stored inside the skull, are placed at the following priority. It is as simple as specifying your own name, e.g. Thatsmusic99.\n" +
                    "Texture checks are placed at the lowest priority. These can be Base64 strings, Minecraft Textures/Education URLs or skin hashes.");
            // Pre-made names
            for (String str : Arrays.asList("MHF_CoconutB", "MHF_CoconutG", "MHF_Present1", "MHF_Present2", "MHF_TNT", "MHF_Cactus",
                    "MHF_Chest", "MHF_Melon", "MHF_TNT2")) {
                addExample("special.names." + str + ".message", "{consonant-message}");
                addExample("special.names." + str + ".commands", Lists.newArrayList());
            }

            for (String str : Arrays.asList("MHF_OakLog", "MHF_ArrowUp", "MHF_ArrowDown", "MHF_ArrowRight", "MHF_ArrowLeft")) {
                addExample("special.names." + str + ".message", "{vowel-message}");
                addExample("special.names." + str + ".commands", Lists.newArrayList());
            }

            addExample("special.names.MHF_CoconutB.name", "Brown Coconut");
            addExample("special.names.MHF_CoconutG.name", "Green Coconut");
            addExample("special.names.MHF_OakLog.name", "Oak Log");
            addExample("special.names.MHF_Present1.name", "Present");
            addExample("special.names.MHF_Present2.name", "Present");
            addExample("special.names.MHF_TNT.name", "TNT");
            addExample("special.names.MHF_TNT2.name", "TNT");
            addExample("special.names.MHF_ArrowUp.name", "Arrow Pointing Up");
            addExample("special.names.MHF_ArrowDown.name", "Arrow Pointing Down");
            addExample("special.names.MHF_ArrowRight.name", "Arrow Pointing Right");
            addExample("special.names.MHF_ArrowLeft.name", "Arrow Pointing Left");
            addExample("special.names.MHF_Cactus.name", "Cactus");
            addExample("special.names.MHF_Chest.name", "Chest");
            addExample("special.names.MHF_Melon.name", "Melon");

            // hahahaha
            addExample("special.names.Thatsmusic99.message", "{header} oh god it's error, run");
            addExample("special.names.Thatsmusic99.name", "");
            addExample("special.names.Thatsmusic99.commands", Lists.newArrayList());

            addExample("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.name", "???");
            addExample("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.message", "{header} what???");
            addExample("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.commands", Lists.newArrayList());

            addExample("special.locations.0x0y0zworld.name", "The Void");
        }
    }

    public static ConfigInteractions get() {
        return instance;
    }

    public CompletableFuture<String> getMessageForHead(Skull skull, Player receiver) {
        return CompletableFuture.supplyAsync(() -> {
            Location location = skull.getLocation();
            String locationStr = location.getBlockX() + "x" + location.getBlockY() + "y" + location.getBlockZ() + "z" + location.getWorld().getName();
            if (contains("special.locations." + locationStr)) {
                runCommands("special.locations." + locationStr, receiver);
                return getMessage("special.locations." + locationStr, receiver, skull.getOwner());
            }

            try {
                // Get the skull's game profile
                Field profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                GameProfile profile = (GameProfile) profileField.get(skull);
                // Check to see if the config contains the head's name.
                if (contains("special.names." + profile.getName())) {
                    runCommands("special.names." + profile.getName(), receiver);
                    return getMessage("special.names." + profile.getName(), receiver, profile.getName());
                }

                // Check to see if the texture is noted.
                // We'll get all three forms of this: the b64 texture, the URL and the hash.
                // There are rumours of HD and transparent heads so b64 allows us to retain support for that.
                // EXCITING STUFF
                Property texturesProp = profile.getProperties().get("textures").iterator().next();
                String b64Texture = texturesProp.getValue();
                String url = new String(Base64.getDecoder().decode(b64Texture.getBytes()));
                String hash = url.replaceAll("http(s?)://textures\\.minecraft\\.net/texture/", "");

                for (String str : Arrays.asList(b64Texture, url, hash)) {
                    if (contains("special.textures." + str)) {
                        runCommands("special.textures." + str, receiver);
                        return getMessage("special.textures." + str, receiver, profile.getName());
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            runCommands("default", receiver);
            return getMessage("default", receiver, skull.getOwner());
        }, HeadsPlus.async).thenApplyAsync(msg -> msg, HeadsPlus.sync);
    }

    private String getMessage(String path, Player player, String name) {
        String message = getString(path + ".message", getString("defaults.message"));
        // Default message is null, what the hell
        if (message == null) return "";
        Pattern defaultsPattern = Pattern.compile("\\{(.+)}");
        if (defaultsPattern.matcher(message).matches()) {
            String pointer = defaultsPattern.matcher(message).group(1);
            if (getConfigSection("defaults").getKeys(false).contains(pointer)) {
                message = getString("defaults." + pointer, getString("defaults.message"));
            }
        }

        if (message == null) return "";

        if (getString(path + ".name") != null) {
            message = message.replaceAll("\\{name}", getString(path + ".name"));
        } else {
            message = message.replaceAll("\\{name}", name);
        }

        return HeadsPlusMessagesManager.get().formatMsg(message, player);
    }

    private void runCommands(String path, Player player) {
        List<String> commands = getList(path + ".commands", Lists.newArrayList(getString(path + ".commands")));
        if (commands == null) return;

        for (String command : commands) {
            if (command.startsWith("player:")) {
                player.performCommand(command.replaceFirst("player:", ""));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }
}
