package io.github.thatsmusic99.headsplus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked")
public class DebugFileCreator {

    @Deprecated
    public String createReport(Exception e, String when) throws IOException {
        HeadsPlus hp = HeadsPlus.get();
        JSONArray array1 = new JSONArray();
        JSONObject o1 = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        o1.put("Date", date);
        o1.put("Special message", getErrorHeader());
        try {
            o1.put("HeadsPlus version", hp.getDescription().getVersion());
            o1.put("NMS version", Bukkit.getVersion());
            o1.put("Has Vault hooked", hp.isVaultEnabled());
            o1.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o1.put("Locale", MainConfig.get().getLocalisation().LOCALE);
        } catch (NullPointerException ignored) {

        }

        o1.put("Server version", Bukkit.getVersion());
        JSONObject o3 = new JSONObject();
        try {
            o3.put("Autograb enabled", ConfigHeadsSelector.get().getBoolean("autograb"));
            o3.put("Droppable heads enabled", MainConfig.get().getMainFeatures().MOB_DROPS);
            o3.put("Sellable heads enabled", MainConfig.get().getMainFeatures().SELL_HEADS);
            o3.put("Uses heads selector", MainConfig.get().getMainFeatures().HEADS_SELECTOR);
            o3.put("Uses leaderboards", MainConfig.get().getMainFeatures().LEADERBOARDS);
            o3.put("Stops placement of sellable heads", MainConfig.get().getSellingHeads().STOP_PLACEMENT);
            o3.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            o3.put("Player death messages", MainConfig.get().getPlayerDrops().ENABLE_PLAYER_DEATH_MESSAGES);
            o3.put("Total challenges", hp.getChallenges().size());
            o3.put("Total levels", hp.getLevels().size());
            o3.put("Masks enabled", MainConfig.get().getMainFeatures().MASKS);
            o3.put("Allows looting enchantment", MainConfig.get().getMobDrops().ENABLE_LOOTING);
            o3.put("Levels enabled", MainConfig.get().getMainFeatures().LEVELS);
        } catch (NullPointerException ignored) {

        }
        o3.put("Cached players", HPPlayer.players.size());
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception | NoSuchMethodError ex) {
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
            createDebugFolder();
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

    public static String createReport(HeadsPlusException exception) {
        JSONObject json = getBasicInfo();
        if (exception != null) {
            JSONObject errorInfo = new JSONObject();
            for (String str : exception.getExceptionInfo().keySet()) {
                errorInfo.put(str, exception.getExceptionInfo().get(str));
            }
            JSONArray array = new JSONArray();
            array.addAll(Arrays.asList(getStackTrace(exception.getOriginalException()).split("\r\n\t")));
            errorInfo.put("Exception", array);
            json.put("Error Information", errorInfo);
        }
        try {
            return save(json);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    private static String save(JSONObject json) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String jsonString = gson.toJson(json);
        OutputStreamWriter writer;
        File fileReport = getReportFile();
        writer = new OutputStreamWriter(new FileOutputStream(fileReport));
        try {
            writer.write(jsonString.replace("\u0026", "&"));
        } finally {
            writer.flush();
            writer.close();
        }
        return fileReport.getName();
    }

    public String createHeadReport(ItemStack s) throws NoSuchFieldException, IllegalAccessException, IOException {
        HeadsPlus hp = HeadsPlus.get();
        JSONArray infoArray = new JSONArray();
        JSONObject basicInfo = getBasicInfo();
        JSONObject headDetails = new JSONObject();
        headDetails.put("Amount", s.getAmount());
        headDetails.put("Display name", s.getItemMeta().getDisplayName());
        try {
            JSONArray lore = new JSONArray();
            lore.addAll(s.getItemMeta().getLore());
            headDetails.put("Lore", lore);
        } catch (NullPointerException ignored) {
        }
        try {
            headDetails.put("Owning Player", ((SkullMeta) s.getItemMeta()).getOwningPlayer());
        } catch (NullPointerException ignored) {
        }
        try {
            GameProfile gm = ProfileFetcher.getProfile(s);
            headDetails.put("Texture", gm.getProperties().get("textures").iterator().next().getValue());
        } catch (NullPointerException ignored) {

        }

        headDetails.put("Can be sold", PersistenceManager.get().isSellable(s));
        headDetails.put("Skull Type", PersistenceManager.get().getSellType(s));
        basicInfo.put("Head details", headDetails);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        infoArray.add(basicInfo);
        String jsonString = gson.toJson(infoArray);
        OutputStreamWriter writer;
        File fileReport = getReportFile();
        writer = new OutputStreamWriter(new FileOutputStream(fileReport));
        try {
            writer.write(jsonString.replace("\u0026", "&"));
        } finally {
            writer.flush();
            writer.close();
        }
        return fileReport.getName();
    }

    public String createPlayerReport(HPPlayer player) throws IOException {
        JSONArray infoArray = new JSONArray();
        JSONObject basicInfo = getBasicInfo();
        JSONObject playerInfo = new JSONObject();
        playerInfo.put("Name", player.getPlayer().getName());
        playerInfo.put("UUID", player.getPlayer().getUniqueId());
        playerInfo.put("Banned", player.getPlayer().isBanned());
        playerInfo.put("Online", player.getPlayer().isOnline());
        playerInfo.put("XP", player.getXp());
        playerInfo.put("Completed challenges", player.getCompleteChallenges());
        playerInfo.put("Level", player.getLevel());
        playerInfo.put("Next level", player.getNextLevel());
        JSONObject maskInfo = new JSONObject();
        String type = player.getActiveMaskType();
        JSONArray a = new JSONArray();
        for (PotionEffect p : player.getActiveMasks()) {
            a.add(p.getType().getName());
        }
        maskInfo.put(type, a);
        playerInfo.put("Masks", maskInfo);
        basicInfo.put("Server version", Bukkit.getVersion());
        basicInfo.put("Player details", playerInfo);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        infoArray.add(basicInfo);
        String str = gson.toJson(infoArray);
        OutputStreamWriter writer;
        File fileReport = getReportFile();
        writer = new OutputStreamWriter(new FileOutputStream(fileReport));
        try {
            writer.write(str.replace("\u0026", "&"));
        } finally {
            writer.flush();
            writer.close();
        }
        return fileReport.getName();
    }

    public String createItemReport(ItemStack item) throws IOException {
        JSONArray infoArray = new JSONArray();
        JSONObject basicInfo = getBasicInfo();
        JSONObject itemInfo = new JSONObject();
        itemInfo.put("material", item.getType());
        itemInfo.put("amount", item.getAmount());
        basicInfo.put("Item details", itemInfo);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        infoArray.add(basicInfo);
        String jsonString = gson.toJson(infoArray);
        OutputStreamWriter writer;
        File fileReport = getReportFile();
        writer = new OutputStreamWriter(new FileOutputStream(fileReport));
        try {
            writer.write(jsonString.replace("\u0026", "&"));
        } finally {
            writer.flush();
            writer.close();
        }
        return fileReport.getName();
    }

    private static JSONObject getBasicInfo() {
        HeadsPlus hp = HeadsPlus.get();
        JSONObject basicInfo = new JSONObject();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        basicInfo.put("Date", date);
        basicInfo.put("Special message", getErrorHeader());
        try {
            basicInfo.put("HeadsPlus version", hp.getDescription().getVersion());
            basicInfo.put("Has Vault hooked", hp.isVaultEnabled());
            basicInfo.put("MySQL is enabled", hp.isConnectedToMySQLDatabase());
            basicInfo.put("Locale", MainConfig.get().getLocalisation().LOCALE);
        } catch (NullPointerException ignored) {

        }
        JSONArray plugins = getPluginArray();
        basicInfo.put("Other Plugins", plugins);
        return basicInfo;
    }

    private static String getErrorHeader() {
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
        msgs.add("wingless has a cursed rust server");
        msgs.add("THERE IS NO FRIDGE");
        msgs.add("this meme breaks every rule but it's epic");
        msgs.add("BEES");
        msgs.add("Hi there, my name is the Kneecap bot! And I can confirm this a big YIKES moment.");
        msgs.add("https://media.discordapp.net/attachments/715322160226238635/730734060401590322/mau424glmas31.png");
        msgs.add("This Java-built addition to your video game software which allows multiple individuals with the required client to connect has spontaneously failed to carry out one of its many functions, please report this troubling situation to the individual who compiled this code so that she can investigate further.");
        msgs.add("yuganda sekai ni dan dan boku wa sukitootte mienaku natte\n" +
                "mitsukenaide boku no koto wo mitsumenaide\n" +
                "dareka ga egaita sekai no naka de anata wo kizutsuketaku wa nai yo\n" +
                "oboeteite boku no koto wo azayaka na mama");
        msgs.add("Something's looking sus.");
        msgs.add("We're halfway thereeeeeee OwO Notices a bug there!");
        int random = new Random().nextInt(msgs.size());
        return msgs.get(random);
    }

    // From the ExceptionUtils in Apache Commons Language 3 library which got f*king removed ;-;
    private static String getStackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer, true);
        throwable.printStackTrace(printer);
        return writer.getBuffer().toString();
    }

    private static JSONArray getPluginArray() {
        JSONArray plugins = new JSONArray();
        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            try {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion() + " (" + plugin.getDescription().getAPIVersion() + ")");
            } catch (Exception | NoSuchMethodError e) {
                plugins.add(plugin.getName() + "-" + plugin.getDescription().getVersion());
            }
        }
        return plugins;
    }

    private static void createDebugFolder() {
        File debugFolder = new File(HeadsPlus.get().getDataFolder() + File.separator + "debug");
        if (!debugFolder.exists()) {
            debugFolder.mkdir();
        }
    }

    /**
     * @return Unique file name in case of multiple reports in short amount of time
     */
    private static File getReportFile() {
        createDebugFolder();
        for (int i = 0; true; i++) {
            String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
            File report = new File(HeadsPlus.get().getDataFolder() + "/debug/", date.replaceAll(":", "_").replaceAll("/", ".") + "-REPORT-" + i + ".json");
            if (!report.exists()) {
                return report;
            }
        }
    }
}
