package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "hpc",
        permission = "headsplus.challenges",
        subcommand = "Hpc",
        maincommand = false,
        usage = "/hpc"
)
public class ChallengeCommand implements CommandExecutor, IHeadsPlusCommand {


    @Override
    public boolean onCommand(CommandSender cs, Command c, String l, String[] args) {
        try {
            if (HeadsPlus.getInstance().hasChallengesEnabled()) {
                if (cs instanceof Player) {
                    Player p = (Player) cs;
                    if (cs.hasPermission("headsplus.challenges")) {
                        InventoryManager.getOrCreate(p).showScreen(InventoryManager.Type.CHALLENGES_MENU);
                        return true;
                    } else {
                        cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.no-perm", p));
                    }
                } else {
                    cs.sendMessage("[HeadsPlus] You have to be a player to run this command!");
                }
            } else {
                cs.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.disabled", cs instanceof Player ? (Player) cs : null));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (Challenges/HPC)", true, cs);
        }
        return true;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hpc", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
