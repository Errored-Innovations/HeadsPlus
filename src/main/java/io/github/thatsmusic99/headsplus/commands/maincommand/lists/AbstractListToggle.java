package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractListToggle extends AbstractListCommand {

    public AbstractListToggle(HeadsPlus hp) {
        super(hp);
    }

    public abstract HeadsPlusMainConfig.SelectorList getSelList();

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length == 1) {
            config.getConfig().set(getPath(), getSelList().enabled = !getSelList().enabled);
            config.save();
            hpc.sendMessage("commands." + getFullName() + "." + (getSelList().enabled ? getListType() + "-on" : getListType() + "-off"), sender);
        } else {
            if (args[1].equalsIgnoreCase("on")) {
                if (!getSelList().enabled) {
                    config.getConfig().set(getPath(), getSelList().enabled = true);
                    config.save();
                    hpc.sendMessage("commands." + getFullName() + "." + getListType() + "-on", sender);
                } else {
                    hpc.sendMessage("commands." + getFullName() + "." + getListType() + "-a-on", sender);
                }
            } else if (args[1].equalsIgnoreCase("off")) {
                if (getSelList().enabled) {
                    config.getConfig().set(getPath(), getSelList().enabled = false);
                    config.save();
                    hpc.sendMessage("commands." + getFullName() + "." + getListType() + "-off", sender);
                } else {
                    hpc.sendMessage("commands." + getFullName() + "." + getListType() + "-a-off", sender);
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], Arrays.asList("on", "off"), results);
        }
        return results;
    }
}
