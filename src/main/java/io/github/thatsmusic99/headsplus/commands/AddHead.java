package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;

import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.github.thatsmusic99.headsplus.util.prompts.DataListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "addhead",
        permission = "headsplus.addhead",
        subcommand = "addhead",
        maincommand = false,
        usage = "/addhead [player]"
)
public class AddHead implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length > 0) {
            if (args[0].matches("^[A-Za-z0-9_]+$")) {
                if (args[0].length() > 2) {
                    if (args[0].length() < 17) {
                        HeadsPlus hp = HeadsPlus.get();
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        String uuid = p.getUniqueId().toString();
                        if (!hp.getServer().getOnlineMode()) {
                            hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                            uuid = AutograbManager.grabUUID(p.getName(), 3, null);
                        }
                        if (AutograbManager.grabProfile(uuid, sender, true)) {
                            hpc.sendMessage("commands.addhead.head-adding", sender, "{player}", p.getName());
                        }
                        return true;
                    } else {
                        hpc.sendMessage("commands.head.head-too-long", sender);
                    }
                } else {
                    hpc.sendMessage("commands.head.head-too-short", sender);
                }
            } else {
                hpc.sendMessage("commands.head.alpha-names", sender);
            }
        } else {
            if (sender.hasPermission("headsplus.addhead.texture")) {
                if (sender instanceof Conversable) {
                    ConversationFactory factory = new ConversationFactory(HeadsPlus.get());
                    Conversation conversation = factory.withLocalEcho(false)
                            .withModality(MainConfig.get().getMiscellaneous().SUPPRESS_MESSAGES_DURING_SEARCH)
                            .withFirstPrompt(new DataListener(0, hpc.getString("commands.addhead.id", sender)))
                            .buildConversation((Conversable) sender);
                    conversation.addConversationAbandonedListener(event -> {
                        if (event.gracefulExit()) {
                            ConversationContext context = event.getContext();
                            if (context.getSessionData("cancel") != null) {
                                return;
                            }
                            String id = String.valueOf(context.getSessionData("id"));
                            ConfigCustomHeads customHeads = ConfigCustomHeads.get();
                            for (Object key : context.getAllSessionData().keySet()) {
                                if (key.equals("id")) continue;
                                customHeads.addDefault("heads." + id + "." + key, context.getSessionData(key));
                            }
                            try {
                                customHeads.save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            customHeads.addHeadToCache(id, String.valueOf(context.getSessionData("section")));
                            hpc.sendMessage("commands.addhead.custom-head-added", sender, "{id}", id);
                        }
                    });
                    conversation.begin();
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        }
        return true;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.addhead", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }

}
