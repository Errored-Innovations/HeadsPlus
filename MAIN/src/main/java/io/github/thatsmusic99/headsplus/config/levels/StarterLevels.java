package io.github.thatsmusic99.headsplus.config.levels;

import io.github.thatsmusic99.headsplus.api.Level;

public class StarterLevels {

    public static class Grass implements Level {

        @Override
        public String getConfigName() {
            return "grass";
        }

        @Override
        public String getDisplayName() {
            return "&2&lGrass";
        }

        @Override
        public int getRequiredXP() {
            return 0;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class Dirt implements Level {

        @Override
        public String getConfigName() {
            return "dirt";
        }

        @Override
        public String getDisplayName() {
            return "&6&lDirt";
        }

        @Override
        public int getRequiredXP() {
            return 250;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class Stone implements Level {

        @Override
        public String getConfigName() {
            return "stone";
        }

        @Override
        public String getDisplayName() {
            return "&7&lStone";
        }

        @Override
        public int getRequiredXP() {
            return 750;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }
}
