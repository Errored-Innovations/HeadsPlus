package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Mask extends BukkitRunnable {

    private ItemStack item;
    private SkullMeta meta;
    private Player player;
    private List<PotionEffect> effects;

    public Mask(Player player, String id, ItemStack item) {
        this.player = player;
    }

    @Override
    public void run() {

    }

    public void start() {
        // TODO
        runTaskTimer(HeadsPlus.get(), 20, 20);
    }
}
