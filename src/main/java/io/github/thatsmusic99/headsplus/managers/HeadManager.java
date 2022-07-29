package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.util.paper.PaperUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HeadManager {

    private final HashMap<String, HeadInfo> heads = new HashMap<>();
    private final HashMap<String, String> textures = new HashMap<>();
    private static HeadManager instance;

    public HeadManager() {
        instance = this;
    }

    public static HeadManager get() {
        return instance;
    }

    public void reset() {
        heads.clear();
    }

    public void registerHead(String key, HeadInfo headInfo) {
        heads.put(key, headInfo);
        if (headInfo.texture != null) {
            textures.put(headInfo.texture, key);
        }
        HeadsPlus.debug("Registered head " + key + ".");
    }

    public boolean contains(String key) {
        if (key.startsWith("HPM#")) return MaskManager.get().isMaskRegistered(key);
        if (key.startsWith("HP#")) {
            key = key.substring(3);
        }
        return heads.containsKey(key);
    }

    public HeadInfo getHeadInfo(String key) {
        if (key.startsWith("HPM#")) {
            if (MaskManager.get().isMaskRegistered(key)) return MaskManager.get().getMaskInfo(key);
            key = key.substring(4);
            String oldKey = key;
            if (ConfigMobs.get().isLoaded()) {
                key = ConfigMobs.get().getString("masks." + key + ".idle");
            }
            if (key == null)
                throw new NullPointerException("The idle mask ID for " + oldKey + " appears to not exist, was there an error when registering masks?");
        }
        if (key.startsWith("HP#")) {
            key = key.substring(3);
        }
        return heads.getOrDefault(key, new HeadInfo()).clone();
    }

    public Set<String> getKeys() {
        return heads.keySet();
    }

    public Set<String> getAddedTextures() {
        return textures.keySet();
    }

    public String getId(String texture) {
        return textures.get(texture);
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

        public HeadInfo withDisplayName(@Nullable String name) {
            if (name == null) return this;
            this.displayName = ChatColor.translateAlternateColorCodes('&', name);
            return this;
        }

        public HeadInfo withLore(String... lore) {
            this.lore = new ArrayList<>();
            for (String str : lore) {
                if (str == null) continue;
                this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
            }
            return this;
        }

        public HeadInfo withMaterial(Material material) {
            this.material = material;
            return this;
        }

        public HeadInfo withTexture(@NotNull String str) {
            if (str.length() < 17) {
                this.player = str;
                return this;
            }

            if (str.matches("^[0-9a-fA-F]+$")) {
                str = "https://textures.minecraft.net/texture/" + str;
            }
            if (str.startsWith("http")) {
                str = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", str);
                str = new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)));
            }
            this.texture = str;
            return this;
        }

        @NotNull
        public List<String> getLore() {
            return lore;
        }

        public Material getMaterial() {
            return material;
        }

        @Nullable
        public String getDisplayName() {
            return displayName;
        }

        public String getPlayer() {
            return player;
        }

        public String getTexture() {
            return texture;
        }

        public void setLore(@Nullable List<String> lore) {
            if (lore == null) return;
            this.lore = new ArrayList<>();
            for (String str : lore) {
                if (str == null) continue;
                this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
            }
        }

        public CompletableFuture<ItemStack> buildHead() {
            ItemStack head = new ItemStack(material);
            ItemMeta meta = head.getItemMeta();
            PaperUtil.get().setDisplayName(meta, displayName);
            PaperUtil.get().setLore(meta, lore);
            if (material != Material.PLAYER_HEAD) {
                head.setItemMeta(meta);
                return CompletableFuture.completedFuture(head);
            }
            HeadsPlus.debug("Building a head.");
            if (player != null && !player.isEmpty()) {
                HeadsPlus.debug("Setting a player skull. (" + player + ")");
                return PaperUtil.get().setProfile((SkullMeta) meta, player).thenApply(newMeta -> {
                    head.setItemMeta(newMeta);
                    return head;
                });
            } else if (texture != null) {
                HeadsPlus.debug("Setting a texture. (" + texture + ")");
                return PaperUtil.get().setProfileTexture((SkullMeta) meta, texture).thenApply(newMeta -> {
                    head.setItemMeta(newMeta);
                    return head;
                });
            }
            HeadsPlus.debug("Setting the metadata now...");
            head.setItemMeta(meta);
            return CompletableFuture.completedFuture(head);
        }

        public ItemStack forceBuildHead() {
            ItemStack head = new ItemStack(material);
            ItemMeta meta = head.getItemMeta();
            PaperUtil.get().setDisplayName(meta, displayName);
            PaperUtil.get().setLore(meta, lore);
            if (material != Material.PLAYER_HEAD) {
                head.setItemMeta(meta);
                return head;
            }
            HeadsPlus.debug("Building a head.");
            if (player != null && !player.isEmpty()) {
                HeadsPlus.debug("Setting a player skull. (" + player + ")");
                PaperUtil.get().forceSetProfile((SkullMeta) meta, player);
            } else if (texture != null) {
                HeadsPlus.debug("Setting a texture. (" + texture + ")");
                PaperUtil.get().forceSetProfileTexture((SkullMeta) meta, texture);
            }
            HeadsPlus.debug("Setting the metadata now...");
            head.setItemMeta(meta);
            return head;
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
