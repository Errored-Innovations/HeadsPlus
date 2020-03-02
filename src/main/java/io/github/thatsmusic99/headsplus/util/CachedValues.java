package io.github.thatsmusic99.headsplus.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class CachedValues {

    public static final Pattern MATCH_PAGE = Pattern.compile("^[0-9]+$");
    public static final Pattern PLAYER_NAME = Pattern.compile("^([a-zA-Z_0-9]){3,16}$");
    public static final Pattern DOUBLE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");

    public static Double getPrice(String path, FileConfiguration config) {
        String value = config.getString(path);
        assert value != null;
        return CachedValues.DOUBLE_PATTERN.matcher(value).matches() ? Double.parseDouble(value) : null;
    }
}
