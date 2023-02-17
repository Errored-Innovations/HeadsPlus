package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMasks;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The manager that takes care of masks. Masks are heads that when worn, give players certain effects. This manager
 * allows external plugins to access and register their own masks.
 *
 * @author Thatsmusic99 (Holly)
 */
public class MaskManager {

    private final HashMap<String, MaskInfo> masks = new HashMap<>();
    private static MaskManager instance;
    private final HashMap<String, BukkitRunnable> runningTasks = new HashMap<>();

    private MaskManager(boolean empty) {
        instance = this;
        if (!empty) init();
    }

    /**
     * Initiates the mask manager. Not intended for external use.
     */
    public MaskManager() {
        this(false);
    }

    /**
     * Retrieves an instance of the Mask Manager. If an instance does not already exist, one will be created.
     *
     * @return the existing instance of the mask manager.
     */
    @NotNull
    public static MaskManager get() {
        if (instance == null) instance = new MaskManager(true);
        return instance;
    }

    /**
     * The method used to re-register masks in the configuration file. Any head added via an API will be lost.
     */
    public void reload() {
        reset();
        init();
    }

    /**
     * Removes the mask effects from a specific player.
     *
     * @param player the player to have the mask effects reset.
     */
    public void resetMask(@NotNull Player player) {
        Objects.requireNonNull(player, "The player specified is null!");

        // If the player does not have an active mask, stop there.
        if (!runningTasks.containsKey(player.getUniqueId().toString())) return;

        // Cancel the runnable controlling the masks.
        runningTasks.get(player.getUniqueId().toString()).cancel();
    }

    /**
     * Clears all registered masks and stops all running ones.
     */
    public void reset() {
        masks.clear();
        runningTasks.values().forEach(BukkitRunnable::cancel);
        runningTasks.clear();
    }

    /**
     * Registers a mask with an ID and its mask data.
     *
     * @param key The ID of the mask to be specified. It does not require the HPM# prefix.
     * @param maskInfo The mask data in question.
     * @throws IllegalStateException if the mask with the specific ID has already been registered.
     */
    public void registerMask(@NotNull String key, @NotNull MaskInfo maskInfo) {

        // Make sure nothing is null
        Objects.requireNonNull(key, "The key to be used is null!");
        Objects.requireNonNull(maskInfo, "The mask data is null!");

        // If the key includes HPM#, remove it.
        key = removeMaskPrefix(key);

        // If the mask has already been registered, stop it there
        if (isMaskRegistered(key)) {
            throw new IllegalStateException("The mask " + key + " has already been registered!");
        }

        // Register the mask!
        masks.put(key, maskInfo);
    }

    /**
     * Gets the mask data of a mask with a specific ID. The mask data in question is cloned, so it is used per-player.
     *
     * @param key The ID of the mask. It can contain the HPM# prefix, but HP# will potentially return null.
     * @return The mask data of the mask if it is registered. If it is not registered, then null is returned instead.
     */
    @Nullable
    public MaskInfo getMaskInfo(@NotNull String key) {

        // Make sure nothing is null
        Objects.requireNonNull(key, "The key used to search for a mask is null!");

        // Get the mask information with the specified character.
        MaskInfo info = masks.get(removeMaskPrefix(key));
        if (info == null) return null;
        return (MaskInfo) info.clone();
    }

    /**
     * Determines whether a mask with a specified ID is registered.
     *
     * @param key The key of the mask to be checked.
     * @return true if the mask is registered, false if it is not.
     */
    public boolean isMaskRegistered(String key) {
        return masks.containsKey(removeMaskPrefix(key));
    }

    /**
     * A set of all masks that have been registered. The set in question is cloned, so modifying it has no effect on the
     * actual masks map.
     *
     * @return A set of all mask IDs.
     */
    public Set<String> getMaskKeys() {
        return new HashSet<>(masks.keySet());
    }

