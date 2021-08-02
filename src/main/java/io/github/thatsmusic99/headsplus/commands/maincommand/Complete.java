package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
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
        commandname = "complete", permission = "headsplus.maincommand.complete", usage = "/hp complete <Challenge name> [Player]", descriptionPath = "descriptions.hp.complete", maincommand = true)
public class Complete implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
            player = Bukkit.getPlayer(args[2]);
            if (player == null || !player.isOnline()) {
                hpc.sendMessage("commands.errors.player-offline", sender);
                return false;
            }
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
        c.canComplete(player).thenApply(result -> {
            if (result) c.complete(player); else hpc.sendMessage("commands.challenges.cant-complete-challenge", sender);
            return true;
        });
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().CHALLENGES;
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
