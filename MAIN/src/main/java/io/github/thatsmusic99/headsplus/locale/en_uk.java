package io.github.thatsmusic99.headsplus.locale;

public class en_uk implements Locale {

    @Override
    public boolean active() {
        return true;
    }

    @Override
    public String getLanguage() {
        return "English, UK";
    }

    @Override
    public String getReloadingMessage() {
        return "%h &3Reloading config...";
    }

    @Override
    public String getReloadMessage() {
        return "%h &3Reloaded config!";
    }

    @Override
    public String getHeadInteractMessage() {
        return "&3That is &b%p&3''s &3head!";
    }

    @Override
    public String getHeadMhfInteractMessage() {
        return "&3That is a &b%p&3''s head!";
    }

    @Override
    public String getHeadMhfInteractMessage2() {
        return "&3That is an &b%p&3''s &3head!";
    }

    @Override
    public String getSellSuccess() {
        return "&3You successfully sold the head(s) for &b%l &3and now have &b%b!";
    }

    @Override
    public String getNotEnoughHeads() {
        return "&cYou don't have enough heads!";
    }

    @Override
    public String getNoHeads() {
        return "&cYou don't have any valid heads in your inventory!";
    }

    @Override
    public String getInvalidArguments() {
        return "&cInvalid arguments!";
    }

    @Override
    public String getFalseHead() {
        return "&cThis head cannot be sold!";
    }

    @Override
    public String getFalseItem() {
        return "&cThis is not a head!";
    }

    @Override
    public String getBlacklistHead() {
        return "&cThis head is blacklisted and cannot be used!";
    }

    @Override
    public String getWhitelistHead() {
        return "&cThis head isn't whitelisted and therefore cannot be used!";
    }

    @Override
    public String getFullInventory() {
        return "&cYour inventory is full!";
    }

    @Override
    public String getAlphaNames() {
        return "&cThis command only handles alphanumeric names!";
    }

    @Override
    public String getTooManyArguments() {
        return "&cToo many arguments!";
    }

    @Override
    public String getHeadTooLong() {
        return "&cIGN is too long to be valid! Please use an IGN between 3 and 16 characters.";
    }

    @Override
    public String getHeadTooShort() {
        return "&cIGN is too short to be valid! Please use an IGN between 3 and 16 characters.";
    }

    @Override
    public String getInvalidPageNumber() {
        return "{header} &cInvalid page number!";
    }

    @Override
    public String getInvalidInputInteger() {
        return "{header} &cYou can only use integers in this command!";
    }

    @Override
    public String getNoPermissions() {
        return "&cYou do not have permission to use this command.";
    }

    @Override
    public String getHeadAlreadyAdded() {
        return "{header} &3This head is already added!";
    }

    @Override
    public String getHeadAddedBlacklist() {
        return "{header} &3%p has been added to the blacklist!";
    }

    @Override
    public String getHeadNotOnBlacklist() {
        return "{header} &3This head is not on the blacklist!";
    }

    @Override
    public String getHeadRemovedBlacklist() {
        return "{header} &3%p has been removed from the blacklist!";
    }

    @Override
    public String getHeadAddedWhitelist() {
        return "{header} &3%p has been added to the whitelist!";
    }

    @Override
    public String getHeadNotOnWhitelist() {
        return "{header} &3This head is not on the whitelist!";
    }

    @Override
    public String getHeadRemovedWhitelist() {
        return "%h &3%p has been removed from the whitelist!";
    }

    @Override
    public String getWorldAlreadyAdded() {
        return "%h &3This world is already added!";
    }

    @Override
    public String getWorldAddedBlacklist() {
        return "%h &3%w has been added to the world blacklist!";
    }

    @Override
    public String getWorldNotOnBlacklist() {
        return "%h &3This world is not on the blacklist!";
    }

    @Override
    public String getWorldRemovedBlacklist() {
        return "%h &3%w has been removed from the blacklist!";
    }

    @Override
    public String getWorldAddedWhitelist() {
        return "%h &3%w has been added to the world whitelist!";
    }

    @Override
    public String getWorldNotOnWhitelist() {
        return "%h &3This world is not on the whitelist!";
    }

    @Override
    public String getWorldRemovedWhitelist() {
        return "%h &3%w has been removed from the whitelist!";
    }

    @Override
    public String getBlacklistOn() {
        return "%h &3The blacklist has been enabled!";
    }

    @Override
    public String getBlacklistAlreadyOn() {
        return "%h &3The blacklist is already enabled!";
    }

