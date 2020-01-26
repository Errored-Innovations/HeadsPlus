package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@CommandInfo(
        commandname = "locale",
        permission = "headsplus.maincommand.locale",
        subcommand = "Locale",
        maincommand = true,
        usage = "/hp locale <Locale|Refresh> [Player]"
)
public class LocaleCommand implements IHeadsPlusCommand {

    private HeadsPlusMessagesManager messages = HeadsPlus.getInstance().getMessagesConfig();
    private final Set<String> languages = HeadsPlusMessagesManager.getLocales().keySet();

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        if (HeadsPlus.getInstance().getConfiguration().getConfig().getBoolean("smart-locale")) {
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("refresh")) {
                    if (sender instanceof Player) {
                        return "";
                    } else {
                        return messages.getString("commands.errors.not-a-player", sender);
                    }
                } else {
                    if (sender.hasPermission("headsplus.maincommand.locale.change")) {
                        String str = args[1].split("_")[0];
                        if (languages.contains(str)) {
                            if (args.length > 2) {
                                Player player = Bukkit.getPlayer(args[2]);
                                if (sender.hasPermission("headsplus.maincommand.locale.others")) {
                                    if (player != null && player.isOnline()) {
                                        return "";
                                    } else {
                                        return messages.getString("commands.errors.player-offline", sender);
                                    }
                                } else {
                                    return messages.getString("commands.errors.no-perm", sender);
                                }

                            } else {
                                if (sender instanceof Player) {
                                    return "";
                                } else {
                                    return messages.getString("commands.errors.not-a-player", sender);
                                }
                            }
                        } else {
                            return messages.getString("commands.locale.invalid-lang", sender).replaceAll("\\{languages}", Arrays.toString(languages.toArray()));
                        }
                    } else {
                        return messages.getString("commands.errors.no-perm", sender);
                    }
                }
            } else {
                return messages.getString("commands.errors.invalid-args", sender);
            }
        } else {
            return messages.getString("commands.errors.disabled", sender);
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return messages.getString("descriptions.hp.locale", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args[1].equalsIgnoreCase("refresh")) {
            messages.setPlayerLocale((Player) sender);
            sender.sendMessage(messages.getString("commands.locale.changed-locale", sender));
            return true;
        } else {
            String str = args[1].split("_")[0];
            if (args.length > 2) {
                Player player = Bukkit.getPlayer(args[2]);
                messages.setPlayerLocale(player, str);
                sender.sendMessage(messages.getString("commands.locale.changed-locale-other", sender)
                        .replaceAll("\\{player}", player.getName()).replaceAll("\\{language}", str));
            } else {
                messages.setPlayerLocale((Player) sender, str);
                sender.sendMessage(messages.getString("commands.locale.changed-locale", sender));
            }
        }
        return false;
    }
}
