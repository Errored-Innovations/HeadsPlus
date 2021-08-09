package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigInteractions;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class HeadInteractListener extends HeadsPlusListener<PlayerInteractEvent> {

    private final List<UUID> sent = new ArrayList<>();

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerInteractEvent.class, "HPHeadInteractEvent", this), HeadsPlus.get(), true);

        int length = Action.values().length;
        String[] actions = new String[length];
        for (int i = 0; i < length; i++) {
            actions[i] = Action.values()[i].name();
        }
        addPossibleData("action", actions);
        addPossibleData("is-skull", "true", "false");
        addPossibleData("owner", "<Name>");
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().INTERACTIONS;
    }

    @Override
    public void onEvent(PlayerInteractEvent event) {
        if (addData("action", event.getAction()) == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            BlockState block = event.getClickedBlock().getState();
            if (addData("is-skull", block instanceof Skull)) {
                Skull skull = (Skull) block;
                if (!sent.contains(player.getUniqueId())) {
                    sent.add(player.getUniqueId());
                    player.sendMessage(ConfigInteractions.get().getMessageForHead(skull, player));
                } else {
                    sent.remove(player.getUniqueId());
                }
            }
        }
    }
}
