package io.github.thatsmusic99.headsplus.managers;

import com.google.common.collect.Sets;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EntityDataManager {

    public static final List<String> ableEntities = new ArrayList<>();


    private static final LinkedHashMap<String, List<DroppedHeadInfo>> storedHeads = new LinkedHashMap<>();
    private static final HashSet<String> NOT_ALIVE = Sets.newHashSet("PLAYER", "ARMOR_STAND");

    public static void createEntityList() {
        Permission permission = Bukkit.getPluginManager().getPermission("headsplus.drops.*");
        if (permission == null) {
            permission = new Permission("headsplus.drops.*");
        }
        permission.getChildren().put("headsplus.drops.player", true);
        for (EntityType type : Registry.ENTITY_TYPE) {
            if (!type.isAlive()) continue;

            // who decided that an armor stand is alive?
            if (!NOT_ALIVE.contains(type.name())) {
                ableEntities.add(type.name());
                permission.getChildren().put("headsplus.drops." + type.name().toLowerCase(), true);
            }
        }
        Collections.sort(ableEntities);
    }

    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    storedHeads.clear();
                    setupHeads();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HeadsPlus.get());
    }

    public static LinkedHashMap<String, List<DroppedHeadInfo>> getStoredHeads() {
        return storedHeads;
    }

    public static String getMeta(Entity entity) {
        String result = "default";
        StringBuilder builder = new StringBuilder();
        switch (entity.getType().name()) {
            case "AXOLOTL":
                builder.append(((Axolotl) entity).getVariant());
                break;
            case "HORSE": {
                Horse horse = (Horse) entity;
                builder.append(horse.getColor());
                break;
            }
            case "SHEEP": {
                builder.append(((Sheep) entity).getColor());
                break;
            }
            case "RABBIT": {
                builder.append(((Rabbit) entity).getRabbitType());
                break;
            }
            case "LLAMA":
            case "TRADER_LLAMA": {
                builder.append(((Llama) entity).getColor());
                break;
            }
            case "PARROT": {
                builder.append(((Parrot) entity).getVariant());
                break;
            }
            case "TROPICAL_FISH": {
                TropicalFish fish = (TropicalFish) entity;
                builder.append(fish.getPattern()).append(",");
                builder.append(fish.getBodyColor()).append(",");
                builder.append(fish.getPatternColor());
                break;
            }
            case "FOX": {
                builder.append(((Fox) entity).getFoxType());
                break;
            }
            case "CAT": {
                builder.append(getUnstableKeyed(entity, "getCatType", "default"));
                break;
            }
            case "VILLAGER": {
                Villager villager = (Villager) entity;
                builder.append(extend(villager.getVillagerType())).append(",");
                builder.append(extend(villager.getProfession()));
                break;
            }
            case "MOOSHROOM":
            case "MUSHROOM_COW": {
                builder.append(((MushroomCow) entity).getVariant().name());
                break;
            }
            case "PANDA": {
                builder.append(((Panda) entity).getMainGene());
                break;
            }
            case "BEE": {
                Bee bee = (Bee) entity;
                builder.append(bee.getAnger() > 0 ? "ANGRY," : "").append(bee.hasNectar() ? "NECTAR" : "");
                break;
            }
            case "ZOMBIE_VILLAGER": {
                ZombieVillager zombie = (ZombieVillager) entity;
                builder.append(extend(zombie.getVillagerType())).append(",");

                // ignore the null check it's lying
                if (zombie.getVillagerProfession() != null) builder.append(extend(zombie.getVillagerProfession()));
                break;
            }
            case "CREEPER": {
                builder.append(((Creeper) entity).isPowered() ? "POWERED" : "");
                break;
            }
            case "STRIDER": {
                builder.append(entity.isOnGround() ? "COLD" : "");
                break;
            }
            case "FROG": {
                builder.append(((Frog) entity).getVariant().name());
                break;
            }
            case "WOLF": {
                builder.append(getUnstableKeyed(entity, "getVariant", "PALE"));
            }

        }
        if (builder.length() > 0) {
            if (builder.charAt(builder.length() - 1) == ',') builder.setLength(builder.length() - 1);
            result = builder.toString();
        }
        return result;
    }

    private static void setupHeads() {
        for (String name : ableEntities) {
            ConfigMobs headsCon = ConfigMobs.get();
            ConfigSection entitySection = headsCon.getConfigSection(name);
            if (entitySection == null) continue;
            for (String conditions : entitySection.getKeys(false)) {
                List<DroppedHeadInfo> heads = new ArrayList<>();
                ConfigSection conditionSection = entitySection.getConfigSection(conditions);
                if (conditionSection == null) continue;
                for (String head : conditionSection.getKeys(false)) {
                    try {
                        DroppedHeadInfo headInfo = new DroppedHeadInfo(HeadManager.get().getHeadInfo(head), head);

                        if (head.equalsIgnoreCase("{mob-default}")) {
                            switch (name) {
                                case "WITHER_SKELETON":
                                    headInfo.withMaterial(Material.WITHER_SKELETON_SKULL);
                                    break;
                                case "ENDER_DRAGON":
                                    headInfo.withMaterial(Material.DRAGON_HEAD);
                                    break;
                                case "ZOMBIE":
                                    headInfo.withMaterial(Material.ZOMBIE_HEAD);
                                    break;
                                case "CREEPER":
                                    headInfo.withMaterial(Material.CREEPER_HEAD);
                                    break;
                                case "SKELETON":
                                    headInfo.withMaterial(Material.SKELETON_SKULL);
                                    break;
                                case "PIGLIN":
                                    try {
                                        headInfo.withMaterial(Material.valueOf("PIGLIN_HEAD"));
                                    } catch (IllegalArgumentException ignored) {
                                        headInfo = new DroppedHeadInfo(HeadManager.get().getHeadInfo("HP#piglin"), head);
                                    }
                                    break;
                            }
                        }

                        String path = name + "." + conditions + "." + head;
                        String displayName = ConfigMobs.get().getDisplayName(path);
                        if (displayName != null) {
                            headInfo.withDisplayName(displayName.replaceAll("\\{type}",
                                    HeadsPlus.capitalize(name.replaceAll("_", " "))));
                        }

                        headInfo.withXP(path).withChance(path).withPrice(path).setUnique(path);

                        //headInfo.setLore(ConfigMobs.get().getLore(name, conditions, headInfo.price));

                        heads.add(headInfo);
                        SellableHeadsManager.get().registerPrice("mobs_" + name + ":" + conditions + ":" + head,
                                SellableHeadsManager.SellingType.HUNTING, headInfo.price);
                    } catch (NullPointerException ex) {
                        HeadsPlus.get().getLogger().warning("A potential configuration fault was found when creating " +
                                "the head " + head + " for entity " + name + " with conditions " + conditions + ":");
                        HeadsPlus.get().getLogger().warning(ex.getMessage());
                    } catch (Exception e) {
                        HeadsPlus.get().getLogger().warning("Error thrown when creating " +
                                "the head " + head + " for entity " + name + " with conditions " + conditions + ":");
                        storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
                        e.printStackTrace();
                    }
                    storedHeads.put(name + ";" + conditions, heads);
                }
                storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
            }
        }

        SellableHeadsManager.get().registerPrice("mobs_PLAYER", SellableHeadsManager.SellingType.HUNTING,
                MainConfig.get().getPlayerDrops().DEFAULT_PRICE);

        ConfigSection section = ConfigMobs.get().getConfigSection("player");
        if (section == null) return;
        for (String player : section.getKeys(false)) {
            // Get the player price
            double price = ConfigMobs.get().getDouble("player." + player + ".price", -1);
            if (price == -1) continue;
            // Register that price
            SellableHeadsManager.get().registerPrice("mobs_PLAYER;" + player, SellableHeadsManager.SellingType.HUNTING, price);
        }
    }

    private static @NotNull String getUnstableKeyed(final @NotNull Entity entity, final @NotNull String methodName,
                                             final @NotNull String defaultOption) {

        try {
            final Method method = entity.getClass().getMethod(methodName);
            final Object obj = method.invoke(entity);

            // If it's a keyed object
            if (obj instanceof Keyed) return extend((Keyed) obj);

            // If it's an enum
            if (obj instanceof Enum<?>) return ((Enum<?>) obj).name().toUpperCase();

        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            HeadsPlus.get().getLogger().throwing(EntityDataManager.class.getSimpleName(),
                    "getUnstableKeyed", e);
            HeadsPlus.get().getLogger().warning("Something went wrong fetching metadata from "
                    + entity.getClass().getSimpleName() + " using " + methodName + "!");
        }
        return defaultOption;
    }

    private static @NotNull String extend(final @NotNull Keyed keyed) {
        return keyed.getKey().getKey().toUpperCase();
    }

    public static class DroppedHeadInfo extends MaskManager.MaskInfo {

        private long xp;
        private final String id;
        private MaskManager.MaskInfo info;
        private double chance;
        private double price;
        private boolean unique;

        public DroppedHeadInfo(HeadManager.HeadInfo info, String id) {
            super();
            this.id = id;
            this.chance = MainConfig.get().getMobDrops().DEFAULT_DROP_CHANCE;
            this.price = MainConfig.get().getMobDrops().DEFAULT_PRICE;
            this.unique = false;
            this.withDisplayName(info.getDisplayName())
                    .withMaterial(info.getMaterial());
            if (info.getTexture() != null) withTexture(info.getTexture());
            setLore(info.getLore());
            xp = MainConfig.get().getMobDrops().DEFAULT_XP_GAINED;
            if (info instanceof MaskManager.MaskInfo) {
                this.info = (MaskManager.MaskInfo) info;
            }
        }

        public DroppedHeadInfo withXP(String path) {
            if (!ConfigMobs.get().contains(path + ".xp")) return this;
            xp = ConfigMobs.get().getLong(path + ".xp");
            return this;
        }

        public DroppedHeadInfo withChance(String path) {
            if (!ConfigMobs.get().contains(path + ".chance")) return this;
            chance = ConfigMobs.get().getDouble(path + ".chance");
            return this;
        }

        public DroppedHeadInfo withPrice(String path) {
            if (!ConfigMobs.get().contains(path + ".price")) return this;
            price = ConfigMobs.get().getDouble(path + ".price");
            return this;
        }

        public DroppedHeadInfo setUnique(String path) {
            if (!ConfigMobs.get().containsKey(path + ".unique")) return this;
            this.unique = ConfigMobs.get().getBoolean(path + ".unique");
            return this;
        }

        public long getXp() {
            return xp;
        }

        public String getId() {
            return id;
        }

        public double getChance() {
            return chance;
        }

        public double getPrice() {
            return price;
        }

        public boolean isUnique() {
            return unique;
        }

        @Override
        public CompletableFuture<ItemStack> buildHead() {
            return info != null ? info.buildHead() : super.buildHead();
        }

        @Override
        public void run(Player player) {
            if (info != null) info.run(player);
        }
    }
}
