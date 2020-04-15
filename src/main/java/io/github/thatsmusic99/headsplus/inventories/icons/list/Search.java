package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.nms.SearchGUI;
import io.github.thatsmusic99.headsplus.util.AnvilSlot;
import io.github.thatsmusic99.headsplus.util.prompts.ChatPrompt;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Search extends Icon {
    public Search(Player player) {
        super(player);
    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        player.closeInventory();
        HashMap<String, String> context = new HashMap<>();
        InventoryManager manager = InventoryManager.getManager(player);
        if (hp.getConfiguration().getMechanics().getBoolean("anvil-menu-search", false)) {
            SearchGUI s = null;
            try {
                s = hp.getNMS().getSearchGUI(player, event1 -> {
                    if (event1.getSlot().equals(AnvilSlot.OUTPUT)) {
                        event1.setWillClose(false);
                        event1.setWillDestroy(false);
                        context.put("search", event1.getName().replace(":", ""));
                        manager.open(InventoryManager.InventoryType.HEADS_SEARCH, context);
                    }
                });
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            ItemStack is = new ItemStack(Material.NAME_TAG);
            s.setSlot(AnvilSlot.INPUT_LEFT, is);
            s.open();
        } else {
            ConversationFactory c = new ConversationFactory(hp);
            Conversation conv = c.withFirstPrompt(new ChatPrompt()).withLocalEcho(false).buildConversation(player);
            conv.addConversationAbandonedListener(event1 -> {
                if (event1.gracefulExit()) {
                    context.put("search", String.valueOf(event1.getContext().getSessionData("term")));
                    manager.open(InventoryManager.InventoryType.HEADS_SEARCH, context);
                }
            });
            conv.begin();
        }
        return true;
    }

    @Override
    public String getId() {
        return "search";
    }
}
