package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigItems;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.SellheadHead;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellheadCategory extends BaseInventory {
    public SellheadCategory(Player player) {
        super(player);
    }

    @Override
    public String getDefaultTitle() {
        return null;
    }

    @Override
    public String getDefaultItems() {
        return null;
    }

    @Override
    public String getDefaultId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void gatherContents(HashMap<String, String> context, Player player) {
        HeadsPlusConfigItems itemsConf = HeadsPlus.getInstance().getItems();
        String items = itemsConf.getConfig().getString("inventories.sellheadcategory.icons");
        int contentsPerPage = HPUtils.matchCount(CachedValues.CONTENT_PATTERN.matcher(items));
        contents = new PagedLists<>(transformContents(context, player), contentsPerPage);
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        switch (context.get("type")) { // ignore
            case "mobs":
                HashMap<String, List<EntityHead>> heads = HeadsPlus.getInstance().getDeathEvents().getStoredHeads();
                for (String str : heads.keySet()) {
                    if (heads.get(str).size() > 0) {
                        contents.add(new SellheadHead(heads.get(str).get(0).getItemStack()));
                    }
                }
                break;
            case "mining":
            case "farming":
            case "fishing":
            case "crafting":
                break;
        }
        return contents;
    }

    @Override
    public String getContentType() {
        return null;
    }
}
