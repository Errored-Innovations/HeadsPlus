package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PersistenceManager {

    private final PersistenceManager instance;
    private static final NamespacedKey HEADSPLUS_STORAGE = new NamespacedKey(HeadsPlus.getInstance(), "storage");
    private static final NamespacedKey HEADSPLUS_SELL_PRICE = new NamespacedKey(HeadsPlus.getInstance(), "sell_price");
    private static final NamespacedKey HEADSPLUS_SELL_TYPE = new NamespacedKey(HeadsPlus.getInstance(), "sell_type");
    private static final NamespacedKey HEADSPLUS_SELL_BOOLEAN = new NamespacedKey(HeadsPlus.getInstance(), "sell_boolean");

    public PersistenceManager() {
        instance = this;
    }

    public PersistenceManager get() {
        return instance;
    }

    public void setSellPrice(ItemStack item, double price) {
        setValue(item, HEADSPLUS_SELL_PRICE, PersistentDataType.DOUBLE, price);
    }

    public void setSellType(ItemStack item, String type) {
        setValue(item, HEADSPLUS_SELL_TYPE, PersistentDataType.STRING, type);
    }

    private <T, Z> void setValue(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> dataType, Z object) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = getStorage(meta);
        if (container == null) return;
        container.set(key, dataType, object);
        setStorage(meta, container);
        item.setItemMeta(meta);
    }

    private @Nullable PersistentDataContainer getStorage(@NotNull ItemMeta meta) {
        PersistentDataContainer mainContainer = meta.getPersistentDataContainer();
        return mainContainer.get(HEADSPLUS_STORAGE, PersistentDataType.TAG_CONTAINER);
    }

    private void setStorage(@NotNull ItemMeta meta, PersistentDataContainer container) {
        meta.getPersistentDataContainer().set(HEADSPLUS_STORAGE, PersistentDataType.TAG_CONTAINER, container);
    }
}
