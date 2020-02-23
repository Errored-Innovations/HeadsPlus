package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
    private HashMap<Integer, List<String>> autocomplete;

    @Override
    public String getCmdDescription(CommandSender cs) {
        return hpc.getString("descriptions.hp.conjure", cs);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 1) {
            Player p = null;
            String entity = args[1].toUpperCase();
            if (DeathEvents.ableEntities.contains(entity)) {
                int amount = 1;
                if (args.length > 2) {
                    if (args[2].matches("^[0-9]+$")) {
                        amount = Integer.parseInt(args[2]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-input-int", sender));
                    }
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
                        sender.sendMessage(hpc.getString("commands.errors.not-a-player", sender));
                    }
                }
                if (args.length > 4) {
                    if (args[4].matches("^[0-9]+$")) {
                        index = Integer.parseInt(args[4]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-input-int", sender));
                    }
                }
                if (args.length > 5) {
                    type = args[5];
                }
                DeathEvents de = HeadsPlus.getInstance().getDeathEvents();
                try {
                    ItemStack i = de.getStoredHeads().get(entity + ";" + type).get(index).getItemStack();
                    i.setAmount(amount);
                    p.getWorld().dropItem(p.getLocation(), i).setPickupDelay(0);
                    return true;
                } catch (NullPointerException ex) {
                    sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(hpc.getString("commands.errors.invalid-pg-no", sender));
                }
            } else {
                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
            }
        } else {
            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
        }

        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getEntities(), results);
        } else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], IHeadsPlusCommand.getPlayers(), results);
        } else if (args.length == 6) {
            StringUtil.copyPartialMatches(args[5], IHeadsPlusCommand.getEntityConditions(args[1]), results);
        }
        return results;
    }
}
