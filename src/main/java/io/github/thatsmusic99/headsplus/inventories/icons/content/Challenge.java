package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class Challenge extends Content {
    public Challenge(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        io.github.thatsmusic99.headsplus.api.Challenge challenge = NBTManager.getChallenge(event.getCurrentItem());
        try {
            if (challenge != null) {
                if (!challenge.isComplete(player)) {
                    if (challenge.canComplete(player)) {
                        challenge.complete(player, event.getInventory(), event.getSlot());
                    } else {
                        player.sendMessage(hpc.getString("commands.challenges.cant-complete-challenge", player));
                    }
                } else {
                    player.sendMessage(hpc.getString("commands.challenges.already-complete-challenge", player));
                }
            }
            event.setCancelled(true);
        }catch (NullPointerException ignored) {
        } catch (SQLException ex) {
            DebugPrint.createReport(ex, "Completing challenge", false, player);
        }
    }

    @Override
    public String getId() {
        return null;
    }
}
