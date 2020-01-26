package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@CommandInfo(
        commandname = "conjure",
        permission = "headsplus.maincommand.conjure",
        subcommand = "Conjure",
        maincommand = true,
        usage = "/hp conjure <Entity> [Amount] [Player] [Index] [Colour]"
)
public class Conjure implements IHeadsPlusCommand {

    // F
    private ItemStack head = null;
    private Player p = null;
    private HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription(CommandSender cs) {
        return hpc.getString("descriptions.hp.conjure", cs);
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
            if (args.length > 1) {
                HeadsPlusConfigHeads heads = HeadsPlus.getInstance().getHeadsConfig();
                List<String> mHeads = heads.mHeads;
                List<String> uHeads = heads.uHeads;
                if (mHeads.contains(args[1]) || uHeads.contains(args[1])) {
                    int amount = 1;
                    if (args.length > 2) {
                        if (args[2].matches("^[0-9]+$")) {
                            amount = Integer.parseInt(args[2]);
                        } else {
                            return hpc.getString("commands.errors.invalid-input-int", sender);
                        }
                    }
                    int index = 0;
                    String type = "default";
                    if (args.length > 3) {
                        if (Bukkit.getPlayer(args[3]) != null && Bukkit.getPlayer(args[3]).isOnline()) {
                            p = Bukkit.getPlayer(args[3]);
                        }
                    }
                    if (p == null) {
                        if (sender instanceof Player) {
                            p = (Player) sender;
                        } else {
                            return hpc.getString("commands.errors.not-a-player", sender);
                        }
                    }
                    if (args.length > 4) {
                        if (args[4].matches("^[0-9]+$")) {
                            index = Integer.parseInt(args[4]);
                        } else {
                            return hpc.getString("commands.errors.invalid-input-int", sender);
                        }
                    }
                    if (args.length > 5) {
                        type = args[5];
                    }
                    DeathEvents de = HeadsPlus.getInstance().getDeathEvents();
                    try {
                        ItemStack i = DeathEvents.heads.get(de.prettyStringToUglyString(args[1])).get(type).get(index);
                        i.setAmount(amount);
                        this.head = i;

                        return "";
                    } catch (NullPointerException ex) {
                        return hpc.getString("commands.errors.invalid-args", sender);
                    } catch (IndexOutOfBoundsException e) {
                        return hpc.getString("commands.errors.invalid-pg-no", sender);
                    }
                } else {
                    return hpc.getString("commands.errors.invalid-args", sender);
                }
            } else {
                return hpc.getString("commands.errors.invalid-args", sender);
            }

    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        p.getWorld().dropItem(p.getLocation(), head).setPickupDelay(0);
        return false;
    }
}
