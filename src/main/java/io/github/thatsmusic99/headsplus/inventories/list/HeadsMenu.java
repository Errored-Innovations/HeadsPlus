package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.CustomHeadSection;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
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
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        HeadsPlusConfigCustomHeads headsConfig = HeadsPlus.getInstance().getHeadsXConfig();
        for (String section : headsConfig.sections.keySet()) {
            ConfigurationSection configSec = headsConfig.getConfig().getConfigurationSection("sections." + section);
            ItemStack item;
            try {
                item = headsConfig.getSkull(configSec.getString("texture"));
            } catch (NullPointerException ex) {
                hp.getLogger().warning("Texture for " + configSec.getString("texture") + " not found.");
                continue;
            }

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

}
