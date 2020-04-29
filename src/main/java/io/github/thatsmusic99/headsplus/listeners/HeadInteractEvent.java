package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public final class HeadInteractEvent implements Listener {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
    private int TimesSent = 0;

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (HeadsPlus.getInstance().getConfiguration().getPerks().click_in) {
                    Player player = event.getPlayer();
                    BlockState block = event.getClickedBlock().getState();
                    if (block instanceof Skull) {

                        Skull skull = (Skull) block;
                        String owner;

                        owner = getSkullName(skull);
                        String playerName = player.getName();
                        HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
                        FileConfiguration fc = hpch.getConfig();
                        List<String> names = new ArrayList<>();
                        names.addAll(hpch.eHeads);
                        names.addAll(hpch.ieHeads);
                        if (TimesSent < 1) {
                            for (String n : names) {
                                if (fc.getStringList(n + ".name").contains(owner)) {
                                    String iMessage1;
                                    String dn = hpch.getInteractName(n).toLowerCase();
                                    if (dn.startsWith("a") || dn.startsWith("e") || dn.startsWith("i") || dn.startsWith("o") || dn.startsWith("u")) {
                                        iMessage1 = hpc.getString("event.head-mhf-interact-message-2", player);
                                    } else {
                                        iMessage1 = hpc.getString("event.head-mhf-interact-message", player);
                                    }
                                    iMessage1 = iMessage1.replaceAll("\\{name}", dn).replaceAll("\\{player}", playerName);
                                    player.sendMessage(iMessage1);
                                    TimesSent++;
                                    return;
                                }
                            }
                            String iMessage1 = hpc.getString("event.head-interact-message", player);
                            iMessage1 = iMessage1.replaceAll("\\{name}", owner);
                            iMessage1 = iMessage1.replaceAll("\\{player}", playerName);
                            player.sendMessage(iMessage1);
                            TimesSent++;
                        } else {
                            TimesSent --;
                        }
                    }
                }

            }
        } catch (NullPointerException ex) {
            //
        } catch (Exception e) {
            DebugPrint.createReport(e, "Event (HeadInteractEvent)", false, null);
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