package io.github.thatsmusic99.headsplus.managers;

import com.google.common.collect.Sets;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

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
        for (EntityType type : EntityType.values()) {
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
                builder.append(((Cat) entity).getCatType());
                break;
            }
            case "VILLAGER": {
                Villager villager = (Villager) entity;
                builder.append(villager.getVillagerType()).append(",");
                builder.append(villager.getProfession());
                break;
            }
            case "MUSHROOM_COW": {
                builder.append(((MushroomCow) entity).getVariant());
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
                builder.append(zombie.getVillagerType()).append(",");
                builder.append(zombie.getVillagerProfession());
                break;
            }
            case "CREEPER": {
                builder.append(((Creeper) entity).isPowered() ? "CHARGED" : "");
                break;
            }
            case "STRIDER": {
                builder.append(entity.isOnGround() ? "COLD" : "");
                break;
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
            try {
                ConfigMobs headsCon = ConfigMobs.get();
                ConfigSection entitySection = headsCon.getConfigSection(name);
                if (entitySection == null) continue;
                for (String conditions : entitySection.getKeys(false)) {
                    List<DroppedHeadInfo> heads = new ArrayList<>();
                    ConfigSection conditionSection = entitySection.getConfigSection(conditions);
                    if (conditionSection == null) continue;
                    for (String head : conditionSection.getKeys(false)) {
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
                            }
                        }

                        String path = name + "." + conditions + "." + head;
                        String displayName = ConfigMobs.get().getDisplayName(path);
                        if (displayName != null) {
                            headInfo.withDisplayName(displayName.replaceAll("\\{type}",
                                    HeadsPlus.capitalize(name.replaceAll("_", " "))));
                        }

                        headInfo.withXP(path).withChance(path);

                        headInfo.setLore(ConfigMobs.get().getLore(name, conditions));

                        heads.add(headInfo);
                        SellableHeadsManager.get().registerPrice("mobs_" + name + ":" + conditions + ":" + head,
                                SellableHeadsManager.SellingType.HUNTING, ConfigMobs.get().getPrice(path));
                    }
                    storedHeads.put(name + ";" + conditions, heads);
                }
                storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
            } catch (Exception e) {
                HeadsPlus.get().getLogger().severe("Error thrown when creating the head for " + name + ". If it's a " +
                        "custom head, please double check the name. (Error code: 6)");
                storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
                e.printStackTrace();
            }
        }

        SellableHeadsManager.get().registerPrice("mobs_PLAYER", SellableHeadsManager.SellingType.HUNTING,
                ConfigMobs.get().getDouble("player.default.price", ConfigMobs.get().getDouble("defaults.price")));
    }

    public static class DroppedHeadInfo extends MaskManager.MaskInfo {

        private long xp;
        private final String id;
        private MaskManager.MaskInfo info;
        private double chance;

        public DroppedHeadInfo(HeadManager.HeadInfo info, String id) {
            super();
            this.id = id;
            this.chance = MainConfig.get().getMobDrops().DEFAULT_DROP_CHANCE;
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

        public long getXp() {
            return xp;
        }

        public String getId() {
            return id;
        }

        public double getChance() {
            return chance;
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
