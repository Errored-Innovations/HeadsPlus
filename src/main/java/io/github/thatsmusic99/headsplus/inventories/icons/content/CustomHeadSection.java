package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.SectionChangeEvent;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomHeadSection extends Content {
    private HeadsPlusConfigCustomHeads.SectionInfo section;

    public CustomHeadSection(HeadsPlusConfigCustomHeads.SectionInfo info) {
        super(HeadsPlus.getInstance().getHeadsXConfig().getSkull(info.getTexture()));
        this.section = info;
    }

    public CustomHeadSection() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        HashMap<String, String> context = new HashMap<>();
        context.put("section", section.getName());
        InventoryManager manager = InventoryManager.getManager(player);
        SectionChangeEvent changeEvent = new SectionChangeEvent(player, manager.getSection(), section.getName());
        Bukkit.getPluginManager().callEvent(changeEvent);
        if (!changeEvent.isCancelled()) {
            InventoryManager.getManager(player).open(InventoryManager.InventoryType.HEADS_CATEGORY, context);
            return true;
        }
        // The event was cancelled, so don't destroy the GUI
        return false;
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        ItemMeta meta = item.getItemMeta();
        ConfigurationSection itemSec = hpi.getConfigurationSection("icons.headsection");
        if (meta != null) {
            try {
                meta.setDisplayName(hpc.formatMsg(itemSec.getString("display-name")
                        .replaceAll("\\{head-name}", section.getDisplayName()), player));
                List<String> lore = new ArrayList<>();
                for (String loreStr : itemSec.getStringList("lore")) {
                    lore.add(hpc.formatMsg(loreStr, player)
                            .replaceAll("\\{head-count}", String.valueOf(hp.getHeadsXConfig().sections.get(section.getName()).size())));
                }
                meta.setLore(lore);
            } catch (NullPointerException ex) {
                hp.getLogger().warning("A problem was found when setting the display name for icon Heads Section with ID " + section + ". Either the item is null, or there is a config error in the display names! (Error code: 11)");

            }
            item.setItemMeta(meta);
        }
    }

    @Override
    public String getId() {
        return "headsection";
    }

    @Override
    public String getDefaultDisplayName() {
        return "{head-name}";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[]{"{msg_inventory.icon.head.count}"};
    }

}
