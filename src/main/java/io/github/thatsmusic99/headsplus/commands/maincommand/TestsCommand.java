package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@CommandInfo(commandname = "tests", permission = "headsplus.maincommand.tests", subcommand = "Tests", usage = "/hp tests <Entity type> <Amount>", maincommand = true)
public class TestsCommand implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.tests", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 1) {
            if (EntityDataManager.ableEntities.contains(args[1].toUpperCase())) {
                if (args.length > 2) {
                    int amount = HPUtils.isInt(args[2]);
                    String type = args[1].toLowerCase().replaceAll("_", "");
                    hpc.sendMessage("commands.tests.running-tests", sender);
                    double chance = ConfigMobs.get().getDouble(type + ".chance");
                    Random rand = new Random();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int successes = 0;
                            for (int in = 0; in < amount; in++) {
                                double result = rand.nextDouble() * 100;
                                if (result <= chance) {
                                    successes++;
                                }
                            }
                            hpc.sendMessage("commands.tests.results", sender, "{results}", successes + "/" + amount + " (" + (((double) successes / (double) amount) * 100) + "%)");
                        }
                    }.runTaskAsynchronously(HeadsPlus.get());
                    return true;

                } else {
                    hpc.sendMessage("commands.errors.invalid-args", sender);
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        } else {
            hpc.sendMessage("commands.errors.invalid-args", sender);
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getEntities(), results);
        }
        return results;
    }
}
