package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHeadSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsMenu extends BaseInventory {

    public HeadsMenu(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public HeadsMenu() {}

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Heads: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "FGGGSGGGK" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "<{[BXN]}>";
    }

    @Override
    public String getId() {
        return "headmenu";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        for (String section : ConfigCustomHeads.get().sections.keySet()) {
            ConfigSection configSec = ConfigCustomHeads.get().getConfigSection("sections." + section);
            ConfigSection itemSec = hpi.getConfigSection("icons.headsection");
            ItemStack item;
            try {
                item = ConfigCustomHeads.get().getSkull(configSec.getString("texture"));
            } catch (NullPointerException ex) {
                if (!suppressWarnings) {
                    hp.getLogger().warning("Texture for " + configSec.getString("texture") + " not found. (Error code: 10)");
                }
                continue;
            }
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            try {
                meta.setDisplayName(hpc.formatMsg(itemSec.getString("display-name")
                        .replaceAll("\\{head-name}", configSec.getString("display-name")), player));
                List<String> lore = new ArrayList<>();
                for (String loreStr : itemSec.getStringList("lore")) {
                    lore.add(hpc.formatMsg(loreStr, player)
                            .replaceAll("\\{head-count}", String.valueOf(ConfigCustomHeads.get().sections.get(section).size())));
                }
                meta.setLore(lore);
            } catch (NullPointerException ex) {
                if (!suppressWarnings) {
                    hp.getLogger().warning("A problem was found when setting the display name for icon Heads Section with ID " + section + ". Either the item is null, or there is a config error in the display names! (Error code: 11)");
                }
            }

            item.setItemMeta(meta);
            contents.add(new CustomHeadSection(item, section));
        }
        return contents;
    }
}