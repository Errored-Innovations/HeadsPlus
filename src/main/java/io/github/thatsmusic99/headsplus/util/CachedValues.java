package io.github.thatsmusic99.headsplus.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class CachedValues {

    public static final Pattern MATCH_PAGE = Pattern.compile("^[0-9]+$");
    public static final Pattern PLAYER_NAME = Pattern.compile("^([a-zA-Z_0-9]){1,16}$");
    public static final Pattern DOUBLE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    public static final Pattern MINECRAFT_TEXTURES_PATTERN = Pattern.compile("^(http(s)?://)?textures\\.minecraft\\" +
            ".net/texture/([0-9a-fA-F]+)$");
    public static final Pattern MINECRAFT_TEXTURES_PATTERN_LENIENT = Pattern.compile("(http(s)?://)?textures\\.minecraft\\.net/texture/([0-9a-fA-F]+)");
    public static final Pattern BASE64_PATTERN = Pattern.compile("^" +
            "(eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv)" +
            "[a-zA-Z0-9]+=*$");
    public static final Pattern CONTENT_PATTERN = Pattern.compile("(C)");

    public static Double getPrice(String path, FileConfiguration config) {
        String value = config.getString(path);
        if (value != null) {
            return CachedValues.DOUBLE_PATTERN.matcher(value).matches() ? Double.parseDouble(value) : null;
        } else {
            return null;
        }

    }
}