    @Override
    public String getBlacklistOff() {
        return "%h &3The blacklist has been disabled!";
    }

    @Override
    public String getBlacklistAlreadyOff() {
        return "%h &3The blacklist is already disabled!";
    }

    @Override
    public String getWBlacklistOn() {
        return "%h &3The world blacklist has been enabled!";
    }

    @Override
    public String getWBlacklistAlreadyOn() {
        return "%h &3The world blacklist is already enabled!";
    }

    @Override
    public String getWBlacklistOff() {
        return "%h &3The world blacklist has been disabled!";
    }

    @Override
    public String getWBlacklistAlreadyOff() {
        return "%h &3The world blacklist is already disabled!";
    }

    @Override
    public String getWhitelistOn() {
        return "%h &3The whitelist has been enabled!";
    }

    @Override
    public String getWhitelistAlreadyOn() {
        return "%h &3The whitelist is already enabled!";
    }

    @Override
    public String getWhitelistOff() {
        return "%h &3The whitelist has been disabled!";
    }

    @Override
    public String getWhitelistAlreadyOff() {
        return "%h &3The whitelist is already disabled!";
    }

    @Override
    public String getWWhitelistOn() {
        return "%h &3The world whitelist has been enabled!";
    }

    @Override
    public String getWWhitelistAlreadyOn() {
        return "%h &3The world whitelist is already enabled!";
    }

    @Override
    public String getWWhitelistOff() {
        return "{header} &3The world whitelist has been disabled!";
    }

    @Override
    public String getWWhitelistAlreadyOff() {
        return "{header} &3The world whitelist is already disabled!";
    }

    @Override
    public String getDisabledCommand() {
        return "&cThis command is disabled.";
    }

    @Override
    public String getEmptyBlacklist() {
        return "{header} &cThe blacklist is empty!";
    }

    @Override
    public String getEmptyWBlacklist() {
        return "%h &cThe world blacklist is empty!";
    }

    @Override
    public String getEmptyWhitelist() {
        return "%h &cThe whitelist is empty!";
    }

    @Override
    public String getEmptyWWhitelist() {
        return "%h &cThe world whitelist is empty!";
    }

    @Override
    public String getBuySuccess() {
        return "&3You have bought a head for &b%l &3and now have &b%b!";
    }

    @Override
    public String descBlacklistAdd() {
        return "Adds a head to the blacklist.";
    }

    @Override
    public String descBlacklistDelete() {
        return "Removes a head from the blacklist.";
    }

    @Override
    public String descBlacklistList() {
        return "Lists all heads in the blacklist.";
    }

    @Override
    public String descBlacklistToggle() {
        return "Toggles the blacklist.";
    }

    @Override
    public String descBlacklistwAdd() {
        return "Adds a world to the crafting recipe blacklist.";
    }

    @Override
    public String descBlacklistwDelete() {
        return "Removes a world to the crafting recipe blacklist.";
    }

    @Override
    public String descBlacklistwList() {
        return "Lists blacklisted worlds.";
    }

    @Override
    public String descBlacklistwToggle() {
        return "Toggles the crafting recipe blacklist on/off.";
    }

    @Override
    public String descInfo() {
        return "Displays plugin information.";
    }

    @Override
    public String descMCReload() {
        return "Reloads configuration files.";
    }

    @Override
    public String descWhitelistAdd() {
        return "Adds a head to the whitelist.";
    }

    @Override
    public String descWhitelistDelete() {
        return "Removes a head from the whitelist.";
    }

    @Override
    public String descWhitelistList() {
        return "Lists all heads in the whitelist.";
    }

    @Override
    public String descWhitelistToggle() {
        return "Toggles the whitelist.";
    }

    @Override
    public String descWhitelistwAdd() {
        return "Adds a world to the crafting recipe whitelist.";
    }

    @Override
    public String descWhitelistwDelete() {
        return "Removes a world to the crafting recipe whitelist.";
    }

    @Override
    public String descWhitelistwList() {
        return "Lists whitelisted worlds.";
    }

    @Override
    public String descWhitelistwToggle() {
        return "Toggles the crafting recipe whitelist on/off.";
    }

    @Override
    public String descHead() {
        return "Spawns in a head.";
    }

    @Override
    public String descSellhead() {
        return "Sells the head(s) in your hand, use number parameter to sell a specific number, entity name to sell a specific mob's head, and all to sell every head.";
    }