    private void init() {
        ConfigMasks masksConfig = ConfigMasks.get();
        // Get the config section
        ConfigSection masksSection = masksConfig.getConfigSection("masks");
        if (masksSection == null) return;
        // Get each mask
        for (String key : masksSection.getKeys(false)) {
            try {
                ConfigSection maskSection = masksSection.getConfigSection(key);
                if (maskSection == null) continue;
                //
                PotionMask info;
                String headInfoStr = Objects.requireNonNull(maskSection.getString("idle"),
                        "No idle texture for " + key + " found!");
                if (!HeadManager.get().contains(headInfoStr))
                    throw new IllegalArgumentException("Head " + headInfoStr + " for " + key + "'s idle state is not " +
                            "registered!");
                HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(headInfoStr);
                String type = Objects.requireNonNull(maskSection.getString("type"), "No mask type for " + key + " " +
                        "found!");
                switch (type.toLowerCase()) {
                    case "potion":
                        info = new PotionMask(key, headInfo);
                        for (String effectStr : maskSection.getStringList("effects")) {
                            HeadsPlus.debug("Effect: " + effectStr);
                            String[] content = effectStr.split(":");
                            PotionEffectType effectType = PotionEffectType.getByName(content[0]);
                            if (effectType == null)
                                throw new IllegalArgumentException("Mask effect " + content[0] + " is not an existing" +
                                        " potion effect! (Mask: " + key + ")");
                            int amplifier = 0;
                            if (content.length > 1) {
                                HeadsPlus.debug("Selected amplifier for " + effectStr + ": " + content[1]);
                                amplifier = Integer.parseInt(content[1]) - 1;
                            }
                            PotionEffect effect = new PotionEffect(effectType,
                                    MainConfig.get().getMasks().EFFECT_LENGTH, amplifier);
                            info.addEffect(effect);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Mask type " + type + " for " + key + " does not exist!");
                }

                registerMask(key, info);
            } catch (NullPointerException | IllegalArgumentException ex) {
                HeadsPlus.get().getLogger().warning("Error received when registering mask " + key + ": " + ex.getMessage());
            }
        }

        HeadsPlus.get().getLogger().info("Registered " + masks.size() + " masks.");
    }

    private String removeMaskPrefix(String key) {
        if (key.startsWith("HPM#")) {
            key = key.substring(4);
        }
        return key;
    }

    /**
     * A base class representing masks. It inherits {@link HeadManager.HeadInfo}, so item metadata is stored there.
     * Actual mask functions are handled within this class.
     */
    public static class MaskInfo extends HeadManager.HeadInfo {

        protected String id;

        /**
         * Initialises an empty mask without an ID.
         */
        public MaskInfo() {
            super();
        }

        /**
         * Initialises a mask with existing head info, and its own ID.
         *
         * @param info The head info to be adopted by the mask.
         * @param id The ID for the head.
         */
        public MaskInfo(HeadManager.HeadInfo info, String id) {
            super();
            this.id = id;
            withDisplayName(info.getDisplayName());
            setLore(info.getLore());
            withTexture(info.getPlayer() == null ? info.getTexture() : info.getPlayer());
            withMaterial(info.getMaterial());
        }

        /**
         * Prompts the mask functions to start with a specified player.
         *
         * @param player The player using the mask.
         */
        public void run(@NotNull Player player) {
        }

        /**
         * Determines whether a specified player is still wearing the mask and is able to.
         *
         * @param player The player in question
         * @return true if the mask effects can persist, or false if the player isn't wearing the mask, or if the
         * effects should not apply.
         */
        public boolean isMaskBeingWorn(@Nullable Player player) {
            // If the player is null, offline or if masks are disabled, stop there
            if (player == null || !player.isOnline() || !MainConfig.get().getMainFeatures().MASKS) {
                return false;
            }
            // Get the helmet and ensure it is a valid mask
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet == null || !PersistenceManager.get().getMaskType(helmet).equals(id)) {
                return false;
            }
            // Make sure WorldGuard is not blocking effects
            return !HeadsPlus.get().canUseWG() || FlagHandler.canUseMasks(player);
        }

        @Override
        public CompletableFuture<ItemStack> buildHead() {
            return super.buildHead().thenApply(item -> {
                if (id == null) return item;
                if (id.startsWith("HPM#")) id = id.substring(4);
                PersistenceManager.get().setMaskType(item, id);
                HeadsPlus.debug("Implemented mask type " + id);
                return item;
            });
        }

        @Override
        public ItemStack forceBuildHead() {
            ItemStack item = super.forceBuildHead();
            if (id == null) return item;
            if (id.startsWith("HPM#")) id = id.substring(4);
            PersistenceManager.get().setMaskType(item, id);
            HeadsPlus.debug("Implemented mask type " + id);
            return item;
        }
    }

    /**
     * Represents a mask that applies potion effects to a player when worn.
     */
    public class PotionMask extends MaskInfo {

        private List<PotionEffect> effects;

        public PotionMask(String id, HeadManager.HeadInfo info) {
            super(info, id);
            this.effects = new ArrayList<>();
        }

        public PotionMask withEffects(List<PotionEffect> effects) {
            this.effects = effects;
            return this;
        }

        public PotionMask addEffect(PotionEffect effect) {
            this.effects.add(effect);
            return this;
        }

        @Override
        public void run(@NotNull Player player) {
            BukkitRunnable runnable = new BukkitRunnable() {

                private int intervals = MainConfig.get().getMasks().RESET_INTERVAL - 1;

                @Override
                public void run() {
                    // If the mask isn't being worn, stop there and cancel the mask
                    if (!isMaskBeingWorn(player)) {
                        cancel();
                        return;
                    }
                    if (++intervals != MainConfig.get().getMasks().RESET_INTERVAL) return;
                    intervals = 0;
                    for (PotionEffect effect : effects) {
                        effect.apply(player);
                    }
                }

                @Override
                public void cancel() {
                    super.cancel();
                    runningTasks.remove(player.getUniqueId().toString());
                    for (PotionEffect effect : effects) {
                        player.removePotionEffect(effect.getType());
                    }
                }
            };
            runnable.runTaskTimer(HeadsPlus.get(), 1, MainConfig.get().getMasks().CHECK_INTERVAL);
            runningTasks.put(player.getUniqueId().toString(), runnable);
        }
    }
}
