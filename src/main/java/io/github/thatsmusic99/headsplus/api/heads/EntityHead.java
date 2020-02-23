package io.github.thatsmusic99.headsplus.api.heads;

import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;

public class EntityHead extends Head {

    public EntityHead(String id) {
        this(id, 3);
    }

    public EntityHead(String id, int data) {
        super(id, data);
   //     itemStack.setType(Material.DIAMOND);
        itemStack = NBTManager.makeSellable(itemStack);
        itemStack = NBTManager.setType(itemStack, id);
    //    itemStack.setType(HeadsPlus.getInstance().getNMS().getSkull(data).getType());
    }
}
