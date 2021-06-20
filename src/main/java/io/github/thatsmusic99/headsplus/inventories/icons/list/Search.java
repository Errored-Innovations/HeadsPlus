package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.util.prompts.ChatPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class Search extends Icon {
    public Search(Player player) {
        super(player);
    }

    public Search() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        player.closeInventory();
        HashMap<String, String> context = new HashMap<>();
        InventoryManager manager = InventoryManager.getManager(player);
        ConversationFactory c = new ConversationFactory(hp);
        Conversation conv = c.withFirstPrompt(new ChatPrompt())
                .withLocalEcho(false)
                .withModality(MainConfig.get().getMiscellaneous().SUPPRESS_MESSAGES_DURING_SEARCH)
                .buildConversation(player);
        conv.addConversationAbandonedListener(event1 -> {
            if (event1.gracefulExit()) {
                context.put("search", String.valueOf(event1.getContext().getSessionData("term")));
                manager.open(InventoryManager.InventoryType.HEADS_SEARCH, context);
            }
        });
        conv.begin();
        return true;
    }

    @Override
    public String getId() {
        return "search";
    }

}
