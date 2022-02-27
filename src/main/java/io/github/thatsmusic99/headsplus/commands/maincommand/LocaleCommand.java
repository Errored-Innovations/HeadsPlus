package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@CommandInfo(
        commandname = "locale",
        permission = "headsplus.maincommand.locale",
        maincommand = true,
        usage = "/hp locale <Locale|Refresh> [Player]",
        descriptionPath = "descriptions.hp.locale"
)
public class LocaleCommand implements IHeadsPlusCommand {

    private final MessagesManager messages = MessagesManager.get();
    private final Set<String> languages = MessagesManager.getLocales().keySet();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        // TODO - disable subcommands if feature is disabled like with events?
        if (!MainConfig.get().getLocalisation().SMART_LOCALE) {
            messages.sendMessage("commands.errors.disabled", sender);
            return true;
        }
        if (args.length < 2) {
            messages.sendMessage("commands.errors.invalid-args", sender);
            return true;
        }
        if (!sender.hasPermission("headsplus.maincommand.locale.change")) {
            messages.sendMessage("commands.errors.no-perm", sender);
            return true;
        }
        if (args[1].equalsIgnoreCase("refresh")) {
            Player player;
            if (args.length > 2) {
                player = Bukkit.getPlayer(args[1]);
            } else if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                messages.sendMessage("commands.errors.not-a-player", sender);
                return true;
            }
            if (player == null || !player.isOnline()) {
                messages.sendMessage("commands.errors.player-offline", sender);
                return true;
            }
            messages.setPlayerLocale(player, player.getLocale());
        }
        String str = args[1].split("_")[0];
        if (!languages.contains(str)) {
            messages.sendMessage("commands.locale.invalid-lang", sender,
                    "{languages}", Arrays.toString(languages.toArray()));
            return true;
        }
        if (args.length > 2) {
            Player player = Bukkit.getPlayer(args[2]);
            if (!sender.hasPermission("headsplus.maincommand.locale.others")) {
                messages.sendMessage("commands.errors.no-perm", sender);
                return true;
            }
            if (player != null && player.isOnline()) {
                messages.setPlayerLocale(player, str, true);
                messages.sendMessage("commands.locale.changed-locale-other", sender, "{player}", player.getName(),
                        "{language}", str);
                return true;
            } else {
                messages.sendMessage("commands.errors.player-offline", sender);
            }
        } else {
            if (sender instanceof Player) {
                messages.setPlayerLocale((Player) sender, str, true);
                messages.sendMessage("commands.locale.changed-locale", sender);
                return true;
            } else {
                messages.sendMessage("commands.errors.not-a-player", sender);
            }
        }

        return false;
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getLocalisation().SMART_LOCALE;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            Set<String> locales = MessagesManager.getLocales().keySet();
            locales.add("refresh");
            StringUtil.copyPartialMatches(args[1], locales, results);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
