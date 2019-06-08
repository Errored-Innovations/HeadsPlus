package io.github.thatsmusic99.headsplus.locale;

public interface Locale {

    boolean active(); // Will be set myself.

    String getLanguage(); // Returns the language (English, Deutsch, etc)

    String getReloadingMessage(); // Get the config reloading message.
    // English: "Reloading config..."

    String getReloadMessage(); // Get the reloaded config message.
    // This is different to reloading as it's saying the reload was successful.
    // English: "Config has reloaded!"

    String getHeadInteractMessage(); // Returns a message when a player interacts with a head.
    // English: "That is <Player>'s head!"
    // Parameters:
    // %p - The player's head being clicked.

    String getHeadMhfInteractMessage(); // Returns a message when a player interacts with a head (object).
    // English: "That is a <Object>'s head!"
    // Parameters:
    // %p - The object being clicked.

    String getHeadMhfInteractMessage2(); // Returns a message when a player interacts with a head (object).
    // NOTE: This comes up because of how English works. If needed, it can be the same as the above message.
    // English: "That is an <Object>'s head!"
    // Parameters:
    // %p - The object being clicked.

    String getSellSuccess(); // Returns a message when a player successfully sells their head.
    // English: "You successfully sold the head(s) for %l and now have %b!"
    // Parameters: &l - Total price head(s) were sold for.
    // %b - The total balance of a player.

    String getNotEnoughHeads(); // Returns a message if a player tries to sell more heads than they have.
    // English: "You don't have enough heads!"

    String getNoHeads(); // Returns a message if a player doesn't have any actual heads that can be sold in their inventory.
    // English: "You don't have any valid heads in your inventory!"

    String getInvalidArguments(); // Returns a message when invalid arguments are provided in a command.
    // English: "Invalid arguments!"

    String getFalseHead(); // Returns a message when a head can't be sold due to being invalid.
    // English: "&cThis head cannot be sold!"

    String getFalseItem(); // Returns a message when an item that isn't a skull is found.
    // English: "This is not a head!"

    String getBlacklistHead(); // Returns a message when a head is blacklisted and can't be used.
    // English: "That head is blacklisted and can not be used!"

    String getWhitelistHead();

    String getFullInventory();

    String getAlphaNames();

    String getTooManyArguments();

    String getHeadTooLong();

    String getHeadTooShort();

    String getInvalidPageNumber();

    String getInvalidInputInteger();

    String getNoPermissions();

    String getHeadAlreadyAdded();

    String getHeadAddedBlacklist();

    String getHeadNotOnBlacklist();

    String getHeadRemovedBlacklist();

    String getHeadAddedWhitelist();

    String getHeadNotOnWhitelist();

    String getHeadRemovedWhitelist();

    String getWorldAlreadyAdded();

    String getWorldAddedBlacklist();

    String getWorldNotOnBlacklist();

    String getWorldRemovedBlacklist();

    String getWorldAddedWhitelist();

    String getWorldNotOnWhitelist();

    String getWorldRemovedWhitelist();

    String getBlacklistOn();

    String getBlacklistAlreadyOn();

    String getBlacklistOff();

    String getBlacklistAlreadyOff();

    String getWBlacklistOn();

    String getWBlacklistAlreadyOn();

    String getWBlacklistOff();

    String getWBlacklistAlreadyOff();

    String getWhitelistOn();

    String getWhitelistAlreadyOn();

    String getWhitelistOff();

    String getWhitelistAlreadyOff();

    String getWWhitelistOn();

    String getWWhitelistAlreadyOn();

    String getWWhitelistOff();

    String getWWhitelistAlreadyOff();

    String getDisabledCommand();

    String getEmptyBlacklist();

    String getEmptyWBlacklist();

    String getEmptyWhitelist();

    String getEmptyWWhitelist();

    String getBuySuccess();

    String descBlacklistAdd();

    String descBlacklistDelete();

    String descBlacklistList();

    String descBlacklistToggle();

    String descBlacklistwAdd();

    String descBlacklistwDelete();

    String descBlacklistwList();

    String descBlacklistwToggle();

    String descInfo();

    String descMCReload();

    String descWhitelistAdd();

    String descWhitelistDelete();

    String descWhitelistList();

    String descWhitelistToggle();

    String descWhitelistwAdd();

    String descWhitelistwDelete();

    String descWhitelistwList();

    String descWhitelistwToggle();

    String descHead();

    String descSellhead();

    String descHeads();

    String descMyHead();

    String descHPLeaderboards();

    String getUpdateFound();

    String getCurrentVersion();

    String getNewVersion();

    String getDescription();

    String getChristmasDeniedMessage();

    String getBlockPlaceDenied();

    String getNoDataRecorded();

    String getPlayerOffline();

    String easy();

    String medium();

    String hard();

    String getReward();

    String chCompleteMessage();

    String descProfile();

    String noData();

    String cantCompleteChallenge();

    String alreadyCompleted();

    String cantViewData();

    String descHelpMenu();

    String descChallenges();

    String getAchievedNextLevel();

    String getPluginUpToDate();

    String getEnabled();

    String getErrorEnabled();

    String getDisabled();

    String badTheme();

    String getCommandFail();

    String noVault();

    String descDebug();

    String noVaultGroup();

    String noNameData();

    String noLoreData();

    String noMaskData();

    String descHeadView();

    String setValue();

    String addedValue();

    String removedValue();

    String getNotEnoughMoney();

    String inputChat();

    String completed();

    String lostMoney();

    String descConjure();

}