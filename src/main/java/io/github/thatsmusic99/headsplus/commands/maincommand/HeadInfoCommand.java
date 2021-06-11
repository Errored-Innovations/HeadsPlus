package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        commandname = "headinfo",
        permission = "headsplus.maincommand.headinfo",
        subcommand = "Headinfo",
        maincommand = true,
        usage = "/hp headinfo <view> <Entity Type> [Name|Mask|Lore] [Page]"
)
public class HeadInfoCommand implements IHeadsPlusCommand {

    // A
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();
    private static final List<String> potions = new ArrayList<>();


    public HeadInfoCommand() {
        try {
            for (PotionEffectType type : PotionEffectType.values()) {
                potions.add(type.getName());
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.headinfo", sender);
    }

    @Override
    public String[] advancedUsages() {
        String[] s = new String[4];
        s[0] = "/hp headinfo view <Entity Type> [Name|Mask|Lore] [Page]";
        s[1] = "/hp headinfo set <Entity Type> <Chance|Price|Display-name|Interact-name> <Value>";
        s[2] = "/hp headinfo add <Entity Type> <Name|Mask|Lore> <Value|Effect> [Type|Amplifier]";
        s[3] = "/hp headinfo remove <Entity Type> <Name|Mask|Lore> <Value> [Type]";
        return s;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (sender.hasPermission("headsplus.maincommand.headinfo")) {
            if (args.length == 2) {
                List<String> allowed = new ArrayList<>();
                for (String str : Arrays.asList("view", "set", "add", "remove")) {
                    if (sender.hasPermission("headsplus.maincommand.headinfo." + str)) {
                        allowed.add(str);
                    }
                }
                StringUtil.copyPartialMatches(args[1], allowed, results);
            } else if (args.length == 3) {
                StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getEntities(), results);
            } else if (args.length == 4) {
                if (sender.hasPermission("headsplus.maincommand.headinfo." + args[1].toLowerCase())) {
                    switch (args[1].toLowerCase()) {
                        case "view":
                        case "add":
                        case "remove":

                            StringUtil.copyPartialMatches(args[3], Arrays.asList("name", "mask", "lore"), results);
                            break;
                        case "set":
                            StringUtil.copyPartialMatches(args[3], Arrays.asList("chance", "price", "display-name", "interact-name"), results);
                            break;
                    }
                }
            } else if (args.length == 5) {
                if (sender.hasPermission("headsplus.maincommand.headinfo." + args[1].toLowerCase())) {
                    if (args[3].equalsIgnoreCase("mask")) {
                        if (args[1].equalsIgnoreCase("add")) {
                            StringUtil.copyPartialMatches(args[4], potions, results);
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            StringUtil.copyPartialMatches(args[4], HeadsPlus.getInstance().getHeadsConfig()
                                    .getStringList(args[2].equalsIgnoreCase("WANDERING_TRADER")
                                            || args[2].equalsIgnoreCase("TRADER_LLAMA") ? args[2].toLowerCase() : args[2].toLowerCase().replace("_", "") + ".mask-effects"), results);
                        }
                    } else if (args[3].equalsIgnoreCase("lore") && args[1].equalsIgnoreCase("remove")) {
                        List<String> lore = new ArrayList<>();
                        for (String str : HeadsPlus.getInstance().getHeadsConfig()
                                .getLore(args[2].equalsIgnoreCase("WANDERING_TRADER")
                                        || args[2].equalsIgnoreCase("TRADER_LLAMA") ? args[2].toLowerCase() : args[2].toLowerCase().replace("_", ""))) {
                            lore.add(str.replaceAll("§", "&"));
                        }
                        StringUtil.copyPartialMatches(args[4], lore, results);
                    }
                }
            } else if (args.length == 6) {
                if (args[3].equalsIgnoreCase("name") && sender.hasPermission("headsplus.maincommand.headinfo." + args[1].toLowerCase())) {
                    StringUtil.copyPartialMatches(args[5], IHeadsPlusCommand.getEntityConditions(args[2]), results);
                }
            }
        }
        return results;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            ConfigMobs hpch = HeadsPlus.getInstance().getHeadsConfig();
            if (args.length > 2) {
                String type = args[2].toLowerCase().replaceAll("_", "");
                if (args[2].equalsIgnoreCase("WANDERING_TRADER") || args[2].equalsIgnoreCase("TRADER_LLAMA")) {
                    type = args[2].toLowerCase();
                }
                if (EntityDataManager.ableEntities.contains(args[2]) || args[2].equalsIgnoreCase("player")) {
                    if (sender.hasPermission("headsplus.maincommand.headinfo." + args[1].toLowerCase())) {
                        switch (args[1].toLowerCase()) {
                            case "view":
                                if (args.length > 3) {
                                    if (args.length > 4) {
                                        int page = HPUtils.isInt(args[4]);
                                        switch (args[3].toLowerCase()) {
                                            case "name":
                                                sender.sendMessage(printNameInfo(type, page, sender));
                                                break;
                                            case "mask":
                                                sender.sendMessage(ConfigTextMenus.HeadInfoTranslator.translateMaskInfo(sender, type, page));
                                                break;
                                            case "lore":
                                                sender.sendMessage(ConfigTextMenus.HeadInfoTranslator.translateLoreInfo(sender, type, page));
                                                break;
                                            default:
                                                hpc.sendMessage("commands.errors.invalid-args", sender);
                                                break;
                                        }
                                        return true;
                                    }
                                    switch (args[3].toLowerCase()) {
                                        case "name":
                                            sender.sendMessage(printNameInfo(type, 1, sender));
                                            break;
                                        case "mask":
                                            sender.sendMessage(ConfigTextMenus.HeadInfoTranslator.translateMaskInfo(sender, type, 1));
                                            break;
                                        case "lore":
                                            sender.sendMessage(ConfigTextMenus.HeadInfoTranslator.translateLoreInfo(sender, type, 1));
                                            break;
                                        default:
                                            hpc.sendMessage("commands.errors.invalid-args", sender);
                                            break;
                                    }
                                } else {
                                    sender.sendMessage(ConfigTextMenus.HeadInfoTranslator.translateNormal(type, sender));
                                }
                                break;
                            case "set":
                                if (args.length > 4) {
                                    if (args[3].equalsIgnoreCase("chance")
                                            || args[3].equalsIgnoreCase("price")) {
                                        if (isValueValid(args[3], args[4])) {
                                            hpch.getConfig().set(type + "." + args[3], Double.valueOf(args[4]));
                                        }
                                    } else if (args[3].equalsIgnoreCase("display-name")
                                            || args[3].equalsIgnoreCase("interact-name")) {
                                        hpch.getConfig().set(type + "." + args[3], args[4]);
                                    }
                                    hpc.sendMessage("commands.head-info.set-value", sender, "{value}", args[4], "{entity}", type, "{setting}", args[3]);
                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                                break;
                            case "add":
                                if (args.length > 4) {
                                    List<String> list;
                                    String sub = "name.default";
                                    switch (args[3].toLowerCase()) {
                                        case "mask":
                                            if (PotionEffectType.getByName(args[4]) != null) {
                                                int amplifier = 1;
                                                if (args.length > 5) {
                                                    amplifier = HPUtils.isInt(args[5]);
                                                }
                                                List<Integer> maskAmp = hpch.getConfig().getIntegerList(type + ".mask-amplifiers");
                                                maskAmp.add(amplifier);
                                                hpch.getConfig().set(type + ".mask-amplifiers", maskAmp);
                                                sub = "mask-effects";
                                            } else {
                                                hpc.sendMessage("commands.errors.invalid-args", sender);
                                                return false;
                                            }
                                            break;
                                        case "name":
                                            if (args.length > 5) {
                                                if (hpch.getConfig().get(type + ".name." + args[5]) != null) {
                                                    sub = "name." + args[5];
                                                } else {
                                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                                    return false;
                                                }
                                            }
                                            break;
                                        case "lore":
                                            sub = "lore";
                                            break;
                                        default:
                                            hpc.sendMessage("commands.errors.invalid-args", sender);
                                            break;
                                    }
                                    list = hpch.getConfig().getStringList(type + "." + sub);
                                    list.add(args[4]);
                                    hpch.getConfig().set(type + "." + sub, list);
                                    hpc.sendMessage("commands.head-info.add-value", sender, "{value}", args[4], "{entity}", type, "{setting}", args[3]);
                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                                break;
                            case "remove":
                                if (args.length > 4) {
                                    List<String> list;
                                    String sub = "name.default";
                                    switch (args[3].toLowerCase()) {
                                        case "name":
                                            if (args.length > 5) {
                                                if (hpch.getConfig().get(type + ".name." + args[5]) != null) {
                                                    sub = "name." + args[5];
                                                } else {
                                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                                    return false;
                                                }
                                            }
                                            list = hpch.getConfig().getStringList(type + ".name." + sub);
                                            break;
                                        case "lore":
                                            list = hpch.getConfig().getStringList(type + ".lore");
                                            sub = "lore";
                                            break;
                                        case "mask":
                                            list = hpch.getConfig().getStringList(type + ".mask-effects");
                                            List<Integer> otherList = hpch.getConfig().getIntegerList(type + ".mask-amplifiers");
                                            otherList.remove(list.indexOf(args[4]));
                                            hpch.getConfig().set(type + ".mask-amplifiers", otherList);
                                            sub = "mask-effects";
                                            break;
                                        default:
                                            hpc.sendMessage("commands.errors.invalid-args", sender);
                                            return false;
                                    }
                                    if (!list.contains(args[4])) {
                                        hpc.sendMessage("commands.errors.invalid-args", sender);
                                        return false;
                                    }
                                    list.remove(args[4]);
                                    hpch.getConfig().set(type + "." + sub, list);
                                    hpc.sendMessage("commands.head-info.remove-value", sender, "{value}", args[4], "{entity}", type, "{setting}", args[3]);

                                } else {
                                    hpc.sendMessage("commands.errors.invalid-args", sender);
                                }
                                break;
                            default:
                                hpc.sendMessage("commands.errors.invalid-args", sender);
                                break;
                        }
                    } else {
                        hpc.sendMessage("commands.errors.no-perm", sender);
                    }

                } else {
                    hpc.sendMessage("commands.errors.invalid-args", sender);
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
            hpch.getConfig().options().copyDefaults(true);
            hpch.save();
            EntityDataManager.init();
            return true;
        } catch (IndexOutOfBoundsException ex) {
            hpc.sendMessage("commands.errors.invalid-args", sender);
        }
        return false;
    }

    private boolean isValueValid(String option, String value) {
        if (option.equalsIgnoreCase("chance") || option.equalsIgnoreCase("price")) {
            try {
                Double.valueOf(value);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        } else {
            return true;
        }
    }

    private String printNameInfo(String type, int page, CommandSender p) {
        try {
            return ConfigTextMenus.HeadInfoTranslator.translateNameInfo(type, p, page);
        } catch (IllegalArgumentException ex) {
            return hpc.getString("commands.errors.invalid-pg-no", p);
        }

    }
}
