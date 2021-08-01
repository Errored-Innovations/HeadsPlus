package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.RestrictionsManager;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "myhead",
        permission = "headsplus.myhead",
        maincommand = false,
        usage = "/myhead",
        descriptionPath = "descriptions.myhead")
public class MyHead implements CommandExecutor, IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l, @NotNull String[] args) {
        try {
            if (sender.hasPermission("headsplus.myhead")) {
                if (!(sender instanceof Player)) {
                    hpc.sendMessage("commands.errors.not-a-player", sender);
                    return false;
                }
                Player p = (Player) sender;
                if (!RestrictionsManager.canUse(p.getName(), RestrictionsManager.ActionType.HEADS)) {
                    // TODO message
                    return true;
                }
                giveHead(p, sender.getName());
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (myhead)", true, sender);
        }

        return false;
    }

    private void giveHead(Player p, String n) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        PaperUtil.get().setProfile((SkullMeta) skull.getItemMeta(), n).thenAccept(meta -> {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigMobs.get().getPlayerDisplayName(n)));
            skull.setItemMeta(meta);
            p.getInventory().addItem(skull);
        });
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
