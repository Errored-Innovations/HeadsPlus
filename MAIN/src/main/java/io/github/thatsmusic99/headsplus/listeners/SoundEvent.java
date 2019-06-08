package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.*;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigSounds;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoundEvent implements Listener {

    private HeadsPlusConfigSounds sounds = HeadsPlus.getInstance().getSounds();

    @EventHandler
    public void onHeadSell(SellHeadEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-sell-head.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-sell-head");
        }
    }

    @EventHandler
    public void onHeadBuy(HeadPurchaseEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-buy-head.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-buy-head");
        }
    }

    @EventHandler
    public void onSectionChange(SectionChangeEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-change-section.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-change-section");
        }
    }

    @EventHandler
    public void onEntityHeadDrop(EntityHeadDropEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-entity-head-drop.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-entity-head-drop");
        }
    }

    @EventHandler
    public void onPlayerHeadDrop(PlayerHeadDropEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-player-head-drop.enabled")) {
            playSound(event.getDeadPlayer().getLocation(), "on-player-head-drop");
        }
    }

    @EventHandler
    public void onLevelUp(LevelUpEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-level-up.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-level-up");
        }
    }

    @EventHandler
    public void onHeadCraft(HeadCraftEvent event) {
        if (sounds.getConfig().getBoolean("sounds.on-craft-head.enabled")) {
            playSound(event.getPlayer().getLocation(), "on-craft-head");
        }
    }

    private void playSound(Location l, String st) {
        try {
            Sound s = Sound.valueOf(sounds.getConfig().getString("sounds." + st + ".sound"));
            float vol = (float) sounds.getConfig().getDouble("sounds." + st + ".volume");
            float pitch = (float) sounds.getConfig().getDouble("sounds." + st + ".pitch");
            l.getWorld().playSound(l, s, vol, pitch);
        } catch (IllegalArgumentException ex) {
            HeadsPlus.getInstance().getLogger().warning("Could not find sound " + sounds.getConfig().getString("sounds." + st + ".sound") + "!");
        }

    }
}
