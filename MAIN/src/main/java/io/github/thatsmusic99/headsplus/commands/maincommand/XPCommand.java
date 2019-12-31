package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
@CommandInfo(commandname = "xp", permission = "headsplus.maincommand.xp", subcommand = "XP", usage = "/hp xp <Player Name> [View|Add|Subtract|Reset] [Amount]", maincommand = true)
public class XPCommand implements IHeadsPlusCommand {

    private HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            HPPlayer player = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[1]));
            if (player != null) {
                if (args.length > 2) {
                    switch (args[2].toLowerCase()) {
                        case "view":
                        case "reset":
                            h.put(true, "");
                            break;
                        case "add":
                        case "subtract":
                            if (args.length > 3) {
                                if (args[3].matches("^[0-9]+$")) {
                                    h.put(true, "");
                                } else {
                                    h.put(false, hpc.getString("commands.errors.invalid-args"));
                                }
                            } else {
                                h.put(false, hpc.getString("commands.errors.invalid-args"));
                            }
                            break;
                        default:
                            h.put(false, hpc.getString("commands.errors.invalid-args"));
                            break;
                    }
                } else {
                    h.put(true, "");
                }
            } else {
                h.put(false, hpc.getString("commands.errors.no-data"));
            }
        } else {
            h.put(false, hpc.getString("commands.errors.invalid-args"));
        }
        return h;
    }

    @Override
    public String getCmdDescription() {
        return hpc.getString("descriptions.hp.xp");
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        HPPlayer player = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[1]));
        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("add")) {
                int amount = Integer.valueOf(args[3]);
                player.addXp(amount);
                sender.sendMessage(hpc.getString("commands.xp.added-xp").replaceAll("\\{player}", args[1])
                        .replaceAll("\\{xp}", String.valueOf(player.getXp()))
                        .replaceAll("\\{amount}", String.valueOf(args[3])));
                return true;
            } else if (args[2].equalsIgnoreCase("subtract")) {
                int amount = Integer.valueOf(args[3]);
                if (amount > player.getXp() && !HeadsPlus.getInstance().getConfiguration().getPerks().negative_xp) {
                    sender.sendMessage(hpc.getString("commands.xp.negative-xp"));
                    return true;
                }
                player.removeXp(amount);
                sender.sendMessage(hpc.getString("commands.xp.removed-xp").replaceAll("\\{player}", args[1])
                        .replaceAll("\\{xp}", String.valueOf(player.getXp()))
                        .replaceAll("\\{amount}", String.valueOf(args[3])));
            } else if (args[2].equalsIgnoreCase("reset")) {
                player.setXp(0);
                sender.sendMessage(hpc.getString("commands.xp.reset-xp").replaceAll("\\{player}", args[1]));
            } else {
                sender.sendMessage(hpc.getString("commands.xp.current-xp").replaceAll("\\{player}", args[1])
                        .replaceAll("\\{xp}", String.valueOf(player.getXp())));
            }
        } else {
            sender.sendMessage(hpc.getString("commands.xp.current-xp").replaceAll("\\{player}", args[1])
                    .replaceAll("\\{xp}", String.valueOf(player.getXp())));
        }
        return false;
    }
}
