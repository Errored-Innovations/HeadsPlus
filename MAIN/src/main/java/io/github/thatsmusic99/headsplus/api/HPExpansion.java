package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HPExpansion extends PlaceholderExpansion {

    private HeadsPlus hp;

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
        hp = (HeadsPlus) Bukkit.getPluginManager().getPlugin("HeadsPlus");

        if(hp == null){
            return false;
        }
        return PlaceholderAPI.registerPlaceholderHook(getIdentifier(), this);
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
            try {
                return String.valueOf(hp.getAPI().getPlayerInLeaderboards(player, getFixedString(identifier, true), "hunting"));
            } catch (SQLException e) {
                e.printStackTrace();
                return "0";
            }
        }

        if (identifier.startsWith("crafting")) {
            try {
                return String.valueOf(HeadsPlus.getInstance().getAPI().getPlayerInLeaderboards(player, getFixedString(identifier, true), "crafting"));
            } catch (SQLException e) {
                e.printStackTrace();
                return "0";
            }
        }

        if (identifier.startsWith("selling")) {
            try {
                return String.valueOf(HeadsPlus.getInstance().getAPI().getPlayerInLeaderboards(player, getFixedString(identifier, true), "selling"));
            } catch (SQLException e) {
                e.printStackTrace();
                return "0";
            }
        }

        if (identifier.startsWith("top")) {
            // Format:
            // %headsplus_top_CATEGORY_ENTITY_NUMBER_player%
            // %headsplus_top_CATEGORY_ENTITY_NUMBER_score%
            String[] args = identifier.split("_");
            String category = args[1];
            String entity = getFixedString(args[2], false);
            int position = Integer.valueOf(args[3]);
            String option = args[4];
            try {
                List<OfflinePlayer> players = new ArrayList<>(HeadsPlus.getInstance().getMySQLAPI().getScores(entity, category).keySet());
                Iterator<OfflinePlayer> playerIterator = players.iterator();
                List<Integer> scores = new ArrayList<>(HeadsPlus.getInstance().getMySQLAPI().getScores(entity, category).values());
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
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                return "0";
            }
            return "0";


        }
        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }

    private String getFixedString(String identifier, boolean split) {
        String section = identifier;
        if (split) {
            section = identifier.split("_")[1];
        }

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
                section = "total";
                break;
            default:
                section = section.toUpperCase();
                break;
        }
        return  section;
    }
}
