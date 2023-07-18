package io.github.thatsmusic99.headsplus.hooks.shopguiplus;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopGUIPlusHook implements Listener {

    @EventHandler
    public void onShopGUIEnable(ShopGUIPlusPostEnableEvent event) {
        ShopGuiPlusApi.registerItemProvider(new HeadsPlusItemProvider());
        HeadsPlus.get().getLogger().info("Registered HeadsPlus as an item provider in ShopGUI+!");
    }
}
