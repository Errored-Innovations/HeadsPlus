package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.util.LeaderboardsCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.WordUtils;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class HPExpansion extends PlaceholderExpansion {

    private final HeadsPlus hp;
    private boolean warned = false;

    public HPExpansion(HeadsPlus headsPlus) {
        hp = headsPlus;
    }
    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "headsplus";
    }

    @Override
    public String getAuthor() {
        return "Thatsmusic99";
    }

    @Override
    public String getVersion() {
        return hp.getVersion();
    }

    @Override
    public boolean register(){
        if(!canRegister()){
            return false;
        }
        return super.register();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        HPPlayer pl = HPPlayer.getHPPlayer(player);
        if (pl == null) {
            pl = new HPPlayer(player);
        }
        // %example_placeholder1%
        if(identifier.equals("xp")){
            return String.valueOf(pl.getXp());
        }

        // %example_placeholder2%
        if(identifier.equals("completed_challenges_total")){
            return String.valueOf(pl.getCompleteChallenges().size());
        }

        if (identifier.equals("level")) {
            return pl.getLevel().getDisplayName();
        }

        if (identifier.startsWith("hunting")) {
            return String.valueOf(HeadsPlusAPI.getPlayerInLeaderboards(player, getFixedString(identifier, true), "hunting"));
        }

        if (identifier.startsWith("crafting")) {
            return String.valueOf(HeadsPlusAPI.getPlayerInLeaderboards(player, getFixedString(identifier, true), "crafting"));
        }

        if (identifier.startsWith("selling")) {
            return String.valueOf(HeadsPlusAPI.getPlayerInLeaderboards(player, getFixedString(identifier, true), "selling"));
        }

        if (identifier.startsWith("top")) {
            // Format:
            // %headsplus_top_CATEGORY_ENTITY_NUMBER_player%
            // %headsplus_top_CATEGORY_ENTITY_NUMBER_score%
            String[] args = identifier.split("_");
            int length = args.length;
            String category = args[1].toLowerCase();
            StringBuilder entitySB = new StringBuilder();
            for (int i = 2; i < length - 2; i++) {
                entitySB.append(args[i]).append("_");
            }
            entitySB.setLength(entitySB.length() - 1);
            String entity = getFixedString(entitySB.toString(), false);
            int position = Integer.parseInt(args[length - 2]);
            String option = args[length - 1];
            try {
                LinkedHashMap<OfflinePlayer, Integer> list;
                if (MainConfig.get().getLeaderboards().CACHE_LEADERBOARDS) {
                    list = LeaderboardsCache.getType(entity, category, false, false);
                    if (list == null) {
                        list = LeaderboardsCache.getType(entity, category, true, true);
                    }
                } else {
                    list = LeaderboardsCache.getType(entity, category, true, true);
                }
                if (list == null) {
                    return "0";
                }
                List<OfflinePlayer> players = new ArrayList<>(list.keySet());
                Iterator<OfflinePlayer> playerIterator = players.iterator();
                List<Integer> scores = new ArrayList<>(list.values());
                while (playerIterator.hasNext()) {
                    OfflinePlayer p = playerIterator.next();
                    if (p.getName() == null || p.getName().equalsIgnoreCase("null")) {
                        scores.remove(players.indexOf(p));
                        playerIterator.remove();
                    }
                }
                if (option.equalsIgnoreCase("score")) {
                    return String.valueOf(scores.get(position));
                } else {
                    return players.get(position).getName();
                }
            } catch (IndexOutOfBoundsException e) {
                return "0";
            }


        }

        if (identifier.startsWith("challenge")) {
            // Format: %headsplus_CHALLENGE_header%
            // %headsplus_CHALLENGE_name%
            // %headsplus_CHALLENGE_description%
            // %headsplus_CHALLENGE_min-heads%
            // %headsplus_CHALLENGE_progress%
            // %headsplus_CHALLENGE_difficulty%
            // %headsplus_CHALLENGE_type%
            // %headsplus_CHALLENGE_reward%
            // %headsplus_CHALLENGE_completed%
            String[] args = identifier.split("_");
            if (args.length == 1) return null;
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length - 1; i++) {
                name.append(args[i]);
            }
            Challenge challenge = hp.getChallengeByName(name.toString());
            if (challenge == null) return null;
            switch (args[args.length - 1].toLowerCase()) {
                case "header":
                    return challenge.getChallengeHeader();
                case "name":
                    return challenge.getMainName();
                case "description":
                    StringBuilder desc = new StringBuilder();
                    for (String s : challenge.getDescription()) {
                        desc.append(s);
                    }
                    return desc.toString();
                case "min-heads":
                    return String.valueOf(challenge.getRequiredHeadAmount());
                case "progress":
                    return String.valueOf(HeadsPlusAPI.getPlayerInLeaderboards(player,
                                challenge.getHeadType(),
                                challenge.getChallengeType().getDatabase()));
                case "difficulty":
                    return String.valueOf(challenge.getDifficulty());
                case "type":
                    return challenge.getHeadType();
                case "reward":
                    if (challenge.getReward().getRewardString() != null) {
                        return challenge.getReward().getRewardString();
                    } else {
                        String reward = "";
                        HPChallengeRewardTypes type = challenge.getRewardType();
                        HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();
                        String value = challenge.getRewardValue().toString();
                        if (type == HPChallengeRewardTypes.ECO) {
                            reward = hpc.getString("inventory.icon.reward.currency").replace("{amount}", value);
                        } else if (type == HPChallengeRewardTypes.GIVE_ITEM) {
                            reward = hpc.getString("inventory.icon.reward.item-give")
                                    .replace("{amount}", String.valueOf(challenge.getRewardItemAmount()))
                                    .replace("{item}", WordUtils.capitalize(value.toLowerCase().replaceAll("_", " ")));
                        } else if (type == HPChallengeRewardTypes.ADD_GROUP) {
                            reward = hpc.getString("inventory.icon.reward.group-add").replace("{group}", value);
                        } else if (type == HPChallengeRewardTypes.REMOVE_GROUP) {
                            reward = hpc.getString("inventory.icon.reward.group-remove").replace("{group}", value);
                        }
                        return reward;
                    }
                case "completed":
                    return challenge.isComplete(player.getPlayer()) ? HeadsPlusMessagesManager.get().getString("command.challenges.challenge-completed", player.getPlayer()) : "";
                case "xp":
                    return String.valueOf(challenge.getGainedXP());
            }
        }
        return null;
    }

    private String getFixedString(String identifier, boolean split) {
        String section = identifier;
        if (split) {
            section = identifier.split("_")[1];
        }
        boolean legacy = true;
        switch (section) {
            case "cavespider":
                section = "CAVE_SPIDER";
                break;
            case "irongolem":
                section = "IRON_GOLEM";
                break;
            case "mushroomcow":
                section = "MUSHROOM_COW";
                break;
            case "enderdragon":
                section = "ENDER_DRAGON";
                break;
            case "elderguardian":
                section = "ELDER_GUARDIAN";
                break;
            case "magmacube":
                section = "MAGMA_CUBE";
                break;
            case "pigzombie":
                section = "PIG_ZOMBIE";
                break;
            case "polarbear":
                section = "POLAR_BEAR";

                break;
            case "skeletonhorse":
                section = "SKELETON_HORSE";
                break;
            case "traderllama":
                section = "TRADER_LLAMA";
                break;
            case "tropicalfish":
                section = "TROPICAL_FISH";
                break;
            case "wanderingtrader":
                section = "WANDERING_TRADER";
                break;
            case "witherskeleton":
                section = "WITHER_SKELETON";
                break;
            case "zombiehorse":
                section = "ZOMBIE_HORSE";
                break;
            case "zombievillager":
                section = "ZOMBIE_VILLAGER";
                break;
            case "total":
                legacy = false;
                section = "total";
                break;
            default:
                legacy = false;
                section = section.toUpperCase();
                break;
        }
        if (legacy && !warned) {
            hp.getLogger().warning("We've noticed you're using a deprecated format for your placeholders. You must now use " + section + " in your placeholder rather than " + identifier + "! (e.g. %headsplus_top_hunting_IRON_GOLEM_0_player%)");
            warned = true;
        }
        return  section;
    }
}
