package io.github.thatsmusic99.headsplus.config.customheads.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import io.github.thatsmusic99.headsplus.nms.SearchGUI;
import io.github.thatsmusic99.headsplus.util.AnvilSlot;
import io.github.thatsmusic99.headsplus.util.ChatPrompt;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Search extends ItemStack implements Icon {

    @Override
    public String getIconName() {
        return "search";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
        p.closeInventory();
        try {
            if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("anvil-menu-search", false)) {
                SearchGUI s = HeadsPlus.getInstance().getNMS().getSearchGUI(p, event -> {
                    if (event.getSlot().equals(AnvilSlot.OUTPUT)) {
                        event.setWillClose(false);
                        event.setWillDestroy(false);
                        im.showSearch(event.getName().replace(":", ""));
                    }
                });
                ItemStack is = new ItemStack(Material.NAME_TAG);
                ItemMeta ism = is.getItemMeta();
                ism.setDisplayName(":");
                is.setItemMeta(ism);
                s.setSlot(AnvilSlot.INPUT_LEFT, is);
                s.open();
                im.searchAnvilOpen = true;
            } else {
                ConversationFactory c = new ConversationFactory(HeadsPlus.getInstance());
                Conversation conv = c.withFirstPrompt(new ChatPrompt()).withLocalEcho(false).buildConversation(p);
                conv.addConversationAbandonedListener(event -> {
                    if (event.gracefulExit()) {
                        im.showSearch(String.valueOf(event.getContext().getSessionData("term")));
                    }
                });
                conv.begin();
            }

        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (InventoryEvent)", false, null);
        }
    }

    @Override
    public Material getDefaultMaterial() {
        return Material.NAME_TAG;
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultDisplayName() {
        return "&6[&e&lSearch Heads&6]";
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "K";
    }
}
