package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface IHeadsPlusCommand {

    String getCmdDescription(CommandSender sender);

    boolean fire(String[] args, CommandSender sender);

    default String[] advancedUsages() {
        return new String[0];
    }

    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args);

    static List<String> getPlayers(CommandSender sender) {
        List<String> p = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (player.canSee(pl)) {
                    p.add(pl.getName());
                }
            }
        } else {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                p.add(pl.getName());
            }
        }

        return p;
    }

    static List<String> getWorlds() {
        List<String> w = new ArrayList<>();
        for (World wo : Bukkit.getWorlds()) {
            w.add(wo.getName());
        }
        return w;
    }

    static List<String> getEntities() {
        return EntityDataManager.ableEntities;
    }

    static List<String> getEntityConditions(String entity) {
        Object section = MainConfig.get().get(entity);
        return section instanceof ConfigurationSection ? new ArrayList<>(((ConfigurationSection) section).getKeys(false)) : new ArrayList<>();
    }
}
