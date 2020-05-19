package io.github.thatsmusic99.headsplus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked")
public class DebugFileCreator {

    public String createReport(Exception e, String when) throws IOException {
        HeadsPlus hp = HeadsPlus.getInstance();
        JSONArray array1 = new JSONArray();
        JSONObject o1 = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
        o1.put("Date", date);
        o1.put("Special message", getErrorHeader());
        try {
            o1.put("HeadsPlus version", hp.getDescription().getVersion());
            o1.put("NMS version", hp.getNMS().getNMSVersion());
            o1.put("Has Vault hooked", hp.econ());
            o1.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o1.put("Locale", hp.getConfiguration().getConfig().getString("locale"));
        } catch (NullPointerException ignored) {

        }

        o1.put("Server version", Bukkit.getVersion());
        JSONObject o3 = new JSONObject();
        try {
            o3.put("Autograb enabled", hp.getConfiguration().getConfig().getBoolean("plugin.autograb.enabled"));
            o3.put("Droppable heads enabled", hp.isDropsEnabled());
            o3.put("Sellable heads enabled", hp.getConfiguration().getPerks().sell_heads);
            o3.put("Uses heads selector", hp.isUsingHeadDatabase());
            o3.put("Uses leaderboards", hp.isUsingLeaderboards());
            o3.put("Stops placement of sellable heads", hp.isStoppingPlaceableHeads());
            o3.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o3.put("Player death messages", hp.isDeathMessagesEnabled());
            o3.put("Total challenges", hp.getChallenges().size());
            o3.put("Total levels", hp.getLevels().size());
            o3.put("Masks enabled", hp.getConfiguration().getPerks().mask_powerups);
            o3.put("Allows looting enchantment", hp.getConfiguration().getMechanics().getBoolean("allow-looting-enchantment"));
            o3.put("Levels enabled", hp.usingLevels());
        } catch (NullPointerException ignored) {

        }
        o3.put("Cached players", HPPlayer.players.size());
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception ex) {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion());
            }
        }
        o1.put("Other Plugins", plugins);
        o1.put("Plugin values", o3);
        if (e != null) {
            JSONObject o4 = new JSONObject();
            o4.put("Message", e.getMessage());
            try {
                o4.put("Cause", e.getCause().getClass().getName());
            } catch (NullPointerException ignored) {

            }
            o4.put("Fired when", when);
            JSONArray array = new JSONArray();
            array.addAll(Arrays.asList(getStackTrace(e).split("\r\n\t")));
            o4.put("Stacktrace", array);
            o1.put("Exception details", o4);
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        array1.add(o1);
        String str = gson.toJson(array1);
        OutputStreamWriter fw;
        boolean cancelled = false;
        File fr = null;
        for (int i = 0; !cancelled; i++) {
            File f2 = new File(hp.getDataFolder() + "/debug");
            if (!f2.exists()) {
                f2.mkdir();
            }
            File f = new File(hp.getDataFolder() + "/debug/", date.replaceAll(":", "_").replaceAll("/", ".") + "-REPORT-" + i + ".json");
            if (!f.exists()) {
                f.createNewFile();
                fr = f;
                cancelled = true;
            }
        }
        fw = new OutputStreamWriter(new FileOutputStream(fr));
        try {
            fw.write(str.replace("\u0026", "&"));
        } finally {
            fw.flush();
            fw.close();
        }
        return fr.getName();
    }

    public String createHeadReport(ItemStack s) throws NoSuchFieldException, IllegalAccessException, IOException {
        HeadsPlus hp = HeadsPlus.getInstance();
        JSONArray array1 = new JSONArray();
        JSONObject o1 = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
        o1.put("Date", date);
        o1.put("Special message", getErrorHeader());
        try {
            o1.put("HeadsPlus version", hp.getDescription().getVersion());
            o1.put("NMS version", hp.getNMS().getClass().getSimpleName());
            o1.put("Has Vault hooked", hp.econ());
            o1.put("Locale", hp.getConfiguration().getConfig().getString("locale"));
        } catch (NullPointerException ignored) {

        }
        o1.put("Skull Type", s.getType().name());
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception ex) {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion());
            }
        }
        o1.put("Other Plugins", plugins);
        o1.put("Server version", Bukkit.getVersion());
        JSONObject o2 = new JSONObject();
        o2.put("Amount", s.getAmount());
        o2.put("Durability", s.getDurability());
        o2.put("Display name", s.getItemMeta().getDisplayName());
        try {
            JSONArray ee = new JSONArray();
            ee.addAll(s.getItemMeta().getLore());
            o2.put("Lore", ee);
        } catch (NullPointerException ignored) {
        }
        try {
            o2.put("Owning Player", hp.getNMS().getSkullOwnerName((SkullMeta) s.getItemMeta()));
        } catch (NullPointerException ignored) {
        }
        try {
            Field pro = ((SkullMeta) s.getItemMeta()).getClass().getDeclaredField("profile");
            pro.setAccessible(true);
            GameProfile gm = (GameProfile) pro.get(s.getItemMeta());
            o2.put("Texture", gm.getProperties().get("textures").iterator().next().getValue());
        } catch (NullPointerException ignored) {

        }

        o2.put("Can be sold", hp.getAPI().isSellable(s));
        o2.put("Skull Type", hp.getAPI().getSkullType(s));
        o1.put("Head details", o2);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        array1.add(o1);
        String str = gson.toJson(array1);
        OutputStreamWriter fw;
        boolean cancelled = false;
        File fr = null;
        for (int i = 0; !cancelled; i++) {
            File f2 = new File(hp.getDataFolder() + File.separator + "debug");
            if (!f2.exists()) {
                f2.mkdir();
            }
            File f = new File(hp.getDataFolder() + File.separator + "debug" + File.separator, date.replaceAll(":", "_").replaceAll("/", ".") + "-REPORT-" + i + ".json");
            if (!f.exists()) {
                fr = f;
                cancelled = true;
            }
        }
        fw = new OutputStreamWriter(new FileOutputStream(fr));
        try {
            fw.write(str.replace("\u0026", "&"));
        } finally {
            fw.flush();
            fw.close();
        }
        return fr.getName();
    }

    public String createPlayerReport(HPPlayer player) throws IOException {
        JSONArray array1 = new JSONArray();
        HeadsPlus hp = HeadsPlus.getInstance();
        JSONObject o1 = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
        o1.put("Date", date);
        o1.put("Special message", getErrorHeader());
        try {
            o1.put("HeadsPlus version", hp.getDescription().getVersion());
            o1.put("NMS version", hp.getNMS().getClass().getSimpleName());
            o1.put("Has Vault hooked", hp.econ());
            o1.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o1.put("Locale", hp.getConfiguration().getConfig().getString("locale"));
        } catch (NullPointerException ignored) {

        }
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception ex) {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion());
            }
        }
        o1.put("Other Plugins", plugins);
        JSONObject o2 = new JSONObject();
        o2.put("Name", player.getPlayer().getName());
        o2.put("UUID", player.getPlayer().getUniqueId());
        o2.put("Banned", player.getPlayer().isBanned());
        o2.put("Online", player.getPlayer().isOnline());
        o2.put("XP", player.getXp());
        o2.put("Completed challenges", player.getCompleteChallenges());
        o2.put("Level", player.getLevel());
        o2.put("Next level", player.getNextLevel());
        JSONObject o3 = new JSONObject();
        String type = player.getActiveMaskType();
        JSONArray a = new JSONArray();
        for (PotionEffect p : player.getActiveMasks()) {
            a.add(p.getType().getName());
        }
        o3.put(type, a);
        o2.put("Masks", o3);
        o1.put("Server version", Bukkit.getVersion());
        o1.put("Player details", o2);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        array1.add(o1);
        String str = gson.toJson(array1);
        OutputStreamWriter fw;
        boolean cancelled = false;
        File fr = null;

        for (int i = 0; !cancelled; i++) {
            File f2 = new File(hp.getDataFolder() + "/debug");
            if (!f2.exists()) {
                f2.mkdir();
            }
            File f = new File(hp.getDataFolder() + "/debug/", date.replaceAll(":", "_").replaceAll("/", ".") + "-REPORT-" + i + ".json");
            if (!f.exists()) {
                fr = f;
                cancelled = true;
            }
        }
        fw = new OutputStreamWriter(new FileOutputStream(fr));
        try {
            fw.write(str.replace("\u0026", "&"));
        } finally {
            fw.flush();
            fw.close();
        }
        return fr.getName();
    }

    public String createItemReport(ItemStack item) throws IOException {
        JSONArray array1 = new JSONArray();
        HeadsPlus hp = HeadsPlus.getInstance();
        JSONObject o1 = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (System.currentTimeMillis()));
        o1.put("Date", date);
        o1.put("Special message", getErrorHeader());
        try {
            o1.put("HeadsPlus version", hp.getDescription().getVersion());
            o1.put("NMS version", hp.getNMS().getClass().getSimpleName());
            o1.put("Has Vault hooked", hp.econ());
            o1.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o1.put("Locale", hp.getConfiguration().getConfig().getString("locale"));
        } catch (NullPointerException ignored) {

        }
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception ex) {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion());
            }
        }
        o1.put("Other Plugins", plugins);
        JSONObject o2 = new JSONObject();
        o2.put("material", item.getType());
        o2.put("amount", item.getAmount());
        JSONObject o3 = new JSONObject();
        for (String key : hp.getNMS().getNBTTags(item).keySet()) {
            o3.put(key, hp.getNMS().getNBTTags(item).get(key));
        }
        o2.put("NBT Tags", o3);
        o1.put("Item details", o2);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        array1.add(o1);
        String str = gson.toJson(array1);
        OutputStreamWriter fw;
        boolean cancelled = false;
        File fr = null;

        for (int i = 0; !cancelled; i++) {
            File f2 = new File(hp.getDataFolder() + "/debug");
            if (!f2.exists()) {
                f2.mkdir();
            }
            File f = new File(hp.getDataFolder() + "/debug/", date.replaceAll(":", "_").replaceAll("/", ".") + "-REPORT-" + i + ".json");
            if (!f.exists()) {
                fr = f;
                cancelled = true;
            }
        }
        fw = new OutputStreamWriter(new FileOutputStream(fr));
        try {
            fw.write(str.replace("\u0026", "&"));
        } finally {
            fw.flush();
            fw.close();
        }
        return fr.getName();
    }

    private String getErrorHeader() {
        List<String> msgs = new ArrayList<>();
        msgs.add("Oh sorry, did I hurt you?");
        msgs.add("Oopsie Whoopsie! UwU We made a ****y wucky! A wittle ****o boingo! The code monkeys at our headquarters are working VEWY HARD to fix this!");
        msgs.add("The plugin works well with a few exceptions, amirite?");
        msgs.add("Please don't put me on Santa's naughty list.");
        msgs.add("Off with your head!");
        msgs.add("Ohhh, what does this button do???");
        msgs.add("Uh oh.");
        msgs.add("help ive fallen over and i can't get up i need @someone");
        msgs.add(":sobbing:");
        msgs.add("Keyboard not found. Press F1 to continue.");
        msgs.add("Correct way of not doing this found. It's also the only way.");
        msgs.add("Sorry lad but your mouse was disconnected. Click \"OK\" to continue.");
        msgs.add("MEMORY ERROR - I forgot what I was meant to say.");
        msgs.add("Your plugin has ran into a problem and has to dump an error file. Error code: AGGHHHHHH.");
        msgs.add("System Error: Windows XP isn't an OS.");
        msgs.add("[INFO]: Task failed successfully.");
        msgs.add("NullPointer for president!");
        msgs.add("Critical process probably died, we're looking into it");
        msgs.add("Still, could've been a blue screen.");
        msgs.add("Something smells - it's my code.");
        msgs.add("One does not simply HeadsPlusPlusPlusPlusPlus.");
        msgs.add("I have a dream that I'LL ACTUALLY WORK.");
        msgs.add("I need a hug.");
        msgs.add("Y tho?");
        msgs.add("Trans rights!");
        msgs.add("9 out of 10 server owners recommend this plugin- oh wait...");
        msgs.add("VIDEO GAMES CAUSE VIOLENCE");
        msgs.add("Ah, okay.");
        msgs.add("yikes");
        msgs.add("Error, the plugin dev: \"You're weak.\" Plugin error: \"I'm you.\"");
        msgs.add("Now this is an Avengers threat level...");
        msgs.add("Creeper? Aww man...");
        msgs.add("My server runs on win 10");
        msgs.add("Rumour has it you'll find a human version of this plugin where the cat girls are in area 51");
        msgs.add("I'm sorry, I tried :(");
        msgs.add("YOU'RE breathtaking!");
        msgs.add("Ilysm Nie <3");
        msgs.add("VILLAGER CHEST. VILLAGER CHEST. VILLAGER CHEST.");
        msgs.add("This plugin is going to start WW3 with that attitude");
        msgs.add("Also try AdvancedTeleport! You don't get errors like this there. I hope.");
        msgs.add("HeadDatabase is a good plugin too, you know!");
        msgs.add("KEVIIIIIIIIIIIIIIIIIN!!! FOR GOODNESS SAKE, NO I'M NOT!");
        msgs.add("what the hell, chris");
        msgs.add("nope, nope, nope, out. no. get out. get out of here.");
        msgs.add("what's the time quarter to 9 time to take a bath what do you mean we're already clean scrub scrub scrub til the waters brown");
        msgs.add("B L E H");
        msgs.add("At least you didn't come up to me saying I'd be a dad...");
        msgs.add("THEY BROUGHT BRITISH MILK");
        msgs.add("I don’t want my plugin on a website called sooch");
        msgs.add("wind plugin?");
        msgs.add("https://cdn.discordapp.com/emojis/666611994148864000.gif?v=1");
        msgs.add("HE FRICKIN' STOLE TOT");
        msgs.add("the child is here");
        msgs.add("HAHAHAHHAHAHAHHAH! FOOOOOOOOOOOOOOOOOOOOOOoooooo... oh wait, it's you again?");
        int random = new Random().nextInt(msgs.size());
        return msgs.get(random);
    }

    // From the ExceptionUtils in Apache Commons Language 3 library which got f*king removed ;-;
    private static String getStackTrace(Throwable var0) {
        StringWriter var1 = new StringWriter();
        PrintWriter var2 = new PrintWriter(var1, true);
        var0.printStackTrace(var2);
        return var1.getBuffer().toString();
    }
}
