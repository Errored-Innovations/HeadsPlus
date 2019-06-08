package io.github.thatsmusic99.headsplus.config.levels;

import io.github.thatsmusic99.headsplus.api.Level;

public class MidLevels {

    public static class Lapis implements Level {

        @Override
        public String getConfigName() {
            return "lapis";
        }

        @Override
        public String getDisplayName() {
            return "&9&lLapis Lazuli";
        }

        @Override
        public int getRequiredXP() {
            return 7000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class LapisII implements Level {

        @Override
        public String getConfigName() {
            return "lapis_2";
        }

        @Override
        public String getDisplayName() {
            return "&9&lLapis Lazuli &1&lII";
        }

        @Override
        public int getRequiredXP() {
            return 8000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class LapisIII implements Level {

        @Override
        public String getConfigName() {
            return "lapis_3";
        }

        @Override
        public String getDisplayName() {
            return "&9&lLapis Lazuli &1&lIII";
        }

        @Override
        public int getRequiredXP() {
            return 9000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class Gold implements Level {

        @Override
        public String getConfigName() {
            return "gold";
        }

        @Override
        public String getDisplayName() {
            return "&e&lGold";
        }

        @Override
        public int getRequiredXP() {
            return 10000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class GoldII implements Level {

        @Override
        public String getConfigName() {
            return "gold_2";
        }

        @Override
        public String getDisplayName() {
            return "&e&lGold &6&lII";
        }

        @Override
        public int getRequiredXP() {
            return 12500;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class GoldIII implements Level {

        @Override
        public String getConfigName() {
            return "gold_3";
        }

        @Override
        public String getDisplayName() {
            return "&e&lGold &6&lIII";
        }

        @Override
        public int getRequiredXP() {
            return 15000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class Diamond implements Level {

        @Override
        public String getConfigName() {
            return "diamond";
        }

        @Override
        public String getDisplayName() {
            return "&b&lDiamond";
        }

        @Override
        public int getRequiredXP() {
            return 17000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class DiamondII implements Level {

        @Override
        public String getConfigName() {
            return "diamond_2";
        }

        @Override
        public String getDisplayName() {
            return "&b&lDiamond &3&lII";
        }

        @Override
        public int getRequiredXP() {
            return 20000;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }

    public static class DiamondIII implements Level {

        @Override
        public String getConfigName() {
            return "diamond_3";
        }

        @Override
        public String getDisplayName() {
            return "&b&lDiamond &3&lIII";
        }

        @Override
        public int getRequiredXP() {
            return 22500;
        }

        @Override
        public double getAddedVersion() {
            return 0.0;
        }
    }
}
