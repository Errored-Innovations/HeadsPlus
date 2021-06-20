package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PersistenceManager {

    private static PersistenceManager instance;
    private static final NamespacedKey HEADSPLUS_STORAGE = new NamespacedKey(HeadsPlus.get(), "storage");
    private static final NamespacedKey HEADSPLUS_SELL_PRICE = new NamespacedKey(HeadsPlus.get(), "sell_price");
    private static final NamespacedKey HEADSPLUS_SELL_TYPE = new NamespacedKey(HeadsPlus.get(), "sell_type");
    private static final NamespacedKey HEADSPLUS_SELL_BOOLEAN = new NamespacedKey(HeadsPlus.get(), "sell_boolean");
    private static final NamespacedKey HEADSPLUS_INVENTORY_ICON = new NamespacedKey(HeadsPlus.get(), "inventory_icon");

    public PersistenceManager() {
        instance = this;
    }

    public static PersistenceManager get() {
        return instance;
    }

    public void setSellPrice(ItemStack item, double price) {
        setValue(item, HEADSPLUS_SELL_PRICE, PersistentDataType.DOUBLE, price);
    }

    public void setSellType(ItemStack item, String type) {
        setValue(item, HEADSPLUS_SELL_TYPE, PersistentDataType.STRING, type);
    }

    public void setSellable(ItemStack item, boolean flag) {
        setValue(item, HEADSPLUS_SELL_BOOLEAN, PersistentDataType.BYTE, flag ? (byte) 1 : (byte) 0);
    }

    public boolean isSellable(ItemStack item) {
        Byte result = getValue(item, HEADSPLUS_SELL_BOOLEAN, PersistentDataType.BYTE);
        return result != null && result == (byte) 1;
    }

    public String getSellType(ItemStack item) {
        return getValue(item, HEADSPLUS_SELL_TYPE, PersistentDataType.STRING);
    }

    public double getSellPrice(ItemStack item) {
        return ifNull(getValue(item, HEADSPLUS_SELL_PRICE, PersistentDataType.DOUBLE), 0.0);
    }

    public void makeIcon(ItemStack item) {
        setValue(item, HEADSPLUS_INVENTORY_ICON, PersistentDataType.BYTE, (byte) 1);
    }

    public boolean isIcon(ItemStack item) {
        return ifNull(getValue(item, HEADSPLUS_INVENTORY_ICON, PersistentDataType.BYTE), (byte) 0) == 1;
    }

    public void removeIcon(ItemStack item) {
        setValue(item, HEADSPLUS_INVENTORY_ICON, PersistentDataType.BYTE, (byte) 0);
    }

    @NotNull
    private <T> T ifNull(T object, @NotNull T alternative) {
        return object == null ? alternative : object;
    }

    private <T, Z> void setValue(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> dataType, Z object) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = getStorage(meta);
        if (container == null) {
            PersistentDataAdapterContext adapterContext = meta.getPersistentDataContainer().getAdapterContext();
            container = adapterContext.newPersistentDataContainer();
        }
        container.set(key, dataType, object);
        setStorage(meta, container);
        item.setItemMeta(meta);
    }

    @Nullable
    private <T, Z> Z getValue(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> dataType) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = getStorage(meta);
        if (container == null) return null;
        return container.get(key, dataType);
    }

    private @Nullable PersistentDataContainer getStorage(@NotNull ItemMeta meta) {
        PersistentDataContainer mainContainer = meta.getPersistentDataContainer();
        return mainContainer.get(HEADSPLUS_STORAGE, PersistentDataType.TAG_CONTAINER);
    }

    private void setStorage(@NotNull ItemMeta meta, PersistentDataContainer container) {
        meta.getPersistentDataContainer().set(HEADSPLUS_STORAGE, PersistentDataType.TAG_CONTAINER, container);
    }
}
