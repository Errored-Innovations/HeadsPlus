package io.github.thatsmusic99.headsplus.api.heads;

import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import org.bukkit.Material;

@Deprecated
public class EntityHead extends Head {

    public EntityHead(String id, Material type) {
        super(id, type);
   //     itemStack.setType(Material.DIAMOND);
        PersistenceManager.get().setSellable(itemStack, true);
        PersistenceManager.get().setSellType(itemStack, id);
    //    itemStack.setType(HeadsPlus.getInstance().getNMS().getSkull(data).getType());
    }
}
