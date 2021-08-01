package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMasks;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MaskManager {

    private final HashMap<String, MaskInfo> masks = new HashMap<>();
    private static MaskManager instance;
    private final HashSet<BukkitRunnable> runningTasks = new HashSet<>();

    public MaskManager() {
        instance = this;
    }

    public static MaskManager get() {
        return instance;
    }

    public void reload() {
        reset();
        init();
    }

    public void reset() {
        masks.clear();
        runningTasks.forEach(BukkitRunnable::cancel);
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
                MaskInfo info;
                String headInfoStr = Objects.requireNonNull(maskSection.getString("idle"), "No idle texture for " + key + " found!");
                HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(headInfoStr);
                String type = Objects.requireNonNull(maskSection.getString("type"), "No mask type for " + key + " found!");
                switch (type.toLowerCase()) {
                    case "potion": // TODO effects
                        info = new PotionMask(key, headInfo);
                        break;
                    default:
                        throw new IllegalArgumentException("Mask type " + type + " for " + key + " does not exist!");
                }

                registerMask(key, info);
            } catch (NullPointerException | IllegalArgumentException ex) {
                HeadsPlus.get().getLogger().warning(ex.getMessage());
            }
        }
    }

    public static class MaskInfo extends HeadManager.HeadInfo {

        protected Runnable runnable;

        public MaskInfo() {
            super();
        }

        public MaskInfo(HeadManager.HeadInfo info) {
            super();
            withDisplayName(info.getDisplayName());
            setLore(info.getLore());
            withTexture(info.getPlayer() == null ? info.getTexture() : info.getPlayer());
            withMaterial(info.getMaterial());
        }

        // TODO - should probably be abstract
        public void run(Player player) {
        }
    }

    public class PotionMask extends MaskInfo {

        private List<PotionEffect> effects;
        private String id;

        public PotionMask(String id, HeadManager.HeadInfo info) {
            super(info);
            this.effects = new ArrayList<>();
	        this.id = id;
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
                @Override
                public void run() {
                    if (player == null || !player.isOnline()) {
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

                    for (PotionEffect effect : effects) {
                        effect.apply(player);
                    }
                }

                @Override
                public void cancel() {
                    super.cancel();
                    runningTasks.remove(this);
                }
            };
            runnable.runTaskTimer(HeadsPlus.get(), 1, MainConfig.get().getMasks().CHECK_INTERVAL);
            runningTasks.add(runnable);
        }
    }
}
