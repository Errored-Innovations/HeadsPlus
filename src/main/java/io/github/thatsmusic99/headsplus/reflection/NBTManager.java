package io.github.thatsmusic99.headsplus.reflection;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTManager {

    /*
     * Reflection utils for managing NBT by Thatsmusic99
     *
     * A lot of these first methods are just used internally within the class but can be used outside too.
     *
     * For those that are new to NBT Tags, they are sets of data that can set the properties of an item or entity.
     * They can be used in the normal game, through ordinary commands such as /give and /summon. They also decide some
     * of the basic properties for entities, such as whether an entity is a baby or has no AI.
     *
     * If you've worked with HashMaps before, the concept is very similar - NBT tags have keys that lead to values.
     * IsBaby is an example of a key, 1 (true) is an example of a value. This would be formatted as {IsBaby:1}
     *
     * More on entity NBT tags: https://www.digminecraft.com/data_tags/index.php
     *
     * In plugin development, you can use custom NBT tags to create custom items without interfering with other items,
     * or even in GUIs where you decide the type of GUI icon being clicked by using NBT tags: the "icon-type" is the key,
     * and the actual icon ID/name is the type of icon you want.
     *
     * If you don't want to use an excessive amount of NBT keys in your items (you can see how many NBT keys there are in an
     * item using F3+H, if memory serves me right), you can set one key (e.g your plugin's name) and then JSON data as a value.
     * This is what I'm hoping to do in the future at least.
     *
     * My own plugin, HeadsPlus, uses NBT tags to do a lot of what I mentioned above - identifying GUI icons and head types when
     * using /sellhead, rather than trying to compare head textures and deciding which head belongs to which mob, which is far
     * more inefficient.
     *
     * Collecting the NMS version of the server is required, which I achieved using this horrible snippet that works at least:
     *
     * String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
     *
     * This utility is only currently designed to handle NBT in ItemStack objects.
     *
     * IMPORTANT NOTE: Set NBT tags AFTER you've finished setting up the actual ItemMeta of an item; doing it before will reset it.
     */

    /**
     * Returns a provided ItemStack as a NMS ItemStack.
     *
     * It accesses the asNMSCopy method from the CraftItemStack class so that
     * NBT tags can be applied to it.
     *
     * @param itemStack The ItemStack (from ordinary Bukkit) being parsed.
     * @return The new ItemStack in NMS form
     * @throws ClassNotFoundException If org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack isn't found.
     * @throws NoSuchMethodException If "asNMSCopy" isn't found.
     * @throws InvocationTargetException Thrown if an invoked method throws an error.
     * @throws IllegalAccessException If the method "asNMSCopy" is private, which thankfully, it isn't. Yet.
     * @see org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack#asNMSCopy(ItemStack)
     * @see net.minecraft.server.v1_15_R1.ItemStack - The class returned, except v1_15_R1 is replaced with the actual NMS version.
     */
    public static Object getNMSCopy(ItemStack itemStack) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Get the actual NMS version.
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        // Get the class via name.
        Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        // Now get the method of the NMS class.
        Method method = clazz.getMethod("asNMSCopy", Class.forName("org.bukkit.inventory.ItemStack"));
        // Call that method and return the result.
        return method.invoke(method, itemStack);
    }

    /**
     * Converts the ItemStack back into its Bukkit counterpart.
     *
     * This is so the item can be used in the plugin again.
     *
     * @param itemStack The NMS ItemStack being parsed as a Bukkit object again.
     * @return The NMS ItemStack as an ordinary Bukkit ItemStack (org.bukkit.inventory.ItemStack)
     * @throws ClassNotFoundException If org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack isn't found.
     * @throws NoSuchMethodException If the asBukkitCopy() method is not found.
     * @throws InvocationTargetException If there was an error thrown whilst invoking the method.
     * @throws IllegalAccessException If the method in question is private and cannot be accessed/is private.
     * @see org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack#asBukkitCopy(net.minecraft.server.v1_15_R1.ItemStack) (ItemStack)
     */
    public static ItemStack asBukkitCopy(Object itemStack) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        Class<?> itemClass = Class.forName("net.minecraft.server." + version + ".ItemStack");
        Method method = clazz.getMethod("asBukkitCopy", itemClass);
        return (ItemStack) method.invoke(method, itemStack);
    }

    /**
     * Gets the NBT Tag of the NMS ItemStack.
     *
     * @param itemStack The NMS ItemStack which is having its tag checked.
     * @return The NBTTagCompound of the ItemStack.
     * @throws NoSuchMethodException If the getTag() method isn't found.
     * @throws InvocationTargetException If there was an error thrown when invoking the method.
     * @throws IllegalAccessException If the method is inaccessible/private.
     * @see net.minecraft.server.v1_15_R1.ItemStack#getTag()
     */
    public static Object getNBTTag(Object itemStack) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = itemStack.getClass().getMethod("getTag");
        return method.invoke(itemStack);
    }

    /**
     * Sets the NBT Tag of an ItemStack.
     *
     * @param itemStack The ItemStack (NMS) having its NBT tag set.
     * @param nbtTag The actual NBT Tag being applied to the ItemStack.
     * @return The new, modified ItemStack (NMS).
     * @throws ClassNotFoundException if net.minecraft.server.VERSION.NBTTagCompound doesn't exist.
     * @throws NoSuchMethodException if setTag(NBTTagCompound) doesn't exist.
     * @throws InvocationTargetException if there was an error invoking the method.
     * @throws IllegalAccessException If the method accessed is private.
     * @see net.minecraft.server.v1_15_R1.ItemStack#setTag(net.minecraft.server.v1_15_R1.NBTTagCompound)
     * (I don't know why this one doesn't direct itself to its declaration...)
     */
    public static Object setNBTTag(Object itemStack, Object nbtTag) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Class<?> clazz = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
        Method method = itemStack.getClass().getMethod("setTag", clazz);
        method.invoke(itemStack, nbtTag);
        return itemStack;
    }

    /**
     * Creates a new, empty NBT Tag.
     *
     * @return As stated above, it just creates a new NBT tag!
     * @throws ClassNotFoundException if net.minecraft.server.VERSION.NBTTagCompound doesn't exist.
     * @throws NoSuchMethodException if the constructor doesn't exist.
     * @throws IllegalAccessException if the constructor accessed is private.
     * @throws InvocationTargetException if there was an error invoking the constructor.
     * @throws InstantiationException if there was an error creating a new NBTTagCompound instance.
     * @see net.minecraft.server.v1_15_R1.NBTTagCompound
     */
    public static Object newNBTTag() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String version = HeadsPlus.getInstance().getNMS().getNMSVersion();
        Constructor<?> con = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").getConstructor();
        return con.newInstance();
    }

    // Now for the more interesting bits.

    /**
     * This is just a method used by other methods here.
     *
     * However, this method returns the value as to which the parameter nbtKet is mapped to.
     *
     * There are separate methods that utilise this because I don't consider this method safe to be used on its own.
     *
     * @param itemStack The ItemStack having its NBT Tag checked.
     * @param methodName The method name being used here.
     * @param nbtKey The NBT key we're accessing to get its value.
     * @return The value of which we accessed using the nbtKey parameter.
     * @see java.util.HashMap#get(Object) - Practically the same thing, just with more arguments to pass!
     * @see #getString(ItemStack, String)
     * @see #getBoolean(ItemStack, String)
     * @see #getByte(ItemStack, String)
     * @see #getByteArray(ItemStack, String)
     * @see #getDouble(ItemStack, String)
     * @see #getFloat(ItemStack, String)
     * @see #getInt(ItemStack, String)
     * @see #getIntArray(ItemStack, String)
     * @see #getLong(ItemStack, String)
     * @see #getShort(ItemStack, String)
     */
    private static Object getObject(ItemStack itemStack, String methodName, String nbtKey) {
        try {
            Object nmsItem = getNMSCopy(itemStack);
            if (nmsItem == null) return new ItemStack(Material.AIR);
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

    /**
     * Maps a value to a key in the provided NBT tag and applies that tag to the given ItemStack.
     *
     * @param itemStack The ItemStack having its tag modified.
     * @param methodName The name of the method to be accessed.
     * @param nbtKey The NBT key being used.
     * @param value The value being mapped to the NBT key.
     * @param paramType The class type of the parameter being used - depends on the method being used.
     * @return The ItemStack with its NBT tag modified.
     * @see java.util.HashMap#put(Object, Object)
     * @see #setString(ItemStack, String, String)
     * @see #setBoolean(ItemStack, String, boolean)
     * @see #setByte(ItemStack, String, byte)
     * @see #setByteArray(ItemStack, String, byte...)
     * @see #setDouble(ItemStack, String, double)
     * @see #setFloat(ItemStack, String, float)
     * @see #setInt(ItemStack, String, int)
     * @see #setIntArray(ItemStack, String, int...)
     * @see #setLong(ItemStack, String, long)
     * @see #setShort(ItemStack, String, short)
     */
    private static ItemStack setObject(ItemStack itemStack, String methodName, String nbtKey, Object value, Class<?> paramType) {
        try {
            Object nmsItem = getNMSCopy(itemStack);
            if (nmsItem == null) return new ItemStack(Material.AIR);
            Object nbtTag = getNBTTag(nmsItem);
            if (nbtTag == null) {
                nbtTag = newNBTTag();
            }
            Method method1 = nbtTag.getClass().getMethod(methodName, String.class, paramType);
            method1.invoke(nbtTag, nbtKey, value);
            nmsItem = setNBTTag(nmsItem, nbtTag);
            return asBukkitCopy(nmsItem);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    /**
     * Maps a NBT key to a value using the data type String.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a String - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setString(ItemStack itemStack, String nbtKey, String value) {
        return setObject(itemStack, "setString", nbtKey, value, String.class);
    }

    /**
     * Maps a NBT key to a value using the data type Double.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Double - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setDouble(ItemStack itemStack, String nbtKey, double value) {
        return setObject(itemStack, "setDouble", nbtKey, value, double.class);
    }

    /**
     * Maps a NBT key to a value using the data type Boolean.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Boolean - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setBoolean(ItemStack itemStack, String nbtKey, boolean value) {
        return setObject(itemStack, "setBoolean", nbtKey, value, boolean.class);
    }

    /**
     * Maps a NBT key to a value using the data type Byte.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Byte - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setByte(ItemStack itemStack, String nbtKey, byte value) {
        return setObject(itemStack, "setByte", nbtKey, value, byte.class);
    }

    /**
     * Maps a NBT key to a value using the data type Integer.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - an Integer - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setInt(ItemStack itemStack, String nbtKey, int value) {
        return setObject(itemStack, "setInt", nbtKey, value, int.class);
    }

    /**
     * Maps a NBT key to a value using the data type Long.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Long - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setLong(ItemStack itemStack, String nbtKey, long value) {
        return setObject(itemStack, "setLong", nbtKey, value, long.class);
    }

    /**
     * Maps a NBT key to a value using the data type Short.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Short - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setShort(ItemStack itemStack, String nbtKey, short value) {
        return setObject(itemStack, "setShort", nbtKey, value, short.class);
    }

    /**
     * Maps a NBT key to a value using the data type Float.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The value - a Float - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setFloat(ItemStack itemStack, String nbtKey, float value) {
        return setObject(itemStack, "setFloat", nbtKey, value, float.class);
    }

    /**
     * Maps a NBT key to a set of bytes.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The values - bytes - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setByteArray(ItemStack itemStack, String nbtKey, byte... values) {
        return setObject(itemStack, "setByteArray", nbtKey, values, byte[].class);
    }

    /**
     * Maps a NBT key to a set of integers.
     *
     * @param itemStack The ItemStack having its NBT tag modified.
     * @param nbtKey The name of the key being set.
     * @param value The values - integers - being mapped to the key.
     * @return The modified ItemStack with its new NBT tag.
     */
    public static ItemStack setIntArray(ItemStack itemStack, String nbtKey, int... values) {
        return setObject(itemStack, "setIntArray", nbtKey, values, int[].class);
    }

    /**
     * Retrieves a value - of data type byte - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static byte getByte(ItemStack itemStack, String nbtKey) {
        return Byte.valueOf(String.valueOf(getObject(itemStack, "setByte", nbtKey)));
    }

    /**
     * Retrieves a value - of data type string - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static String getString(ItemStack itemStack, String nbtKey) {
        return String.valueOf(getObject(itemStack, "getString", nbtKey));
    }

    /**
     * Retrieves a value - of data type double - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static double getDouble(ItemStack itemStack, String nbtKey) {
        return Double.valueOf(String.valueOf(getObject(itemStack, "getDouble", nbtKey)));
    }

    /**
     * Retrieves a value - of data type boolean - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static boolean getBoolean(ItemStack itemStack, String nbtKey) {
        Object result = getObject(itemStack, "getBoolean", nbtKey);
        return result instanceof Boolean && (boolean) result;
    }

    /**
     * Retrieves a value - of data type int - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static int getInt(ItemStack itemStack, String nbtKey) {
        return Integer.parseInt(String.valueOf(getObject(itemStack, "getInt", nbtKey)));
    }

    /**
     * Retrieves a value - of data type long - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static long getLong(ItemStack itemStack, String nbtKey) {
        return Long.valueOf(String.valueOf(getObject(itemStack, "getLong", nbtKey)));
    }

    /**
     * Retrieves a value - of data type short - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static short getShort(ItemStack itemStack, String nbtKey) {
        return Short.valueOf(String.valueOf(getObject(itemStack, "getShort", nbtKey)));
    }

    /**
     * Retrieves a value - of data type float - according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static float getFloat(ItemStack itemStack, String nbtKey) {
        return Float.valueOf(String.valueOf(getObject(itemStack, "getFloat", nbtKey)));
    }

    /**
     * Retrieves a set of values (bytes) according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static byte[] getByteArray(ItemStack itemStack, String nbtKey) {
        return (byte[]) getObject(itemStack, "getByteArray", nbtKey);
    }

    /**
     * Retrieves a set of values (integers) according to key.
     * @param itemStack The ItemStack having its NBT tag checked.
     * @param nbtKey The name of the key being used.
     * @return The value being mapped to the given key.
     */
    public static int[] getIntArray(ItemStack itemStack, String nbtKey) {
        return (int[]) getObject(itemStack, "getIntArray", nbtKey);
    }

    // And below are some personal examples I use! Enjoy.
    public static ItemStack makeSellable(ItemStack i) {
        return setBoolean(i, "headsplus-sell", true);
    }

    public static boolean isSellable(ItemStack i) {
        return getObject(i, "getBoolean", "headsplus-sell") instanceof Boolean && (boolean) getObject(i, "getBoolean", "headsplus-sell");
    }

    public static ItemStack setType(ItemStack i, String type) {
        return setString(i, "headsplus-type", type);
    }

    // This is a funny one, ignore this example
    public static ItemStack addDatabaseHead(ItemStack i, String id, double price) {
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

    public static String getType(ItemStack i) {
        return getString(i, "headsplus-type");
    }

    public static String getID(ItemStack i) {
        return getString(i, "head-id");
    }

    public static double getPrice(ItemStack i) {
        return getDouble(i,  "head-price");
    }

    public static ItemStack addSection(ItemStack i, String section) {
        return setString(i, "head-section", section);
    }

    public static String getSection(ItemStack i) {
        return getString(i, "head-section");
    }

    public static Icon getIcon(ItemStack i) {
        return Icon.getIconFromName(getString(i,  "head-icon"));
    }

    public static ItemStack setIcon(ItemStack i, Icon icon) {
        return setString(i, "head-icon", icon.getIconName());
    }

    public ItemStack setChallenge(ItemStack i, Challenge c) {
        return setString(i, "head-challenge", c.getConfigName());
    }

    public static Challenge getChallenge(ItemStack i) {
        return  HeadsPlus.getInstance().getChallengeByName(getString(i,  "head-challenge"));
    }

    public ItemStack setChallengeSection(ItemStack i, ChallengeSection section) {
        return setString(i, "head-challenge-section", section.getName());
    }

    public String getChallengeSection(ItemStack i) {
        return  getString(i,  "head-challenge-section");
    }

    public static ItemStack removeIcon(ItemStack i) {
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

    public static ItemStack setPrice(ItemStack i, double price) {
        return setDouble(i, "head-price", price);
    }


}
