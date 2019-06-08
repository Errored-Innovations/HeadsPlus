package io.github.thatsmusic99.headsplus.reflection;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTManager {

    // Reflection utils
    public Object getNMSCopy(ItemStack i) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        Method method = clazz.getMethod("asNMSCopy", Class.forName("org.bukkit.inventory.ItemStack"));
        return method.invoke(method, i);
    }

    public ItemStack asBukkitCopy(Object o) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        Class<?> itemClass = Class.forName("net.minecraft.server." + version + ".ItemStack");
        Method method = clazz.getMethod("asBukkitCopy", itemClass);
        return (ItemStack) method.invoke(method, o);
    }

    public Object getNBTTag(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = o.getClass().getMethod("getTag");
        return method.invoke(o);
    }

    public Object setNBTTag(Object o, Object nbt) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Class<?> clazz = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
        Method method = o.getClass().getMethod("setTag", clazz);
        method.invoke(o, nbt);
        return o;
    }


    public Object newNBTTag() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Constructor<?> con = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").getConstructor();
        return con.newInstance();
    }

    public ItemStack makeSellable(ItemStack i) {
        return setObject(i, "setBoolean", "headsplus-sell", true, String.class, boolean.class);
    }

    public boolean isSellable(ItemStack i) {
        return (boolean) getObject(i, "getBoolean", "headsplus-sell");
    }

    public ItemStack setType(ItemStack i, String type) {
        return setObject(i, "setString", "headsplus-type", type, String.class, String.class);
    }

    public ItemStack addDatabaseHead(ItemStack i, String id, double price) {
        try {
            Object nmsItem = getNMSCopy(i);
            Object nbtTag = getNBTTag(nmsItem);
            if (nbtTag == null) {
                nbtTag = newNBTTag();
            }
            Method method = nbtTag.getClass().getMethod("setString", String.class, String.class);
            method.invoke(nbtTag, "head-id", id);
            Method method1 = nbtTag.getClass().getMethod("setDouble", String.class, double.class);
            method1.invoke(nbtTag, "head-price", price);
            nmsItem = setNBTTag(nmsItem, nbtTag);
            return asBukkitCopy(nmsItem);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return i;
    }

    public String getType(ItemStack i) {
        return String.valueOf(getObject(i, "getString", "headsplus-type"));
    }

    public String getID(ItemStack i) {
        return String.valueOf(getObject(i, "getString", "head-id"));
    }

    public double getPrice(ItemStack i) {
        return (double) getObject(i, "getDouble", "head-price");
    }

    public ItemStack addSection(ItemStack i, String section) {
        return setObject(i, "setString", "head-section", section, String.class, String.class);
    }

    public String getSection(ItemStack i) {
        return String.valueOf(getObject(i, "getString", "head-section"));
    }

    public Icon getIcon(ItemStack i) {
        return Icon.getIconFromName(String.valueOf(getObject(i, "getString", "head-icon")));
    }

    public ItemStack setIcon(ItemStack i, Icon icon) {
        return setObject(i, "setString", "head-icon", icon.getIconName(), String.class, String.class);
    }

    public ItemStack setChallenge(ItemStack i, Challenge c) {
        return setObject(i, "setString", "head-challenge", c.getConfigName(), String.class, String.class);
    }

    public Challenge getChallenge(ItemStack i) {
        return  HeadsPlus.getInstance().getChallengeByName(String.valueOf(getObject(i, "getString", "head-challenge")));
    }

    public ItemStack removeIcon(ItemStack i) {
        try {
            Object nmsItem = getNMSCopy(i);
            Object nbtTag = getNBTTag(nmsItem);
            if (nbtTag == null) {
                nbtTag = newNBTTag();
            }
            Method method = nbtTag.getClass().getMethod("remove", String.class);
            method.invoke(nbtTag, "head-icon");
            nmsItem = setNBTTag(nmsItem, nbtTag);
            return asBukkitCopy(nmsItem);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return i;
    }

    public ItemStack setPrice(ItemStack i, double price) {
        return setObject(i, "setDouble", "head-price", price, String.class, double.class);
    }

    private Object getObject(ItemStack i, String methodName, String nbtKey) {
        try {
            Object nmsItem = getNMSCopy(i);
            Object nbtTag = getNBTTag(nmsItem);
            if (nbtTag == null) {
                nbtTag = newNBTTag();
            }
            Method method = nbtTag.getClass().getMethod(methodName, String.class);
            return method.invoke(nbtTag, nbtKey);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ItemStack setObject(ItemStack i, String methodName, String nbtKey, Object value, Class<?>... params) {
        try {
            Object nmsItem = getNMSCopy(i);
            if (nmsItem == null) return new ItemStack(Material.AIR);
            Object nbtTag = getNBTTag(nmsItem);
            if (nbtTag == null) {
                nbtTag = newNBTTag();
            }
            Method method1 = nbtTag.getClass().getMethod(methodName, params);
            method1.invoke(nbtTag, nbtKey, value);
            nmsItem = setNBTTag(nmsItem, nbtTag);
            return asBukkitCopy(nmsItem);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return i;
    }
}
