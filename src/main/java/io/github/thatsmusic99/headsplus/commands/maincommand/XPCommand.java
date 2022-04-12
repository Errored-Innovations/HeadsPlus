package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        commandname = "xp",
        permission = "headsplus.maincommand.xp",
        usage = "/hp xp <Player Name> [View|Add|Subtract|Reset] [Amount]",
        maincommand = true,
        descriptionPath = "descriptions.hp.xp"
)
public class XPCommand implements IHeadsPlusCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length < 3) {
            MessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
            return false;
        }

        Player target = Bukkit.getPlayer(args[1]);

        switch (args[2].toLowerCase()) {
            case "add":
                if (!sender.hasPermission("headsplus.maincommand.xp.add")) {
                    MessagesManager.get().sendMessage("commands.errors.no-perm", sender);
                    return true;
                }
                if (args.length < 4) {
                    MessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
                    return false;
                }
                int amount = HPUtils.isInt(args[3]);
                if (target != null) {
                    HPPlayer player = HPPlayer.getHPPlayer(target.getUniqueId());
                    player.setXp(player.getXp() + amount);
                    MessagesManager.get().sendMessage("commands.xp.added-xp", sender, "{player}", args[1], "{xp}",
                            String.valueOf(player.getXp()), "{amount}", args[3]);
                    return true;
                }
                PlayerSQLManager.get().addXP(args[1], amount).thenAcceptAsync(rood ->
                        MessagesManager.get().sendMessage("commands.xp.added-xp", sender, "{player}", args[1], "{xp}",
                                String.valueOf(PlayerSQLManager.get().getXP(args[1], false)), "{amount}", args[3]),
                        HeadsPlus.async);
                return true;
            case "subtract":
                if (!sender.hasPermission("headsplus.maincommand.xp.subtract")) {
                    MessagesManager.get().sendMessage("commands.errors.no-perm", sender);
                    return true;
                }
                if (args.length < 4) {
                    MessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
                    return false;
                }
                amount = HPUtils.isInt(args[3]);
                if (target != null) {
                    HPPlayer player = HPPlayer.getHPPlayer(target.getUniqueId());
                    if (amount > player.getXp() && !MainConfig.get().getMiscellaneous().ALLOW_NEGATIVE_XP) {
                        MessagesManager.get().sendMessage("commands.xp.negative-xp", sender);
                        return false;
                    }
                    player.setXp(player.getXp() - amount);
                    MessagesManager.get().sendMessage("commands.xp.remove-xp", sender, "{player}", args[1], "{xp}",
                            String.valueOf(player.getXp() - amount), "{amount}", args[3]);
                    return true;
                }
                PlayerSQLManager.get().getXP(args[1], true).thenAccept(xp -> {
                    if (amount > xp && !MainConfig.get().getMiscellaneous().ALLOW_NEGATIVE_XP) {
                        MessagesManager.get().sendMessage("commands.xp.negative-xp", sender);
                        return;
                    }
                    PlayerSQLManager.get().setXP(args[1], xp - amount).thenAccept(ree -> MessagesManager.get().sendMessage("commands.xp.remove-xp", sender, "{player}", args[1], "{xp}", String.valueOf(xp - amount), "{amount}", args[3]));
                });
                break;
            case "reset":
                if (sender.hasPermission("headsplus.maincommand.reset")) {
                    if (target != null) {
                        HPPlayer player = HPPlayer.getHPPlayer(target.getUniqueId());
                        player.setXp(0);
                        MessagesManager.get().sendMessage("commands.xp.reset-xp", sender, "{player}", args[1]);
                        return true;
                    }
                    PlayerSQLManager.get().setXP(args[1], 0);
                    PlayerSQLManager.get().setLevel(args[1], LevelsManager.get().getLevel(0).getConfigName());
                    MessagesManager.get().sendMessage("commands.xp.reset-xp", sender, "{player}", args[1]);
                    return true;
                } else {
                    MessagesManager.get().sendMessage("commands.errors.no-perm", sender);
                }
                break;
            case "view":
                if (sender.hasPermission("headsplus.maincommand.xp.view")) {
                    PlayerSQLManager.get().getXP(args[1], true).thenAccept(xp -> MessagesManager.get().sendMessage(
                            "commands.xp.current-xp", sender, "{player}", args[1], "{xp}",
                            String.valueOf(xp)));

                }
                break;
            default:
                MessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
                break;
        }
        return false;
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
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], Arrays.asList("view", "add", "subtract", "reset"), results);
        }
        return results;
    }
}
