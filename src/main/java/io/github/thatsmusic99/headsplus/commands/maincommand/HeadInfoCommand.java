package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.headinfo", sender);
    }

    @Override
    public String[] advancedUsages() {
        String[] s = new String[4];
        s[0] = "/hp headinfo view <Entity Type> [Name|Mask|Lore] [Page]";
        s[1] = "/hp headinfo set <Entity Type> <Chance|Price|Display-name|Interact-name> <Value>";
        s[2] = "/hp headinfo add <Entity Type> <Name|Mask|Lore> <Value|Effect> [Colour|Amplifier]";
        s[3] = "/hp headinfo remove <Entity Type> <Name|Mask|Lore> <Index> [Colour]";
        return s;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], Arrays.asList("view", "set", "add", "remove"), results);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getEntities(), results);
        } else if (args.length == 4) {
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
        } else if (args.length == 6) {
            if (args[3].equalsIgnoreCase("name")) {
                StringUtil.copyPartialMatches(args[5], IHeadsPlusCommand.getEntityConditions(args[2]), results);
            }
        }
        return results;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
            if (args.length > 2) {
                String type = args[2].toLowerCase().replaceAll("_", "");
                if (args[2].equalsIgnoreCase("WANDERING_TRADER") || args[2].equalsIgnoreCase("TRADER_LLAMA")) {
                    type = args[2].toLowerCase();
                }
                if (args[1].equalsIgnoreCase("view")) {
                    if (DeathEvents.ableEntities.contains(args[2])) {
                        if (args.length > 3) {
                            if (args[3].equalsIgnoreCase("name") || args[3].equalsIgnoreCase("lore") || args[3].equalsIgnoreCase("mask")) {
                                if (args.length > 4) {
                                    if (CachedValues.MATCH_PAGE.matcher(args[4]).matches()) {
                                        switch (args[3].toLowerCase()) {
                                            case "name":
                                                sender.sendMessage(printNameInfo(type, Integer.parseInt(args[4]), sender));
                                                break;
                                            case "mask":
                                                sender.sendMessage(HeadsPlusConfigTextMenu.HeadInfoTranslator.translateMaskInfo(sender, type, Integer.parseInt(args[4])));
                                                break;
                                            case "lore":
                                                sender.sendMessage(HeadsPlusConfigTextMenu.HeadInfoTranslator.translateLoreInfo(sender, type, Integer.parseInt(args[4])));
                                                break;
                                        }
                                        return true;
                                    }
                                }
                                switch (args[3].toLowerCase()) {
                                    case "name":
                                        sender.sendMessage(printNameInfo(type, 1, sender));
                                        break;
                                    case "mask":
                                        sender.sendMessage(HeadsPlusConfigTextMenu.HeadInfoTranslator.translateMaskInfo(sender, type, 1));
                                        break;
                                    case "lore":
                                        sender.sendMessage(HeadsPlusConfigTextMenu.HeadInfoTranslator.translateLoreInfo(sender, type, 1));
                                        break;
                                }

                            } else {
                                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                            }
                        } else {
                            sender.sendMessage(HeadsPlusConfigTextMenu.HeadInfoTranslator.translateNormal(type, sender));
                        }
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }
                } else if (args[1].equalsIgnoreCase("set")) {
                    // TODO sort out this bs
                    if (args.length > 4) {
                        if (DeathEvents.ableEntities.contains(args[2])) {
                            if (args[3].equalsIgnoreCase("chance")
                                    || args[3].equalsIgnoreCase("price")) {
                                if (isValueValid(args[3], args[4])) {
                                    hpch.getConfig().set(type + "." + args[3], Double.valueOf(args[4]));

                                }
                            } else if (args[3].equalsIgnoreCase("display-name")
                                    || args[3].equalsIgnoreCase("interact-name")) {
                                hpch.getConfig().set(type + "." + args[3], args[4]);
                            }
                            sender.sendMessage(hpc.getString("commands.head-info.set-value", sender)
                                    .replaceAll("\\{value}", args[4])
                                    .replaceAll("\\{entity}", type)
                                    .replaceAll("\\{setting}", args[3]));
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                        }
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }

                } else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length > 4) {
                        if (hpch.mHeads.contains(args[2].toLowerCase())
                                || hpch.uHeads.contains(args[2].toLowerCase())
                                || args[2].equalsIgnoreCase("player")) {
                            if (PotionEffectType.getByName(args[4]) != null) {
                                int amplifier = 1;
                                if (args.length > 5) {
                                    if (CachedValues.MATCH_PAGE.matcher(args[5]).matches()) {
                                        amplifier = Integer.parseInt(args[5]);
                                    } else {
                                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                                        return false;
                                    }
                                }
                                List<Integer> maskAmp = hpch.getConfig().getIntegerList(type + ".mask-amplifiers");
                                maskAmp.add(amplifier);
                                hpch.getConfig().set(type + ".mask-amplifiers", maskAmp);
                                List<String> masks = hpch.getConfig().getStringList(type + ".mask-effects");
                                masks.add(args[4]);
                                hpch.getConfig().set(type + ".mask-effects", masks);
                            } else {
                                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                            }
                        } else if (args[3].equalsIgnoreCase("name")) {
                            String path;
                            if (args.length > 5) {
                                if (hpch.getConfig().get(type + ".name." + args[5]) != null) {
                                    path = type + ".name." + args[5];
                                } else {
                                    sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                                    return false;
                                }
                            } else {
                                if (hpch.getConfig().get(type + ".name") instanceof ConfigurationSection) {
                                    path = type + ".name.default";
                                } else {
                                    path = type + ".name";
                                }
                            }
                            List<String> s = hpch.getConfig().getStringList(path);
                            s.add(args[4]);
                            hpch.getConfig().set(path, s);
                        } else if (args[3].equalsIgnoreCase("lore")){
                            List<String> lore = hpch.getConfig().getStringList(type + "." + args[3]);
                            lore.add(args[4]);
                            hpch.getConfig().set(type + "." + args[3], lore);
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                        }
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length > 4) {
                        if (hpch.mHeads.contains(args[2].toLowerCase())
                            || hpch.uHeads.contains(args[2].toLowerCase())
                                || args[2].equalsIgnoreCase("player")) {
                            if (args[3].equalsIgnoreCase("name")
                                    || args[3].equalsIgnoreCase("lore")
                                    || args[3].equalsIgnoreCase("mask")) {
                                if (CachedValues.MATCH_PAGE.matcher(args[4]).matches()) {
                                    if (args[3].equalsIgnoreCase("name")) {
                                        String path;
                                        if (args.length > 5) {
                                            if (hpch.getConfig().get(type + ".name." + args[5]) != null) {
                                                path = type + ".name." + args[5];
                                            } else {
                                                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                                                return false;
                                            }
                                        } else {
                                            if (hpch.getConfig().get(type + ".name") instanceof ConfigurationSection) {
                                                path = type + ".name.default";
                                            } else {
                                                path = type + ".name";
                                            }
                                        }
                                        List<String> s = hpch.getConfig().getStringList(path);
                                        s.remove(Integer.parseInt(args[4]));
                                        hpch.getConfig().set(path, s);
                                    } else if (args[3].equalsIgnoreCase("lore")) {
                                        List<String> lore = hpch.getConfig().getStringList(type + "." + args[3]);
                                        lore.remove(Integer.parseInt(args[4]));
                                        hpch.getConfig().set(type + "." + args[3], lore);
                                    } else if (args[3].equalsIgnoreCase("mask")) {
                                        List<Integer> maskAmplifiers = hpch.getConfig().getIntegerList(type + ".mask-amplifiers");
                                        maskAmplifiers.remove(Integer.parseInt(args[4]));
                                        hpch.getConfig().set(type + ".mask-amplifiers", maskAmplifiers);
                                        List<String> masks = hpch.getConfig().getStringList(type + ".mask-effects");
                                        masks.remove(Integer.parseInt(args[4]));
                                        hpch.getConfig().set(type + ".mask-effects", masks);
                                    }
                                } else {
                                    sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                                }
                            } else {
                                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                            }
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                        }
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                }
            } else {
                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
            }
            hpch.getConfig().options().copyDefaults(true);
            hpch.save();
            return true;
        } catch (IndexOutOfBoundsException ex) {
            sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
        } catch (Exception e) {
            DebugPrint.createReport(e, "Head Information command", true, sender);
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
            if (type.equalsIgnoreCase("sheep")
                    || type.equalsIgnoreCase("parrot")
                    || type.equalsIgnoreCase("llama")
                    || type.equalsIgnoreCase("horse")) {
                return HeadsPlusConfigTextMenu.HeadInfoTranslator.translateColored(p, type, page);
            } else {
                return HeadsPlusConfigTextMenu.HeadInfoTranslator.translateNormal(type, p);
            }
        } catch (IllegalArgumentException ex) {
            return hpc.getString("commands.errors.invalid-pg-no", p);
        }

    }
}
