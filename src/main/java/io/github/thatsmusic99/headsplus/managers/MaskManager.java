package io.github.thatsmusic99.headsplus.managers;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MaskManager {

    private final HashMap<String, MaskInfo> masks = new HashMap<>();
    private static MaskManager instance;

    public MaskManager() {
        instance = this;
    }

    public static MaskManager get() {
        return instance;
    }

    public void reset() {
        masks.clear();
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

    public static class PotionMask extends MaskInfo {

        private List<PotionEffect> effects;

        public PotionMask() {
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

        }
    }
}
