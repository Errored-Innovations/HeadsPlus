package io.github.thatsmusic99.headsplus.profiles;

import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public interface ProfileUtil {

    UUID getUUID(Skull skull);

    String getName(Skull skull);

    String getB64Texture(SkullMeta meta);

    void setProperties(UUID uuid, String name);


}
