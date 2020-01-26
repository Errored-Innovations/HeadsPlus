package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@CommandInfo(
        commandname = "heads",
        permission = "headsplus.heads",
        subcommand = "Heads",
        maincommand = false,
        usage = "/heads"
)
public class Heads implements CommandExecutor, IHeadsPlusCommand {

    private final HashMap<String, Boolean> tests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String l, String[] args) {
        tests.clear();
        try {
            tests.put("Heads enabled", HeadsPlus.getInstance().isUsingHeadDatabase());
            tests.put("Instance of Player", cs instanceof Player);
            tests.put("No permission", cs.hasPermission("headsplus.heads"));
            if (HeadsPlus.getInstance().isUsingHeadDatabase()) {
                if (cs instanceof Player) {
                    Player p = (Player) cs;
                    if (cs.hasPermission("headsplus.heads")) {
                        InventoryManager im2 = InventoryManager.getOrCreate(p);
                        im2.showScreen(InventoryManager.Type.LIST_MENU);
                        printDebugResults(tests, true);
                        return true;
                    } else {
                        cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.no-perm", p));
                    }
                } else {
                    cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.not-a-player", cs));
                }
            } else {
                cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.disabled", cs));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (heads)", true, cs);
        }
        printDebugResults(tests, false);
        return false;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.heads", sender);
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        return "";
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
