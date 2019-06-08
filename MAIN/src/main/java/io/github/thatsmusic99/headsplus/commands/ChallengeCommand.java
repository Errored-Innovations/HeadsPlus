package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@CommandInfo(
        commandname = "hpc",
        permission = "headsplus.challenges",
        subcommand = "Hpc",
        maincommand = false,
        usage = "/hpc"
)
public class ChallengeCommand implements CommandExecutor, IHeadsPlusCommand {

    private final HashMap<String, Boolean> tests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
        tests.clear();
        try {
            if (HeadsPlus.getInstance().hasChallengesEnabled()) {
                tests.put("Challenges enabled", true);
                if (cs instanceof Player) {
                    tests.put("Instance of Player", true);
                    Player p = (Player) cs;
                    if (cs.hasPermission("headsplus.challenges")) {
                        tests.put("Has permission", true);
                        InventoryManager.getOrCreate(p).showScreen(InventoryManager.Type.CHALLENGES_MENU);
                        printDebugResults(tests, true);
                        return true;
                    } else {
                        tests.put("Has permission", false);
                        cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("no-perms"));
                    }
                } else {
                    tests.put("Instance of Player", false);
                    cs.sendMessage("[HeadsPlus] You have to be a player to run this command!");
                }
            } else {
                tests.put("Challenges enabled", false);
                cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("disabled"));
            }
        } catch (Exception e) {
            new DebugPrint(e, "Command (Challenges/HPC)", true, cs);
        }
        printDebugResults(tests, false);
        return true;
    }

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descChallenges();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        h.put(true, "");
        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
