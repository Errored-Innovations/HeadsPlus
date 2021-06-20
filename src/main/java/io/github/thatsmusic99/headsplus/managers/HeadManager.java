package io.github.thatsmusic99.headsplus.managers;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HeadManager {

    private final HashMap<String, HeadInfo> heads = new HashMap<>();
    private static HeadManager instance;

    public HeadManager() {
        instance = this;
    }

    public static HeadManager get() {
        return instance;
    }

    public void registerHead(String key, HeadInfo headInfo) {
        heads.put(key, headInfo);
    }

    public HeadInfo getHeadInfo(String key) {
        if (key.startsWith("HP#")) {
            key = key.substring(3);
        }
        return heads.getOrDefault(key, new HeadInfo()).clone();
    }

    public static class HeadInfo implements Cloneable {

        @Nullable
        private String displayName;
        @NotNull
        private List<String> lore;
        private String texture;
        private String player;
        private Material material;

        public HeadInfo() {
            this.material = Material.PLAYER_HEAD;
            this.lore = new ArrayList<>();
        }

        public HeadInfo withDisplayName(String name) {
            this.displayName = name;
            return this;
        }

        public HeadInfo withLore(String... lore) {
            this.lore = Lists.newArrayList(lore);
            return this;
        }

        public HeadInfo withMaterial(Material material) {
            this.material = material;
            return this;
        }

        public HeadInfo withTexture(String str) {
            if (str.length() < 17) {
                this.player = str;
                return this;
            }

            if (str.matches("^[0-9a-fA-F]+$")) {
                str = "https://textures.minecraft.net/texture/" + str;
            }
            if (str.startsWith("http")) {

            }
            this.texture = str;
            return this;
        }

        public List<String> getLore() {
            return lore;
        }

        public Material getMaterial() {
            return material;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPlayer() {
            return player;
        }

        public String getTexture() {
            return texture;
        }

        public void setLore(@NotNull List<String> lore) {
            this.lore = lore;
        }

        public CompletableFuture<ItemStack> buildHead() {
            ItemStack head = new ItemStack(material);
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            if (material != Material.PLAYER_HEAD) return CompletableFuture.completedFuture(head);
            if (player != null) {
                ((SkullMeta) meta).setOwner(player);
            } else if (texture != null) {
                return new PaperUtil().setProfile((SkullMeta) meta, texture).thenApply(newMeta -> {
                    head.setItemMeta(newMeta);
                    return head;
                });
            }
            head.setItemMeta(meta);
            return CompletableFuture.completedFuture(head);
        }

         @Override
         public HeadInfo clone() {
             try {
                 return (HeadInfo) super.clone();
             } catch (CloneNotSupportedException e) {
                 e.printStackTrace();
             }
             return new HeadInfo();
         }
    }

}
