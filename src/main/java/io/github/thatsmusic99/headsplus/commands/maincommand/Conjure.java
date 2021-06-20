package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "conjure",
        permission = "headsplus.maincommand.conjure",
        subcommand = "Conjure",
        maincommand = true,
        usage = "/hp conjure <Entity> [Amount] [Player] [Index] [Colour]"
)
public class Conjure implements IHeadsPlusCommand {

    // F
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public String getCmdDescription(CommandSender cs) {
        return hpc.getString("descriptions.hp.conjure", cs);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 1) {
            Player p = null;
            String entity = args[1].toUpperCase();
            if (EntityDataManager.ableEntities.contains(entity)) {
                int amount = 1;
                if (args.length > 2) {
                    amount = HPUtils.isInt(args[2]);
                }
                int index = 0;
                String type = "default";
                if (args.length > 3) {
                    if (Bukkit.getPlayer(args[3]) != null && Bukkit.getPlayer(args[3]).isOnline()) {
                        p = Bukkit.getPlayer(args[3]);
                    }
                }
                if (p == null) {
                    if (sender instanceof Player) {
                        p = (Player) sender;
                    } else {
                        hpc.sendMessage("commands.errors.not-a-player", sender);
                        return false;
                    }
                }
                if (args.length > 4) {
                    index = HPUtils.isInt(args[4]);
                }
                if (args.length > 5) {
                    type = args[5];
                }
                try {
                    HeadManager.HeadInfo info = EntityDataManager.getStoredHeads().get(entity + ";" + type).get(index);
                    int finalAmount = amount;
                    Player finalPlayer = p;
                    info.buildHead().thenAccept(item -> {
                        item.setAmount(finalAmount);
                        finalPlayer.getInventory().addItem(item);
                    });
                    return true;
                } catch (NullPointerException ex) {
                    hpc.sendMessage("commands.errors.invalid-args", sender);
                } catch (IndexOutOfBoundsException e) {
                    hpc.sendMessage("commands.errors.invalid-pg-no", sender);
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        } else {
            hpc.sendMessage("commands.errors.invalid-args", sender);
        }

        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getEntities(), results);
        } else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], IHeadsPlusCommand.getPlayers(sender), results);
        } else if (args.length == 6) {
            StringUtil.copyPartialMatches(args[5], IHeadsPlusCommand.getEntityConditions(args[1]), results);
        }
        return results;
    }
}
