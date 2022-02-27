package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigHeads;
import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.prompts.DataListener;
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
        maincommand = false,
        usage = "/addhead [player]",
        descriptionPath = "descriptions.addhead")
public class AddHead implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final MessagesManager hpc = MessagesManager.get();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length > 0) {
            if (!args[0].matches("^[A-Za-z0-9_]+$")) {
                hpc.sendMessage("commands.head.alpha-names", sender);
                return true;
            }
            if (args[0].length() < 3) {
                hpc.sendMessage("commands.head.head-too-short", sender);
                return true;
            }
            if (args[0].length() > 16) {
                hpc.sendMessage("commands.head.head-too-long", sender);
                return true;
            }
            HeadsPlus hp = HeadsPlus.get();
            HPUtils.getOfflinePlayer(args[0]).thenAccept(player -> {
                String uuid = player.getUniqueId().toString();
                if (!hp.getServer().getOnlineMode()) {
                    hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting" +
                            " to grab UUID...");
                    uuid = AutograbManager.grabUUID(player.getName(), 3, null);
                }
                if (AutograbManager.grabProfile(uuid, sender, true)) {
                    hpc.sendMessage("commands.addhead.head-adding", sender, "{player}", player.getName());
                }
            });
            return true;
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

                            ConfigHeads selector = ConfigHeads.get();
                            selector.forceExample("heads." + id + ".texture", context.getSessionData("texture"));
                            selector.forceExample("heads." + id + ".display-name", context.getSessionData("displayname"));

                            ConfigHeadsSelector otherSelector = ConfigHeadsSelector.get();
                            otherSelector.set("heads.HP#" + id + ".section", context.getSessionData("section"));
                            otherSelector.set("heads.HP#" + id + ".price", context.getSessionData("price"));

                            try {
                                selector.save();
                                otherSelector.save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            HeadManager.HeadInfo headInfo = new HeadManager.HeadInfo();
                            HeadManager.get().registerHead(id, headInfo
                                    .withDisplayName((String) context.getSessionData("displayname"))
                                    .withTexture((String) context.getSessionData("texture")));

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
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }

}
