package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMasks;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MaskManager {

    private final HashMap<String, MaskInfo> masks = new HashMap<>();
    private static MaskManager instance;
    private final HashSet<BukkitRunnable> runningTasks = new HashSet<>();

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
                    case "potion":
                        info = new PotionMask(key, headInfo);
                        for (String effectStr : maskSection.getStringList("effects")) {
                            HeadsPlus.debug("Effect: " + effectStr);
                            String[] content = effectStr.split(":");
                            PotionEffectType effectType = PotionEffectType.getByName(content[0]);
                            if (effectType == null)
                                throw new IllegalArgumentException("Mask effect " + content[0] + " is not an existing potion effect! (Mask: " + key + ")");
                            int amplifier = 0;
                            if (content.length > 1 && CachedValues.MATCH_PAGE.matcher(content[1]).matches())
                                amplifier = Integer.getInteger(content[1]) - 1;
                            PotionEffect effect = new PotionEffect(effectType, MainConfig.get().getMasks().EFFECT_LENGTH, amplifier);
                            ((PotionMask) info).addEffect(effect);
                        }
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
                    if (++intervals != MainConfig.get().getMasks().RESET_INTERVAL) return;
                    intervals = 0;
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
