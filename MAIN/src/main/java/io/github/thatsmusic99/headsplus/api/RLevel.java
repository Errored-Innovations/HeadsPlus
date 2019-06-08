package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import org.bukkit.entity.Player;

public interface RLevel extends Level {

    HPChallengeRewardTypes getRewardType();

    boolean isrEnabled();

    int getRewardItemAmount();

    Object getRewardValue();

    String getSender();

    void reward(Player p);
}
