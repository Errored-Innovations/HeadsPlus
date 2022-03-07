package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.MaskManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "conjure",
        permission = "headsplus.maincommand.conjure",
        maincommand = true,
        usage = "/hp conjure <Head ID> [Amount] [Player]",
        descriptionPath = "descriptions.hp.conjure"
)
public class Conjure implements IHeadsPlusCommand {

    private final MessagesManager hpc = MessagesManager.get();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        // Check argument number
        if (args.length < 2) {
            hpc.sendMessage("commands.errors.invalid-args", sender);
            return false;
        }
        Player p = null;
        if (!HeadManager.get().contains(args[1])) {
            hpc.sendMessage("commands.errors.invalid-args", sender);
            return false;
        }
        int amount = 1;
        if (args.length > 2) {
            amount = HPUtils.isInt(args[2]);
        }
        if (args.length > 3) {
            p = Bukkit.getPlayer(args[3]);
            if (p != null && !p.isOnline()) p = null;
        }
        if (p == null) {
            if (sender instanceof Player) {
                p = (Player) sender;
            } else {
                hpc.sendMessage("commands.errors.not-a-player", sender);
                return false;
            }
        }
        HeadManager.HeadInfo info = HeadManager.get().getHeadInfo(args[1]);
        int finalAmount = amount;
        Player finalPlayer = p;
        info.buildHead().thenAccept(item -> {
            item.setAmount(finalAmount);
            finalPlayer.getInventory().addItem(item);
        });
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            List<String> choices = new ArrayList<>();
            HeadManager.get().getKeys().forEach(str -> choices.add("HP#" + str));
            MaskManager.get().getMaskKeys().forEach(str -> choices.add("HPM#" + str));
            StringUtil.copyPartialMatches(args[1], choices, results);
        } else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
