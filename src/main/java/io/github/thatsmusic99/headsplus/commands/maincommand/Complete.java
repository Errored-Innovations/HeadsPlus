package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.ChallengeManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "complete",
        permission = "headsplus.maincommand.complete",
        subcommand = "complete",
        usage = "/hp complete <Challenge name> [Player]",
        maincommand = true)
public class Complete implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.complete", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length < 2) {
            hpc.sendMessage("commands.errors.invalid-args", sender);
            return false;
        }
        Challenge c = ChallengeManager.get().getChallengeByName(args[1]);
        if (c == null) {
            hpc.sendMessage("commands.challenges.no-such-challenge", sender);
            return false;
        }
        Player player;
        if (args.length > 2) {
            if (!sender.hasPermission("headsplus.maincommand.complete.others")) {
                hpc.sendMessage("commands.errors.no-perm", sender);
                return false;
            }
            // TODO - not on main thread, dumbass!
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
            if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null) {
                hpc.sendMessage("commands.errors.player-offline", sender);
                return false;
            }
            player = offlinePlayer.getPlayer();

        } else if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            hpc.sendMessage("commands.errors.not-a-player", sender);
            return false;
        }
        if (c.isComplete(player)) {
            hpc.sendMessage("commands.challenges.already-complete-challenge", sender);
            return true;
        }
        if (c.canComplete(player)) {
            c.complete(player);
        } else {
            hpc.sendMessage("commands.challenges.cant-complete-challenge", sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            List<String> challenges = ChallengeManager.get().getChallengeNames();
            StringUtil.copyPartialMatches(args[1], challenges, results);
        } else if (args.length == 3 && sender.hasPermission("headsplus.maincommand.complete.others")) {
            StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
