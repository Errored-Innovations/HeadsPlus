package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class HPConfig extends ConfigFile {

    public HPConfig(@NotNull String name) throws IOException, IllegalAccessException {
        super(getOrCreateFile(name));
    }

    public void load() {
        loadDefaults();
        moveToNew();
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        postSave();
        if (this instanceof FeatureConfig) {
            ((FeatureConfig) this).setLoaded(true);
        }
    }

    public abstract void loadDefaults();

    public void moveToNew() {
    }

    public void postSave() {
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        moveToNew();
        save();
        postSave();
    }

    protected static File getOrCreateFile(String name) {
        File file = new File(HeadsPlus.get().getDataFolder(), name);
        try {
            if (!file.exists()) file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
