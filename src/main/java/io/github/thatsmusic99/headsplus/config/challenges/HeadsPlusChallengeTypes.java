package io.github.thatsmusic99.headsplus.config.challenges;

public enum HeadsPlusChallengeTypes {

    SELLHEAD("headsplussh"),
    LEADERBOARD("headspluslb"),
    CRAFTING("headspluscraft"),
    MISC("");

    private String database;

    HeadsPlusChallengeTypes(String d) {
        database = d;
    }

    public String getDatabase() {
        return database;
    }
}
