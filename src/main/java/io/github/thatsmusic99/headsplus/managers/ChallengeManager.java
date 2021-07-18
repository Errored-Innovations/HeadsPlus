package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.ChallengeSection;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ChallengeManager {

    private HashMap<String, Challenge> challenges = new HashMap<>();
    private HashMap<String, ChallengeSection> sections = new LinkedHashMap<>();

    public void reload() {
        challenges.clear();
        sections.clear();
        init();
    }

    public void init() {
        ConfigChallenges challenges = ConfigChallenges.get();

    }
}
