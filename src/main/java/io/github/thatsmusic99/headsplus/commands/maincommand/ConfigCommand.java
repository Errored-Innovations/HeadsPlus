package io.github.thatsmusic99.headsplus.commands.maincommand;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand implements IHeadsPlusCommand {
    @Override
    public String getCmdDescription(CommandSender sender) {
        return null;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        switch (args.length) {
            case 2:
                StringUtil.copyPartialMatches(args[1], Lists.newArrayList("crafting",
                        "heads", "interactions", "inventories", "levels", "main", "masks",
                        "mobs", "sounds"), results);
                break;
        }
        return results;
    }
}
