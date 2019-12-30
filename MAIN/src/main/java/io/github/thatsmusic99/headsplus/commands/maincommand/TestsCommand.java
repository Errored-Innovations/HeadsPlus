package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@CommandInfo(commandname = "tests", permission = "headsplus.maincommand.tests", subcommand = "Tests", usage = "/hp tests <Entity type> <Amount>", maincommand = true)
public class TestsCommand implements IHeadsPlusCommand {

    private HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            HeadsPlusConfigHeads heads = HeadsPlus.getInstance().getHeadsConfig();
            List<String> mHeads = heads.mHeads;
            List<String> uHeads = heads.uHeads;
            if (mHeads.contains(args[1]) || uHeads.contains(args[1])) {
                if (args.length > 2) {
                    if (args[2].matches("^[0-9]+$")) {
                        h.put(true, "");
                    } else {
                        h.put(false, hpc.getString("commands.errors.invalid-input-int"));
                    }
                } else {
                    h.put(false, hpc.getString("commands.errors.invalid-args"));
                }
            } else {
                h.put(false, hpc.getString("commands.errors.invalid-args"));
            }
        } else {
            h.put(false, hpc.getString("commands.errors.invalid-args"));
        }
        return h;
    }

    @Override
    public String getCmdDescription() {
        return "Run tests for mob drop rates.";
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        int amount = Integer.parseInt(args[2]);
        sender.sendMessage(hpc.getString("commands.tests.running-tests"));
        double chance = HeadsPlus.getInstance().getHeadsConfig().getConfig().getDouble(args[1] + ".chance");
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
                sender.sendMessage(hpc.getString("commands.tests.results").replaceAll("\\{results}", successes + "/" + amount + " (" + (((double) successes / (double) amount) * 100) + "%)"));
            }
        }.runTaskAsynchronously(HeadsPlus.getInstance());
        return false;
    }
}
