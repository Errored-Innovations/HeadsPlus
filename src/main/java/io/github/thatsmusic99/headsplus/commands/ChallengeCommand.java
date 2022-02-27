package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
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
        maincommand = false,
        usage = "/hpc",
        descriptionPath = "descriptions.hpc")
public class ChallengeCommand implements CommandExecutor, IHeadsPlusCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command c, @NotNull String l, @NotNull String[] args) {
        try {
            MessagesManager hpc = MessagesManager.get();
            if (MainConfig.get().getMainFeatures().CHALLENGES) {
                if (cs instanceof Player) {
                    Player p = (Player) cs;
                    if (cs.hasPermission("headsplus.challenges")) {
                        InventoryManager.getManager(p).open(InventoryManager.InventoryType.CHALLENGES_MENU,
                                new HashMap<>());
                        return true;
                    } else {
                        hpc.sendMessage("commands.errors.no-perm", p);
                    }
                } else {
                    hpc.sendMessage("commands.errors.not-a-player", cs);
                }
            } else {
                hpc.sendMessage("commands.errors.disabled", cs);
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (Challenges/HPC)", true, cs);
        }
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().CHALLENGES;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        return new ArrayList<>();
    }
}
