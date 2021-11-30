package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Rabbit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigMobs extends FeatureConfig {

    private static ConfigMobs instance;

    public ConfigMobs() {
        super("mobs.yml");
        instance = this;
    }

    public static ConfigMobs get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        addComment("This configuration file has become more complex compared to previous versions.\n" +
                "If you have trouble understanding how it works, please use the /hp config mobs command instead.");
        addExample("defaults.lore", new ArrayList<>(Arrays.asList("&7Price &8» &c{price}", "&7Type &8» &c{type}")));
        addExample("defaults.display-name", "{type} Head");
        addHeads();
        addPlayerHeads();
    }

    @Override
    public void moveToNew() {
        for (String key : existingValues.keySet()) {
            if (!(existingValues.get(key) instanceof ConfigSection)) continue;
            ConfigSection section = (ConfigSection) existingValues.get(key);
            if (section == null
                    || !section.contains("interact-name", true)
                    || !section.contains("name", true)
                    || !(section.get("name", true) instanceof ConfigSection)) continue;
            ConfigSection name = section.getConfigSection("name", true);
            if (name == null) continue;
            for (String nameKey : name.getKeys(false)) {
                if (!(name.get(nameKey) instanceof List)) continue;
                for (String actualName : name.getStringList(nameKey)) {
                    if (actualName.startsWith("HP#")) {
                        moveTo(key + ".interact-name", "special.textures." + actualName + ".name",
                                ConfigInteractions.get());
                    } else {
                        moveTo(key + ".interact-name", "special.names." + actualName + ".name",
                                ConfigInteractions.get());
                    }
                }
            }
        }

        for (String head : Arrays.asList("brownCoconutHead", "greenCoconutHead", "oakLogHead", "present1Head",
                "present2Head", "tntHead", "tnt2Head", "arrowUpHead", "arrowDownHead", "arrowRightHead",
                "arrowLeftHead",
                "excalamationHead", "questionHead")) {
            moveTo(head + "EN", "special.names." + getString(head + "N") + ".name", ConfigInteractions.get());
            set(head + "N", null);
        }

        for (String entity : Arrays.asList("axolotl", "bat", "blaze", "bee", "cat", "chicken", "cod", "cow", "creeper"
                , "dolphin", "donkey", "drowned",
                "enderman", "endermite", "evoker", "fox", "ghast", "giant", "guardian", "hoglin", "horse", "husk",
                "llama", "mule",
                "ocelot", "panda", "parrot", "phantom", "pig", "piglin", "pillager", "pufferfish", "rabbit", "ravager"
                , "salmon", "sheep",
                "shulker", "silverfish", "skeleton", "slime", "snowman", "spider", "squid", "stray", "strider",
                "trader_llama", "turtle", "vex", "villager",
                "vindicator", "wandering_trader", "witch", "wither", "wolf", "zoglin", "zombie")) {

            transferEntity(entity.toUpperCase()); // I AM SUCH A UH UHHHH TWO NUMBER NINES
        }

        for (String entity : Arrays.asList("CAVE_SPIDER", "ELDER_GUARDIAN", "ENDER_DRAGON", "IRON_GOLEM", "MAGMA_CUBE",
                "MUSHROOM_COW", "PIGLIN_BRUTE", "PIG_ZOMBIE", "POLAR_BEAR", "SKELETON_HORSE", "TROPICAL_FISH",
                "WITHER_SKELETON",
                "ZOMBIE_HORSE", "ZOMBIE_VILLAGER", "ZOMBIFIED_PIGLIN")) {

            transferEntity(entity);
        }

        try {
            ConfigMasks.get().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transferEntity(String entity) {
        String oldEntity = entity.replace("_", "").toLowerCase(Locale.ROOT);
        // Transfer masks
        List<String> maskList = getList(oldEntity + ".mask-effects", new ArrayList<>(), true);
        List<Integer> amplifierList = getList(oldEntity + ".mask-amplifiers", new ArrayList<>(), true);
        for (int i = 0; i < maskList.size(); i++) {
            maskList.set(i, maskList.get(i) + (amplifierList.size() > i ? ":" + amplifierList.get(i) : ""));
        }

        ConfigSection section = getConfigSection(oldEntity + ".name", true);
        if (section == null) return;
        for (String key : section.getKeys(false, true)) {
            if (get(oldEntity + ".name." + key, true) instanceof ConfigSection) break;
            List<String> heads = getList(oldEntity + ".name." + key, true);
            for (String head : heads) {
                if (maskList.size() > 0 && !head.equals("{mob-default}")) {
                    String newHead = head.substring(0, 2) + "M" + head.substring(2);
                    String id = head.substring(3);
                    HeadsPlus.get().getLogger().info(maskList.toString());
                    ConfigMasks.get().forceExample("masks." + id + ".when-wearing", new String[0]);
                    ConfigMasks.get().forceExample("masks." + id + ".effects", new ArrayList<>(maskList));
                    ConfigMasks.get().forceExample("masks." + id + ".type", "potion");
                    ConfigMasks.get().forceExample("masks." + id + ".idle", head);
                    createConfigSection(entity + "." + key + "." + newHead);
                } else {
                    createConfigSection(entity + "." + key + "." + head);
                }
            }
        }
    }

    private void addHeads() {
		/* New format:
		 AXOLOTL:
		   default:
		     HP#lucy_axolotl:
		       chance: 5


		 */
        for (String key : EntityDataManager.ableEntities) {
            switch (key) {
                case "AXOLOTL":
                    addDefaultHead(key + ".default", "HP#lucy_axolotl");
                    for (String color : new String[]{"LUCY", "WILD", "GOLD", "CYAN", "BLUE"}) {
                        addDefaultHead(key + "." + color, "HP#" + color.toLowerCase() + "_axolotl");
                    }
                    break;
                case "BEE":
                    addDefaultHead(key + ".default", "HP#bee_mc");
                    addDefaultHead(key + ".ANGRY", "HP#bee_angry");
                    addDefaultHead(key + ".ANGRY,NECTAR", "HP#bee_pollinated_angry");
                    addDefaultHead(key + ".NECTAR", "HP#bee_pollinated");
                    break;
                case "CREEPER":
                    addDefaultHead(key + ".default", "{mob-default}");
                    addDefaultHead(key + ".POWERED", "HP#charged_creeper");
                    break;
                case "CAT":
                    addDefaultHead(key + ".default", "HP#tabby_cat");
                    for (String type : Arrays.asList("tabby", "black", "red", "siamese", "british_shorthair", "calico"
                            , "persian", "ragdoll", "white", "jellie", "all_black")) {
                        addDefaultHead(key + "." + type.toUpperCase(), "HP#" + type + "_cat");
                    }
                    break;
                case "FOX":
                    addDefaultHead(key + ".default", "HP#fox_mc");
                    addDefaultHead(key + ".RED", "HP#fox_mc");
                    addDefaultHead(key + ".SNOW", "HP#snow_fox");
                    break;
                case "HORSE":
                    addDefaultHead(key + ".default", "HP#brown_horse");
                    for (Horse.Color variant : Horse.Color.values()) {
                        addDefaultHead(key + "." + variant.name(), "HP#" + variant.name().toLowerCase() + "_horse");
                    }
                    break;
                case "LLAMA":
                case "TRADER_LLAMA":
                    addDefaultHead(key + ".default", "HP#creamy_" + key.toLowerCase());
                    addDefaultHead(key + ".CREAMY", "HP#creamy_" + key.toLowerCase());
                    addDefaultHead(key + ".WHITE", "HP#white_" + key.toLowerCase());
                    addDefaultHead(key + ".BROWN", "HP#brown_" + key.toLowerCase());
                    addDefaultHead(key + ".GRAY", "HP#gray_" + key.toLowerCase());
                    break;
                case "MUSHROOM_COW":
                    addDefaultHead(key + ".default", "HP#red_mooshroom");
                    addDefaultHead(key + ".RED", "HP#red_mooshroom");
                    addDefaultHead(key + ".BROWN", "HP#brown_mooshroom");
                    break;
                case "OCELOT":
                    addDefaultHead(key + ".default", "HP#ocelot");
                    addDefaultHead(key + ".WILD_OCELOT", "HP#ocelot");
                    addDefaultHead(key + ".BLACK_CAT", "HP#black_cat");
                    addDefaultHead(key + ".RED_CAT", "HP#red_cat");
                    addDefaultHead(key + ".SIAMESE_CAT", "HP#siamese_cat");
                    break;
                case "PANDA":
                    for (String gene : Arrays.asList("NORMAL", "PLAYFUL", "LAZY", "WORRIED", "BROWN", "WEAK",
                            "AGGRESSIVE")) {
                        addDefaultHead(key + "." + gene, "HP#panda_" + gene.toLowerCase());
                    }
                    break;
                case "PARROT":
                    addDefaultHead(key + ".default", "HP#red_parrot");
                    for (String colour : new String[]{"RED", "BLUE", "GREEN", "CYAN", "GRAY"}) {
                        addDefaultHead(key + "." + colour, "HP#" + colour.toLowerCase() + "_parrot");
                    }
                    break;
                case "RABBIT":
                    addDefaultHead(key + ".default", "HP#brown_rabbit");
                    for (Rabbit.Type type : Rabbit.Type.values()) {
                        addDefaultHead(key + "." + type.name(), "HP#" + type.name().toLowerCase() + "_rabbit");
                    }
                    break;
                case "SHEEP":
                    addDefaultHead(key + ".default", "HP#white_sheep");
                    for (DyeColor dc : DyeColor.values()) {
                        try {
                            if (dc == DyeColor.valueOf("LIGHT_GRAY")) {
                                addDefaultHead(key + "." + dc.name(), "HP#silver_sheep");
                            } else {
                                addDefaultHead(key + "." + dc.name(), "HP#" + dc.name().toLowerCase() + "_sheep");
                            }
                        } catch (NoSuchFieldError | IllegalArgumentException ex) {
                            addDefaultHead(key + "." + dc.name(), "HP#silver_sheep");
                        }
                    }
                    break;
                case "TROPICAL_FISH":
                case "PIG_ZOMBIE":
                case "ELDER_GUARDIAN":
                case "ZOMBIE_HORSE":
                case "SKELETON_HORSE":
                    addDefaultHead(key + ".default", "HP#" + key.toLowerCase().replaceAll("_", ""));
                    break;
                case "VILLAGER":
                    addDefaultHead(key + ".default", "HP#villager_plains");
                    for (String biome : Arrays.asList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA",
                            "JUNGLE")) {
                        addDefaultHead(key + "." + biome, "HP#villager_" + biome.toLowerCase());
                    }
                    break;
                case "ZOMBIE_VILLAGER":
                    addDefaultHead(key + ".default", "HP#zombie_villager_plains");
                    for (String biome : Lists.newArrayList("DESERT", "PLAINS", "SAVANNA", "SNOW", "SWAMP", "TAIGA",
                            "JUNGLE")) {
                        addDefaultHead(key + "." + biome, "HP#zombie_villager_" + biome.toLowerCase());
                    }
                    break;
                case "GIANT":
                    addDefaultHead(key + ".default", "HP#zombie");
                    break;
                case "CHICKEN":
                    addDefaultHead(key + ".default", "HP#" + key.toLowerCase() + "_mc");
                    break;
                case "WITHER_SKELETON":
                    addDefaultHead(key + ".default", "{mob-default}");
                    addExample(key + ".default.{mob-default}.chance", 0);
                    break;
                case "ENDER_DRAGON":
                case "ZOMBIE":
                case "SKELETON":
                    addDefaultHead(key + ".default", "{mob-default}");
                    break;
                case "STRIDER":
                    addDefaultHead(key + ".COLD", "HP#cold_strider");
                default:
                    addDefaultHead(key + ".default", "HP#" + key.toLowerCase());
                    break;
            }
        }
    }

    private void addPlayerHeads() {
        makeSectionLenient("player");
        createConfigSection("player.default");
        addExample("player.default.display-name", "{player}'s head");
        addExample("player.default.lore", new ArrayList<>(Arrays.asList("&7Price: &6{price}", "&7Player: &a{player}")));

        addExample("player.Thatsmusic99.lore", Collections.singletonList("oi mate it's a bit rood to stab me innit?"));
    }

    public double getPrice(String type) {
        return getDouble(type + ".price", MainConfig.get().getMobDrops().DEFAULT_PRICE);
    }

    public String getPlayerDisplayName(String name) {
        String displayName = getString("player." + name + ".display-name", "");
        if (displayName.isEmpty()) {
            displayName = getString("player.display-name", getString("defaults.display-name"));
        }
        return ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("\\{player}", name));
    }

    public double getPlayerChance(String name) {
        return getDouble("player." + name + ".chance", MainConfig.get().getPlayerDrops().DEFAULT_DROP_CHANCE);
    }

    public double getPlayerPrice(String name) {
        return getDouble("player." + name + ".price", MainConfig.get().getPlayerDrops().DEFAULT_PRICE);
    }

    @Nullable
    public String getDisplayName(String path) {
        return getString(path + ".display-name", getString("defaults.display-name", null));
    }

    @Nullable
    public List<String> getLore(String type, String conditions) {
        List<String> lore = new ArrayList<>();
        List<String> configLore = getList(type + "." + conditions + ".lore", getList("defaults.lore", null));
        if (configLore == null) return null;
        for (String l : configLore) {
            HPUtils.parseLorePlaceholders(lore, ChatColor.translateAlternateColorCodes('&', l),
                    new HPUtils.PlaceholderInfo("{type}", HeadsPlus.capitalize(type.replaceAll("_", " ")), true),
                    new HPUtils.PlaceholderInfo("{price}", getPrice(type), HeadsPlus.get().isVaultEnabled()));
        }
        return lore;
    }

    public List<String> getPlayerLore(String name, double price, String killerName) {
        List<String> lore = new ArrayList<>();
        List<String> configLore = getList("player.lore", getStringList("defaults.lore"));

        for (String l : configLore) {
            HPUtils.parseLorePlaceholders(lore, ChatColor.translateAlternateColorCodes('&', l),
                    new HPUtils.PlaceholderInfo("{type}", "Player", true),
                    new HPUtils.PlaceholderInfo("{price}", price, true),
                    new HPUtils.PlaceholderInfo("{player}", name, true),
                    new HPUtils.PlaceholderInfo("{killer}", killerName, killerName != null));
        }
        return lore;
    }

    private void addDefaultHead(String path, String head) {
        makeSectionLenient(path);
        createExampleSection(path + "." + head);
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().MOB_DROPS || MainConfig.get().getMainFeatures().PLAYER_DROPS;
    }
}
