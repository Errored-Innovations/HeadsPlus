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

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MaskManager {

    private final HashMap<String, MaskInfo> masks = new HashMap<>();
    private static MaskManager instance;
    private final HashMap<String, BukkitRunnable> runningTasks = new HashMap<>();

    public MaskManager() {
        instance = this;
        init();
    }

    public static MaskManager get() {
        return instance;
    }

    public void reload() {
        reset();
        init();
    }

    public void resetMask(Player player) {
        if (!runningTasks.containsKey(player.getUniqueId().toString())) return;
        runningTasks.get(player.getUniqueId().toString()).cancel();
    }

    public void reset() {
        masks.clear();
        runningTasks.values().forEach(BukkitRunnable::cancel);
        runningTasks.clear();
    }

    public void registerMask(String key, MaskInfo headInfo) {
        masks.put(key, headInfo);
    }

    public MaskInfo getMaskInfo(String key) {
        if (key.startsWith("HPM#")) {
            key = key.substring(4);
        }
        return (MaskInfo) masks.getOrDefault(key, new MaskInfo()).clone();
    }

    public boolean isMaskRegistered(String key) {
        if (key.startsWith("HPM#")) {
            key = key.substring(4);
        }
        return masks.containsKey(key);
    }

    public Set<String> getMaskKeys() {
        return masks.keySet();
    }

    public void init() {
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

    public static class MaskInfo extends HeadManager.HeadInfo {

        protected String id;

        public MaskInfo() {
            super();
        }

        public MaskInfo(HeadManager.HeadInfo info, String id) {
            super();
            this.id = id;
            withDisplayName(info.getDisplayName());
            setLore(info.getLore());
            withTexture(info.getPlayer() == null ? info.getTexture() : info.getPlayer());
            withMaterial(info.getMaterial());
        }

        public void run(Player player) {
        }

        @Override
        public CompletableFuture<ItemStack> buildHead() {
            return super.buildHead().thenApply(item -> {
                if (id == null) return item;
                PersistenceManager.get().setMaskType(item, id);
                HeadsPlus.debug("Implemented mask type " + id);
                return item;
            });
        }

        @Override
        public ItemStack forceBuildHead() {
            ItemStack item = super.forceBuildHead();
            if (id == null) return item;
            PersistenceManager.get().setMaskType(item, id);
            HeadsPlus.debug("Implemented mask type " + id);
            return item;
        }
    }

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
        public void run(Player player) {
            BukkitRunnable runnable = new BukkitRunnable() {

                private int intervals = MainConfig.get().getMasks().RESET_INTERVAL - 1;

                @Override
                public void run() {
                    if (player == null || !player.isOnline() || !MainConfig.get().getMainFeatures().MASKS) {
                        cancel();
                        return;
                    }
                    // Get the helmet
                    ItemStack helmet = player.getInventory().getHelmet();
                    if (helmet == null || !PersistenceManager.get().getMaskType(helmet).equals(id)) {
                        cancel();
                        return;
                    }
                    if (HeadsPlus.get().canUseWG() && !FlagHandler.canUseMasks(player)) {
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
