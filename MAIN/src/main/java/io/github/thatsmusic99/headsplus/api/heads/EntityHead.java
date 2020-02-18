package io.github.thatsmusic99.headsplus.api.heads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;

public class EntityHead extends Head {

    public EntityHead(String id) {
        super(id);
        itemStack = NBTManager.makeSellable(itemStack);
        itemStack = NBTManager.setType(itemStack, id);
        itemStack.setType(HeadsPlus.getInstance().getNMS().getSkull(3).getType());
    }

    public EntityHead(String id, int data) {
        super(id, data);
        itemStack = NBTManager.makeSellable(itemStack);
        itemStack = NBTManager.setType(itemStack, id);
        itemStack.setType(HeadsPlus.getInstance().getNMS().getSkull(data).getType());
    }
}
