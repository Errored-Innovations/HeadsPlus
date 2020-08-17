package io.github.thatsmusic99.headsplus.config;

import java.util.ArrayList;
import java.util.Arrays;

public class HeadsPlusConfigInteractions extends ConfigSettings {

    public HeadsPlusConfigInteractions() {
        conName = "interactions";
        enable();
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
    public void load() {
        getConfig().options().header("HeadsPlus by Thatsmusic99");
        double version = getConfig().getDouble("version");
        if (version < 0.1) {
            // Default values
            getConfig().set("version", 0.1);
            getConfig().addDefault("defaults.message", "{msg_event.head-interact-message}");
            getConfig().addDefault("defaults.consonant-message", "{msg_event.head-mhf-interact-message}");
            getConfig().addDefault("defaults.vowel-message", "{msg_event.head-mhf-interact-message-2}");
            // Pre-made names
            for (String str : Arrays.asList("MHF_CoconutB", "MHF_CoconutG", "MHF_Present1", "MHF_Present2", "MHF_TNT", "MHF_Cactus",
                    "MHF_Chest", "MHF_Melon", "MHF_TNT2")) {
                getConfig().addDefault("special.names." + str + ".message", "{consonant-message}");
                getConfig().addDefault("special.names." + str + ".commands", new ArrayList<>());
            }

            for (String str : Arrays.asList("MHF_OakLog", "MHF_ArrowUp", "MHF_ArrowDown", "MHF_ArrowRight", "MHF_ArrowLeft")) {
                getConfig().addDefault("special.names." + str + ".message", "{vowel-message}");
                getConfig().addDefault("special.names." + str + ".commands", new ArrayList<>());
            }

            getConfig().addDefault("special.names.MHF_CoconutB.name", "Brown Coconut");
            getConfig().addDefault("special.names.MHF_CoconutG.name", "Green Coconut");
            getConfig().addDefault("special.names.MHF_OakLog.name", "Oak Log");
            getConfig().addDefault("special.names.MHF_Present1.name", "Present");
            getConfig().addDefault("special.names.MHF_Present2.name", "Present");
            getConfig().addDefault("special.names.MHF_TNT.name", "TNT");
            getConfig().addDefault("special.names.MHF_TNT2.name", "TNT");
            getConfig().addDefault("special.names.MHF_ArrowUp.name", "Arrow Pointing Up");
            getConfig().addDefault("special.names.MHF_ArrowDown.name", "Arrow Pointing Down");
            getConfig().addDefault("special.names.MHF_ArrowRight.name", "Arrow Pointing Right");
            getConfig().addDefault("special.names.MHF_ArrowLeft.name", "Arrow Pointing Left");
            getConfig().addDefault("special.names.MHF_Cactus.name", "Cactus");
            getConfig().addDefault("special.names.MHF_Chest.name", "Chest");
            getConfig().addDefault("special.names.MHF_Melon.name", "Melon");

            // hahahaha
            getConfig().addDefault("special.names.Thatsmusic99.message", "{header} oh god it's error, run");
            getConfig().addDefault("special.names.Thatsmusic99.name", "");
            getConfig().addDefault("special.names.Thatsmusic99.commands", new ArrayList<>());

            getConfig().addDefault("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.name", "???");
            getConfig().addDefault("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.message", "{header} what???");
            getConfig().addDefault("special.textures.7f3ca4f7c92dde3a77ec510a74ba8c2e8d0ec7b80f0e348cc6dddd6b458bd.commands", new ArrayList<>());
        }
        getConfig().options().copyDefaults(true);
        save();
    }
}
