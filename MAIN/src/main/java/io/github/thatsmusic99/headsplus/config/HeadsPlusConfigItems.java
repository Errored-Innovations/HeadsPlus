package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import io.github.thatsmusic99.headsplus.config.headsx.icons.*;
import io.github.thatsmusic99.headsplus.nms.NewNMSManager;

import java.util.List;

public class HeadsPlusConfigItems extends ConfigSettings {

    public HeadsPlusConfigItems() {
        conName = "inventories";
        enable(false);
    }

    @Override
    protected void load(boolean nullp) {
        for (Icon i : Icon.icons) {
            getConfig().addDefault("icons." + i.getIconName() + ".material", i.getDefaultMaterial().name());
            getConfig().addDefault("icons." + i.getIconName() + ".display-name", i.getDefaultDisplayName());
            getConfig().addDefault("icons." + i.getIconName() + ".lore", i.getDefaultLore());
            getConfig().addDefault("icons." + i.getIconName() + ".replacement", i.getReplacementIcon().getIconName());
            if (i instanceof Challenge) {
                getConfig().addDefault("icons." + i.getIconName() + ".complete-material", ((Challenge) i).getCompleteMaterial().name());
            }
            if (!(HeadsPlus.getInstance().getNMS() instanceof NewNMSManager)) {
                if (i instanceof Challenge) {
                    getConfig().addDefault("icons." + i.getIconName() + ".complete-data-value", 13);
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 14);
                } else if (i instanceof Glass) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 8);
                } else if (i instanceof Head || i instanceof HeadSection){
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 3);
                } else if (i instanceof ChallengeSection.Easy) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 13);
                } else if (i instanceof ChallengeSection.EasyMedium) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 5);
                } else if (i instanceof ChallengeSection.Medium) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 4);
                } else if (i instanceof ChallengeSection.MediumHard) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 1);
                } else if (i instanceof ChallengeSection.Hard || i instanceof ChallengeSection.Deadly) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 14);
                } else if (i instanceof ChallengeSection.Painful) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 2);
                } else if (i instanceof ChallengeSection.PainfulDeadly) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 6);
                } else if (i instanceof ChallengeSection.Tedious) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 11);
                } else if (i instanceof ChallengeSection.TediousPainful) {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 10);
                } else {
                    getConfig().addDefault("icons." + i.getIconName() + ".data-value", 0);
                }
            }
        }
        for (HeadInventory inv : HeadInventory.getInventories()) {
            getConfig().addDefault("inventories." + inv.getName() + ".title", inv.getDefaultTitle());
            if (getConfig().get("inventories." + inv.getName() + ".icons") instanceof List) {
                HeadsPlus.getInstance().getLogger().warning("Old format for inventories.yml detected for " + inv.getName() + "! Starting over...");
                getConfig().set("inventories." + inv.getName() + ".icons", inv.getDefaultItems());
            }
            getConfig().addDefault("inventories." + inv.getName() + ".icons", inv.getDefaultItems());
            getConfig().addDefault("inventories." + inv.getName() + ".size", 54);
        }

        getConfig().options().copyDefaults(true);
        save();
    }
}
