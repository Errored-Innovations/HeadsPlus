package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HPHeadInteractEvent extends HeadsPlusListener<PlayerInteractEvent> {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
    private final List<UUID> sent = new ArrayList<>();

    public HPHeadInteractEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerInteractEvent.class, "HPHeadInteractEvent", this), HeadsPlus.getInstance());
    }

    // TODO - rewrite for interactions overhaul
    @Override
    public void onEvent(PlayerInteractEvent event) {
        try {
            if (addData("action", event.getAction()) == Action.RIGHT_CLICK_BLOCK) {
                if (HeadsPlus.getInstance().getConfiguration().getPerks().click_in) {
                    Player player = event.getPlayer();
                    BlockState block = event.getClickedBlock().getState();
                    if (addData("is-skull", block instanceof Skull)) {

                        Skull skull = (Skull) block;
                        String owner;

                        owner = addData("owner", getSkullName(skull));
                        if (owner == null) return;
                        String playerName = player.getName();
                        HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
                        FileConfiguration fc = hpch.getConfig();
                        List<String> names = new ArrayList<>();
                        names.addAll(hpch.eHeads);
                        names.addAll(hpch.ieHeads);
                        if (!sent.contains(player.getUniqueId())) {
                            sent.add(player.getUniqueId());
                            for (String n : names) {
                                if (fc.getStringList(n + ".name").contains(owner)) {
                                    String dn = hpch.getInteractName(n).toLowerCase();
                                    if (dn.startsWith("a") || dn.startsWith("e") || dn.startsWith("i") || dn.startsWith("o") || dn.startsWith("u")) {
                                        hpc.sendMessage("event.head-mhf-interact-message-2", player, "{name}", dn, "{player}", playerName);
                                    } else {
                                        hpc.sendMessage("event.head-mhf-interact-message", player, "{name}", dn, "{player}", playerName);
                                    }
                                    return;
                                }
                            }
                            hpc.sendMessage("event.head-interact-message", player, "{name}", owner, "{player}", playerName);
                        } else {
                            sent.remove(player.getUniqueId());
                        }
                    }
                }

            }
        } catch (NullPointerException ex) {
            //
        }
    }

    @SuppressWarnings("deprecation")
    private static String getSkullName(Skull s) {
        if (HeadsPlus.getInstance().getServer().getVersion().contains("1.8")) {
            return s.getOwner();
        } else {
            return s.getOwningPlayer().getName();
        }
    }
}
