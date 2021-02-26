package io.github.thatsmusic99.headsplus.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Head {

    private static final PaperUtil util = new PaperUtil();

    protected ItemStack itemStack;
    private String name;
    private final String id;
    private double price; // Pretty much central anyways
    private int data;

    public Head(String id) {
        this(id, 3);
        data = 3;
    }

    public Head(String id, int data) {
        this.itemStack = HeadsPlus.getInstance().getNMS().getSkull(data);
        if (!HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("sellhead-ids-case-sensitive")) {
            id = id.toLowerCase();
        }
        this.id = id;
        this.data = data;
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
        byte[] encodedData;
        if (CachedValues.MINECRAFT_TEXTURES_PATTERN.matcher(texture).matches()) {
            encodedData = Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", texture).getBytes());
        } else if (CachedValues.BASE64_PATTERN.matcher(texture).matches()) {
            encodedData = texture.getBytes();
        } else {
            encodedData = Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
        }
        gm.getProperties().put("textures", new Property("texture", new String(encodedData)));
        Field profile = skullMeta.getClass().getDeclaredField("profile");
        profile.setAccessible(true);
        profile.set(skullMeta, gm);
        itemStack.setItemMeta(skullMeta);
        this.name = null; // overwritten by textures
        return this;
    }

    public Head withPlayerName(String name) {
        this.name = name;
        /*
        SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
    //    if (sm == null) return this;
        sm = HeadsPlus.getInstance().getNMS().setSkullOwner(name, sm);
        itemStack.setItemMeta(sm);
         */
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
        itemStack.setType(Material.DIAMOND);
        itemStack = NBTManager.setPrice(itemStack, price);
        itemStack.setType(HeadsPlus.getInstance().getNMS().getSkull(data).getType());
        return this;
    }

    public ItemStack getItemStack() {
        if (this.name != null) {
            // set sync
            SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
            sm = HeadsPlus.getInstance().getNMS().setSkullOwner(this.name, sm);
            itemStack.setItemMeta(sm);
        }
        return itemStack;
    }

    public CompletableFuture<ItemStack> getItemStackFuture() {
        if (this.name == null) {
            return CompletableFuture.completedFuture(this.itemStack);
        }
        SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
        return util.setProfile(sm, this.name).thenApply(newMeta -> {
            itemStack.setItemMeta(newMeta);
            return itemStack;
        });
    }

    public String getId() {
        return id;
    }

    public Head withAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public int getData() {
        return data;
    }
}
