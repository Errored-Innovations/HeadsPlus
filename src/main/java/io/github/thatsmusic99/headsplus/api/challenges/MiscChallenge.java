package io.github.thatsmusic99.headsplus.api.challenges;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Challenge;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MiscChallenge extends Challenge {

    public MiscChallenge(String id, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        super(id, section, icon, completeIcon);
    }

    @Override
    public boolean canComplete(Player p) {
        return true;
    }

    @Override
    public String getDatabaseType() {
        return "";
    }
}
