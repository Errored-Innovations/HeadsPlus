package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigItems;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHeadSection;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadsMenu extends BaseInventory {

    private HeadsPlusConfigCustomHeads headsConfig = HeadsPlus.getInstance().getHeadsXConfig();

    public HeadsMenu(Player player) {
        super(player);
    }

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
    public String getDefaultId() {
        return "headsmenu";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void gatherContents(HashMap<String, String> context, Player player) {
        // We ignore the player - inventory is not unique.
        HeadsPlusConfigItems itemsConf = HeadsPlus.getInstance().getItems();
        String items = itemsConf.getConfig().getString("inventories.headsmenu.icons");
        int contentsPerPage = HPUtils.matchCount(CachedValues.CONTENT_PATTERN.matcher(items));
        contents = new PagedLists<>(transformContents(new HashMap<>(), player), contentsPerPage);
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        for (String section : headsConfig.sections.keySet()) {
            ConfigurationSection configSec = headsConfig.getConfig().getConfigurationSection("sections." + section);
            ItemStack item = headsConfig.getSkull(configSec.getString("texture"));
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName(hpc.getString(configSec.getString("display-name"), player));
            List<String> lore = new ArrayList<>();
            for (String loreStr : configSec.getStringList("lore")) {
                lore.add(hpc.formatMsg(loreStr, player).replaceAll("\\{head-count}", String.valueOf(headsConfig.sections.get(section).size())));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            item = NBTManager.addSection(item, section);
            contents.add(new CustomHeadSection(item));
        }
        return contents;
    }


    @Override
    public String getContentType() {
        return "head";
    }
}
