package io.github.thatsmusic99.headsplus.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class Head {

    protected ItemStack itemStack;
    private String id;
    private double price; // Pretty much central anyways

    public Head(String id) {
        this(id, 3);
    }

    public Head(String id, int data) {
        this.itemStack = HeadsPlus.getInstance().getNMS().getSkull(data);
        this.id = id;
    }

    public Head withDisplayName(String name) {
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }

    public Head withTexture(String texture) throws NoSuchFieldException, IllegalAccessException {
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile gm = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
        gm.getProperties().put("textures", new Property("texture", texture));
        Field profile = skullMeta.getClass().getDeclaredField("profile");
        profile.setAccessible(true);
        profile.set(skullMeta, gm);
        itemStack.setItemMeta(skullMeta);
        return this;
    }

    public Head withPlayerName(String name) {
        itemStack.setItemMeta(HeadsPlus.getInstance().getNMS().setSkullOwner(name, (SkullMeta) itemStack.getItemMeta()));
        return this;
    }

    public Head withLore(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public Head withPrice(double price) {
        this.price = price;
        NBTManager.setPrice(itemStack, price);
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getId() {
        return id;
    }

    public Head withAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
}
