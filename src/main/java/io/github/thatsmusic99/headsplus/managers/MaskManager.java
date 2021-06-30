package io.github.thatsmusic99.headsplus.managers;

import java.util.HashMap;

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

        public MaskInfo() {
            super();
        }

        public MaskInfo(HeadManager.HeadInfo info) {
            withDisplayName(info.getDisplayName());
            setLore(info.getLore());
            withTexture(info.getPlayer() == null ? info.getTexture() : info.getPlayer());
            withMaterial(info.getMaterial());
        }
    }
}
