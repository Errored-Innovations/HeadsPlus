package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface IHeadsPlusCommand extends CommandExecutor, TabCompleter {

    default String getCmdDescription(CommandSender sender) {
        CommandInfo command = getClass().getAnnotation(CommandInfo.class);
        return MessagesManager.get().getString(command.descriptionPath(), sender);
    }

    default String[] advancedUsages() {
        return new String[0];
    }

    boolean shouldEnable();

    static List<String> getPlayers(CommandSender sender) {
        List<String> p = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (player.canSee(pl)) p.add(pl.getName());
            }
        } else {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                p.add(pl.getName());
            }
        }

        return p;
    }

    static List<String> getEntities() {
        return EntityDataManager.ableEntities;
    }

}
