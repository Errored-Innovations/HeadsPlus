package io.github.thatsmusic99.headsplus.placeholders;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.ChallengeManager;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HPExpansion extends PlaceholderExpansion {

    private final HeadsPlus hp;
    // regex hell 2.0
    private final Pattern TOP_PLACEHOLDER_PATTERN = Pattern.compile("\btop_([A-Z]+)_?([a-zA-Z0-9=,_#]+)?_+(\\d+)_(player|score)\b");
    private final Pattern STATISTIC_PATTERN = Pattern.compile("\b(HUNTING|CRAFTING)_?([a-zA-Z0-9=,_#]+)?_+");

    public HPExpansion(HeadsPlus headsPlus) {
        hp = headsPlus;
    }
    @Override
    public boolean canRegister(){
        return true;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "headsplus";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Thatsmusic99";
    }

    @NotNull
    @Override
    public String getVersion() {
        return hp.getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){

        if (identifier.equals("xp")) {
            return String.valueOf(CacheManager.get().getXP(player));
        }

        if (identifier.equals("remaining_xp")) {
            String level = CacheManager.get().getLevel(player);
            if (level == null) return "-1";
            Level levelObj = LevelsManager.get().getLevel(level);
            long xp = CacheManager.get().getXP(player);
            if (xp == -1) return "-1";
            return String.valueOf(levelObj.getRequiredXP() - xp);
        }

        if (identifier.equals("level")) {
            return CacheManager.get().getLevel(player);
        }

        if (identifier.equals("completed_challenges_total")) {
            return String.valueOf(CacheManager.get().getTotalChallengesComplete(player));
        }

        if (identifier.startsWith("top")) {
            // %headsplus_top_HUNTING_0_player%
            // %headsplus_top_HUNTING_entity=IRON_GOLEM_0_player%
            // %headsplus_top_HUNTING_HP#iron_golem_0_player%
            // %headsplus_top_HUNTING_HP#iron_golem,entity=IRON_GOLEM_0_player%
            Matcher matcher = TOP_PLACEHOLDER_PATTERN.matcher(identifier);
            if (!matcher.matches()) return "-1";
            // Get the category
            String categoryStr = matcher.group(1);
            StatisticsSQLManager.CollectionType category = StatisticsSQLManager.CollectionType.getType(categoryStr);
            if (category == null) return "-1";
            // Get the extra metadata
            String[] metadata = matcher.group(2).split(",");
            List<String> actualMetadata = new ArrayList<>();
            String head = null;
            for (String str : metadata) {
                if (str.startsWith("HP#")) head = str;
                else actualMetadata.add(str);
            }
            String metadataStr = String.join(",", actualMetadata);

            // Get the position
            int position = Integer.parseInt(matcher.group(3));

            // Get the actual records
            List<StatisticsSQLManager.LeaderboardEntry> entries;
            if (head == null) {
                if (metadataStr.isEmpty()) {
                    entries = CacheManager.get().getEntries(category);
                } else {
                    entries = CacheManager.get().getEntriesMeta(category, metadataStr);
                }
            } else {
                if (metadataStr.isEmpty()) {
                    entries = CacheManager.get().getEntries(category, head);
                } else {
                    entries = CacheManager.get().getEntries(category, head, metadataStr);
                }
            }

            if (position >= entries.size()) return "-1";
            StatisticsSQLManager.LeaderboardEntry entry = entries.get(position);
            switch (matcher.group(4)) {
                case "player":
                    return entry.getPlayer();
                case "score":
                    return String.valueOf(entry.getSum());
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
            Challenge challenge = ChallengeManager.get().getChallengeByName(name.toString());
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
                    return String.valueOf(CacheManager.get().getStat(challenge.getCacheID(), challenge.getStatFuture(player.getUniqueId())));
                case "difficulty":
                    return String.valueOf(challenge.getDifficulty());
                case "type":
                    return challenge.getHeadType();
                case "reward":
                    if (player.getPlayer() == null) return null;
                    return challenge.getReward().getRewardString(player.getPlayer());
                case "completed":
                    return challenge.isComplete(player.getPlayer()) ? MessagesManager.get().getString("command.challenges.challenge-completed", player) : "";
                case "xp":
                    return String.valueOf(challenge.getGainedXP());
            }
        }

        Matcher matcher = STATISTIC_PATTERN.matcher(identifier);
        if (!matcher.matches()) return null;
        // Get the category
        String categoryStr = matcher.group(1);
        StatisticsSQLManager.CollectionType category = StatisticsSQLManager.CollectionType.getType(categoryStr);
        if (category == null) return "-1";
        // Get the extra metadata
        String[] metadata = matcher.group(2).split(",");
        List<String> actualMetadata = new ArrayList<>();
        String head = null;
        for (String str : metadata) {
            if (str.startsWith("HP#")) head = str;
            else actualMetadata.add(str);
        }
        String metadataStr = String.join(",", actualMetadata);
        if (head == null) {
            if (metadataStr.isEmpty()) {
                return String.valueOf(CacheManager.get().getStat(player, category));
            } else {
                return String.valueOf(CacheManager.get().getStatMeta(player, category, metadataStr));
            }
        } else {
            if (metadataStr.isEmpty()) {
                return String.valueOf(CacheManager.get().getStat(player, category, head));
            } else {
                return String.valueOf(CacheManager.get().getStat(player, category, head, metadataStr));
            }
        }

    }

}
