package io.github.thatsmusic99.headsplus.nms.v1_16_R2;

import io.github.thatsmusic99.headsplus.nms.SearchGUI;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.github.thatsmusic99.headsplus.util.AnvilSlot;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchGUIUtil extends SearchGUI {

    public SearchGUIUtil(Player player, SearchGUI.AnvilClickEventHandler handler) {
        super(player, handler);
    }

    static boolean compat_mode = true;
    static {
        for(Method m : ContainerProperty.class.getMethods()) {
            if(m.getName().equals("set")) {
                compat_mode = false;
                break;
            }
        }
    }

    private static class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(int id, EntityHuman entity) {
            super(id, entity.inventory, ContainerAccess.at(entity.world, new BlockPosition(0, 0, 0)));
            checkReachable = true;
        }

        @Override
        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void e() {
            super.e();
            if(compat_mode) {
                try {
                    ContainerProperty.class.getDeclaredMethod("a", int.class).invoke(this.levelCost, 0);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(io.github.thatsmusic99.headsplus.nms.v1_14_R1.SearchGUIUtil.class.getName()).log(Level.SEVERE, "Anvil Error", ex);
                }
            } else {
                this.levelCost.set(0);
            }
        }
    }


    public void open() {
        EntityPlayer p = ProfileFetcher.getHandle(getPlayer());
        final int id = p.nextContainerCounter();
        io.github.thatsmusic99.headsplus.nms.v1_16_R2.SearchGUIUtil.AnvilContainer container = new io.github.thatsmusic99.headsplus.nms.v1_16_R2.SearchGUIUtil.AnvilContainer(id, p);
        inv = container.getBukkitView().getTopInventory();
        for (AnvilSlot slot : items.keySet()) {
            inv.setItem(slot.getSlot(), items.get(slot));
        }
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(id, Containers.ANVIL, new ChatMessage("Repairing")));
        container.setTitle(new ChatMessage("Repairing"));
        p.activeContainer = container;
        try {
            Field profileField;
            profileField = Container.class.getDeclaredField("windowId");
            profileField.setAccessible(true);
            profileField.set(p.activeContainer, id);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        p.activeContainer.addSlotListener(p);
    }
}
