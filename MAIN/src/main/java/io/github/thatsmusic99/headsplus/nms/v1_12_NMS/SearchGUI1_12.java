package io.github.thatsmusic99.headsplus.nms.v1_12_NMS;

import io.github.thatsmusic99.headsplus.nms.SearchGUI;
import io.github.thatsmusic99.headsplus.util.AnvilSlot;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Programmed by Tevin on 8/8/2015.
 */
public class SearchGUI1_12 extends SearchGUI {

    public SearchGUI1_12(Player player, SearchGUI.AnvilClickEventHandler handler) {
        super(player, handler);
    }

    // Util by ChaseChocolate/frostythedev on Github
    // https://gist.github.com/frostythedev/95b5d494a1697ad4a0a4

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entity) {
            super(entity.inventory, entity.world,new BlockPosition(0, 0, 0), entity);
        }

        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void e() {
            super.e();
            this.levelCost = 0;
        }
    }


    public void open() {
        EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();

        AnvilContainer container = new AnvilContainer(p);

        //Set the items to the items from the inventory given
        inv = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) {
            inv.setItem(slot.getSlot(), items.get(slot));
        }

        //Counter stuff that the game uses to keep track of inventories
        int c = p.nextContainerCounter();

        //Send the packet
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        //Set their active container to the container
        p.activeContainer = container;

        //Set their active container window id to that counter stuff
        p.activeContainer.windowId = c;

        //Add the slot listener
        p.activeContainer.addSlotListener(p);
    }
}