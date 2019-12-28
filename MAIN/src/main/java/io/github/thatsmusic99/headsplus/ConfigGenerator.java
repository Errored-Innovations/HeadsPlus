package io.github.thatsmusic99.headsplus;

import io.github.thatsmusic99.headsplus.locale.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigGenerator {

    // I hate being this lazy

    public static void main(String[] args) {

        Locale loc = new ru_ru();
        File f2 = new File(".\\generated-configs\\ru_ru.yml");
        try {
            f2.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f2);
            config.addDefault("version", 1.0);
            config.addDefault("prefix", "&cHeadsPlus &8» &7");
            config.addDefault("commands.errors.cmd-fail", tr(loc.getCommandFail()));
            config.addDefault("commands.errors.disabled", tr(loc.getDisabledCommand()));
            config.addDefault("commands.errors.invalid-args", tr(loc.getInvalidArguments()));
            config.addDefault("commands.errors.invalid-input-int", tr(loc.getInvalidInputInteger()));
            config.addDefault("commands.errors.invalid-pg-no", tr(loc.getInvalidPageNumber()));
            config.addDefault("commands.errors.no-perm", tr(loc.getNoPermissions()));
            config.addDefault("commands.errors.player-offline", tr(loc.getPlayerOffline()));
            config.addDefault("commands.errors.too-many-args", tr(loc.getTooManyArguments()));
            config.addDefault("commands.addhead.head-added", "TODO");
            config.addDefault("commands.addhead.head-adding", "TODO");
            config.addDefault("commands.addhead.head-already-added", "TODO");
            config.addDefault("commands.blacklist.bl-a-off", tr(loc.getBlacklistAlreadyOff()));
            config.addDefault("commands.blacklist.bl-a-on", tr(loc.getBlacklistAlreadyOn()));
            config.addDefault("commands.blacklist.bl-off", tr(loc.getBlacklistOff()));
            config.addDefault("commands.blacklist.bl-on", tr(loc.getBlacklistOn()));
            config.addDefault("commands.blacklist.blw-a-on", tr(loc.getWBlacklistOn()));
            config.addDefault("commands.blacklist.blw-a-off", tr(loc.getWBlacklistOff()));
            config.addDefault("commands.blacklist.empty-bl", tr(loc.getEmptyBlacklist()));
            config.addDefault("commands.blacklist.empty-blw", tr(loc.getEmptyWBlacklist()));
            config.addDefault("commands.blacklist.head-a-add", tr(loc.getHeadAlreadyAdded()));
            config.addDefault("commands.blacklist.head-a-removed-bl", tr(loc.getHeadNotOnBlacklist()));
            config.addDefault("commands.blacklist.head-added-bl", tr(loc.getHeadAddedBlacklist()));
            config.addDefault("commands.blacklist.head-removed-bl", tr(loc.getHeadRemovedBlacklist()));
            config.addDefault("commands.blacklist.world-a-add", tr(loc.getWorldAlreadyAdded()));
            config.addDefault("commands.blacklist.world-added-bl", tr(loc.getWorldAddedBlacklist()));
            config.addDefault("commands.blacklist.world-a-removed-bl", tr(loc.getWorldNotOnBlacklist()));
            config.addDefault("commands.blacklist.world-removed-bl", tr(loc.getWorldRemovedBlacklist()));
            config.addDefault("commands.challenges.already-complete-challenge", tr(loc.alreadyCompleted()));
            config.addDefault("commands.challenges.cant-complete-challenge", tr(loc.cantCompleteChallenge()));
            config.addDefault("commands.challenges.challenge-complete", tr(loc.chCompleteMessage()));
            config.addDefault("commands.challenges.challenge-completed", (loc.completed()));
            config.addDefault("commands.head.alpha-names", tr(loc.getAlphaNames()));
            config.addDefault("commands.head.blacklist-head", tr(loc.getBlacklistHead()));
            config.addDefault("commands.head.full-inv", tr(loc.getFullInventory()));
            config.addDefault("commands.head.head-too-long", tr(loc.getHeadTooLong()));
            config.addDefault("commands.head.head-too-short", tr(loc.getHeadTooShort()));
            config.addDefault("commands.head.whitelist-head", tr(loc.getWhitelistHead()));
            config.addDefault("commands.head-info.add-value", tr(loc.addedValue()));
            config.addDefault("commands.head-info.no-lore-data", tr(loc.noLoreData()));
            config.addDefault("commands.head-info.no-mask-data", tr(loc.noMaskData()));
            config.addDefault("commands.head-info.no-name-data", tr(loc.noNameData()));
            config.addDefault("commands.head-info.remove-value", tr(loc.removedValue()));
            config.addDefault("commands.head-info.set-value", tr(loc.setValue()));
            config.addDefault("commands.heads.buy-success", tr(loc.getBuySuccess()));
            config.addDefault("commands.heads.chat-input", tr(loc.inputChat()));
            config.addDefault("commands.heads.not-enough-money", tr(loc.getNotEnoughMoney()));
            config.addDefault("commands.leaderboards.no-data-lb", tr(loc.getNoDataRecorded()));
            config.addDefault("commands.levels.level-up", tr(loc.getAchievedNextLevel()));
            config.addDefault("commands.profile.cant-view-data", tr(loc.cantViewData()));
            config.addDefault("commands.profile.no-data", tr(loc.noData()));
            config.addDefault("commands.reload.reload-message", tr(loc.getReloadMessage()));
            config.addDefault("commands.reload.reloading-message", tr(loc.getReloadingMessage()));
            config.addDefault("commands.sellhead.false-head", tr(loc.getFalseHead()));
            config.addDefault("commands.sellhead.false-item", tr(loc.getFalseItem()));
            config.addDefault("commands.sellhead.no-heads", tr(loc.getNoHeads()));
            config.addDefault("commands.sellhead.not-enough-heads", tr(loc.getNotEnoughHeads()));
            config.addDefault("commands.sellhead.sell-success", tr(loc.getSellSuccess()));
            config.addDefault("commands.tests.running-tests", "TODO");
            config.addDefault("commands.tests.results", "TODO");
            config.addDefault("commands.whitelist.empty-wl", tr(loc.getEmptyWhitelist()));
            config.addDefault("commands.whitelist.empty-wlw", tr(loc.getEmptyWWhitelist()));
            config.addDefault("commands.whitelist.head-a-removed-wl", tr(loc.getHeadNotOnWhitelist()));
            config.addDefault("commands.whitelist.head-added-wl", tr(loc.getHeadAddedWhitelist()));
            config.addDefault("commands.whitelist.head-removed-wl", tr(loc.getHeadRemovedWhitelist()));
            config.addDefault("commands.whitelist.wl-a-off", tr(loc.getWhitelistAlreadyOff()));
            config.addDefault("commands.whitelist.wl-a-on", tr(loc.getBlacklistAlreadyOn()));
            config.addDefault("commands.whitelist.wl-off", tr(loc.getWhitelistOff()));
            config.addDefault("commands.whitelist.wl-on", tr(loc.getWhitelistOn()));
            config.addDefault("commands.whitelist.wlw-a-off", tr(loc.getWWhitelistAlreadyOff()));
            config.addDefault("commands.whitelist.wlw-a-on", tr(loc.getWWhitelistAlreadyOn()));
            config.addDefault("commands.whitelist.wlw-off", tr(loc.getWWhitelistOff()));
            config.addDefault("commands.whitelist.wlw-on", tr(loc.getWWhitelistOn()));
            config.addDefault("commands.whitelist.world-a-removed-wl", tr(loc.getWorldRemovedWhitelist()));
            config.addDefault("commands.whitelist.world-added-wl", tr(loc.getWorldAddedWhitelist()));
            config.addDefault("commands.whitelist.world-removed-wl", tr(loc.getWorldRemovedWhitelist()));
            config.addDefault("descriptions.hp.blacklist", (loc.descBlacklistToggle()));
            config.addDefault("descriptions.hp.blacklistadd", (loc.descBlacklistAdd()));
            config.addDefault("descriptions.hp.blacklistdel", (loc.descBlacklistDelete()));
            config.addDefault("descriptions.hp.blacklistl", (loc.descBlacklistList()));
            config.addDefault("descriptions.hp.blacklistw", (loc.descBlacklistwToggle()));
            config.addDefault("descriptions.hp.blacklistwadd", (loc.descBlacklistwAdd()));
            config.addDefault("descriptions.hp.blacklistwdel", (loc.descBlacklistwDelete()));
            config.addDefault("descriptions.hp.blacklistwl", (loc.descBlacklistwList()));
            config.addDefault("descriptions.hp.complete", (loc.descComplete()));
            config.addDefault("descriptions.hp.conjure", (loc.descConjure()));
            config.addDefault("descriptions.hp.debug", (loc.descDebug()));
            config.addDefault("descriptions.hp.headinfo", (loc.descHeadView()));
            config.addDefault("descriptions.hp.help", (loc.descHelpMenu()));
            config.addDefault("descriptions.hp.info", (loc.descInfo()));
            config.addDefault("descriptions.hp.profile", (loc.descProfile()));
            config.addDefault("descriptions.hp.reload", (loc.descMCReload()));
            config.addDefault("descriptions.hp.tests", "TODO");
            config.addDefault("descriptions.hp.whitelist", (loc.descWhitelistToggle()));
            config.addDefault("descriptions.hp.whitelistadd", (loc.descWhitelistAdd()));
            config.addDefault("descriptions.hp.whitelistdel", (loc.descWhitelistDelete()));
            config.addDefault("descriptions.hp.whitelistl", (loc.descWhitelistList()));
            config.addDefault("descriptions.hp.whitelistw", (loc.descWhitelistwToggle()));
            config.addDefault("descriptions.hp.whitelistwadd", (loc.descWhitelistwAdd()));
            config.addDefault("descriptions.hp.whitelistwdel", (loc.descWhitelistwDelete()));
            config.addDefault("descriptions.hp.whitelistwl", (loc.descWhitelistwList()));
            config.addDefault("descriptions.hp.xp", "TODO");
            config.addDefault("descriptions.addhead", (loc.descAddHead()));
            config.addDefault("descriptions.head", (loc.descHead()));
            config.addDefault("descriptions.heads", (loc.descHeads()));
            config.addDefault("descriptions.hpc", (loc.descChallenges()));
            config.addDefault("descriptions.hplb", (loc.descHPLeaderboards()));
            config.addDefault("descriptions.myhead", (loc.descMyHead()));
            config.addDefault("descriptions.sellhead", (loc.descSellhead()));
            config.addDefault("event.block-place-denied", tr(loc.getBlockPlaceDenied()));
            config.addDefault("event.head-interact-message", tr(loc.getHeadInteractMessage()));
            config.addDefault("event.head-mhf-interact-message", tr(loc.getHeadMhfInteractMessage()));
            config.addDefault("event.head-mhf-interact-message-2", tr(loc.getHeadMhfInteractMessage2()));
            config.addDefault("event.lost-money", tr(loc.lostMoney()));
            config.addDefault("startup.faulty-theme", tr(loc.badTheme()));
            config.addDefault("startup.no-vault", tr(loc.noVault()));
            config.addDefault("startup.no-vault-2", tr(loc.noVaultGroup()));
            config.addDefault("startup.plugin-disabled", tr(loc.getDisabled()));
            config.addDefault("startup.plugin-enabled", tr(loc.getEnabled()));
            config.addDefault("startup.plugin-fail", tr(loc.getErrorEnabled()));
            config.addDefault("update.current-version", tr(loc.getCurrentVersion()));
            config.addDefault("update.description", tr(loc.getDescription()));
            config.addDefault("update.new-version", tr(loc.getNewVersion()));
            config.addDefault("update.plugin-up-to-date", tr(loc.getPluginUpToDate()));
            config.addDefault("update.update-found", tr(loc.getUpdateFound()));
            config.options().copyDefaults(true);
            config.save(f2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String tr(String s) {
        if (!s.startsWith("{header} ")) {
            s = "{header} " + s;
        }
        return s.replaceAll("&c", "")
                .replaceAll("&3", "")
                .replaceAll("&b", "")
                .replaceAll("%h", "")
                .replaceAll("  ", " ")
                .replaceAll("\\{player}", "&c{player}&7")
                .replaceAll("\\{world}", "&c{world}&7")
                .replaceAll("\\{price}", "&c{price}&7")
                .replaceAll("\\{level}", "&c{level}&7")
                .replaceAll("\\{balance}", "&c{balance}&7")
                .replaceAll("%p", "&c{player}&7")
                .replaceAll("%lvl", "&c{level}&7")
                .replaceAll("%l", "&c{price}&7")
                .replaceAll("%w", "&c{world}&7")
                .replaceAll("%b", "&c{balance}&7");
    }
}