    @Override
    public String descHeads() {
        return "Displays a selection of heads.";
    }

    @Override
    public String descMyHead() {
        return "Spawns in your head.";
    }

    @Override
    public String descHPLeaderboards() {
        return "Displays the heads leaderboard.";
    }

    @Override
    public String getUpdateFound() {
        return "%h &3An update has been found for HeadsPlus! Hover over the message for more information.";
    }

    @Override
    public String getCurrentVersion() {
        return "&3Current version: &7";
    }

    @Override
    public String getNewVersion() {
        return "&3New version: &7";
    }

    @Override
    public String getDescription() {
        return "&3Description: &7";
    }

    @Override
    public String getChristmasDeniedMessage() {
        return "&cIt isn't that date yet!";
    }

    @Override
    public String getBlockPlaceDenied() {
        return "&cYou can not place sellable heads!";
    }

    @Override
    public String getNoDataRecorded() {
        return "&cNo leaderboard data has been recorded yet!";
    }

    @Override
    public String getPlayerOffline() {
        return "&cThat player is offline!";
    }

    @Override
    public String easy() {
        return "Easy";
    }

    @Override
    public String medium() {
        return "Intermediate";
    }

    @Override
    public String hard() {
        return "Hard";
    }

    @Override
    public String getReward() {
        return "Reward:";
    }

    @Override
    public String chCompleteMessage() {
        return "{header} &b%p &3has completed the &b{challenge} &3challenge!";
    }

    @Override
    public String descProfile() {
        return "Displays your profile for the plugin.";
    }

    @Override
    public String noData() {
        return "&cThere is no data for this player!";
    }

    @Override
    public String cantCompleteChallenge() {
        return "&cYou can't complete this challenge!";
    }

    @Override
    public String alreadyCompleted() {
        return "&cYou've already completed that challenge!";
    }

    @Override
    public String cantViewData() {
        return "&cYou can't view your own data in console!";
    }

    @Override
    public String descHelpMenu() {
        return "Displays the help menu.";
    }

    @Override
    public String descChallenges() {
        return "Displays challenges you can complete.";
    }

    @Override
    public String getAchievedNextLevel() {
        return "{header} &3%p has reached level %lvl&3!";
    }

    @Override
    public String getPluginUpToDate() {
        return "{header} &3Plugin is up to date!";
    }

    @Override
    public String getEnabled() {
        return "{header} &3HeadsPlus has been enabled!";
    }

    @Override
    public String getErrorEnabled() {
        return "{header} &cHeadsPlus has failed to start up correctly. An error report has been made in /plugins/HeadsPlus/debug";
    }

    @Override
    public String getDisabled() {
        return "{header} &3HeadsPlus has been disabled!";
    }

    @Override
    public String badTheme() {
        return "{header} &3Faulty theme was put in! No theme changes will be made.";
    }

    @Override
    public String getCommandFail() {
        return "{header} &cFailed to run this command!";
    }

    @Override
    public String noVault() {
        return "{header} &cVault not found! Heads cannot be sold and challenge rewards can not add/remove groups.";
    }

    @Override
    public String descDebug() {
        return "Dumps a debug file.";
    }

    @Override
    public String noVaultGroup() {
        return "{header} &cVault wasn't found upon startup! Can not add group.";
    }

    @Override
    public String noNameData() {
        return "{header} &cThere is no name data for this entity!";
    }

    @Override
    public String noLoreData() {
        return "{header} &cThere is no lore data for this entity!";
    }

    @Override
    public String noMaskData() {
        return "{header} &cThere is no mask data for this entity!";
    }

    @Override
    public String descHeadView() {
        return "Displays/modifies information about a specific entity's head.";
    }

    @Override
    public String setValue() {
        return "{header} &3{entity}'s {setting} has been changed to {value}!";
    }

    @Override
    public String addedValue() {
        return "{header} &3{value} has been added to {entity}'s {setting}!";
    }

    @Override
    public String removedValue() {
        return "{header} &3{value} has been removed {entity}'s {setting}!";
    }

    @Override
    public String getNotEnoughMoney() {
        return "{header} &cYou don't have enough money!";
    }

    @Override
    public String inputChat() {
        return "&bType in a term you want to search.";
    }

    @Override
    public String completed() {
        return "&6Completed!";
    }

    @Override
    public String lostMoney() {
        return "&cBecause you got killed by {player}, you lost {price}!";
    }

    @Override
    public String descConjure() {
        return "Retreives a head of a specific type.";
    }
}
