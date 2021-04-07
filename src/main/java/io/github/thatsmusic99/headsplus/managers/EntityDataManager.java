package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EntityDataManager {

    public static final List<String> ableEntities = new ArrayList<>();


    private static final LinkedHashMap<String, List<EntityHead>> storedHeads = new LinkedHashMap<>();
    private static final LinkedHashMap<String, ItemStack> sellheadCache = new LinkedHashMap<>();

    public static void createEntityList() {
        for (EntityType type : EntityType.values()) {
            if (type.isAlive()) ableEntities.add(type.name());
        }
        Collections.sort(ableEntities);
    }
    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    storedHeads.clear();
                    sellheadCache.clear();
                    setupHeads();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HeadsPlus.getInstance());
    }

    public static LinkedHashMap<String, List<EntityHead>> getStoredHeads() {
        return storedHeads;
    }

    public static String getMeta(Entity entity) {
        NMSIndex index = HeadsPlus.getInstance().getNMSVersion();
        String result = "default";
        StringBuilder builder = new StringBuilder();
        switch (entity.getType().name()) {
            case "HORSE": {
                Horse horse = (Horse) entity;
                if (index.getOrder() < 6) {
                    builder.append(horse.getColor()).append(",");
                    builder.append(horse.getVariant()).append(",");
                } else {
                    builder.append(horse.getColor());
                }
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
                if (index.getOrder() > 10) {
                    builder.append(villager.getVillagerType()).append(",");
                }
                builder.append(villager.getProfession());
                break;
            }
            case "MUSHROOM_COW": {
                if (index.getOrder() > 10) {
                    builder.append(((MushroomCow) entity).getVariant());
                }
                break;
            }
            case "PANDA": {
                builder.append(((Panda) entity).getMainGene());
                break;
            }
            case "OCELOT": {
                if (index.getOrder() < 11) {
                    builder.append(((Ocelot) entity).getCatType());
                }
                break;
            }
            case "BEE": {
                Bee bee = (Bee) entity;
                builder.append(bee.getAnger() > 0 ? "ANGRY," : "").append(bee.hasNectar() ? "NECTAR" : "");
                break;
            }
            case "ZOMBIE_VILLAGER": {
                ZombieVillager zombie = (ZombieVillager) entity;
                if (index.getOrder() > 10) {
                    builder.append(zombie.getVillagerType()).append(",");
                }
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
                String fancyName;
                switch (name) {
                    case "WANDERING_TRADER":
                    case "TRADER_LLAMA":
                        fancyName = name.toLowerCase();
                        break;
                    default:
                        fancyName = name.toLowerCase().replaceAll("_", "");
                        break;
                }
                ConfigMobs headsCon = HeadsPlus.getInstance().getHeadsConfig();
                for (String conditions : ((ConfigurationSection) headsCon.getConfig().get(fancyName + ".name")).getKeys(false)) {
                    List<EntityHead> heads = new ArrayList<>();
                    for (String head : headsCon.getConfig().getStringList(name + ".name." + conditions)) {
                        EntityHead headItem;
                        if (head.equalsIgnoreCase("{mob-default}")) {
                            switch (name) {
                                case "WITHER_SKELETON":
                                    headItem = new EntityHead(name, 1);
                                    break;
                                case "ENDER_DRAGON":
                                    headItem = new EntityHead(name, 5);
                                    break;
                                case "ZOMBIE":
                                    headItem = new EntityHead(name, 2);
                                    break;
                                case "CREEPER":
                                    headItem = new EntityHead(name, 4);
                                    break;
                                case "SKELETON":
                                    headItem = new EntityHead(name, 0);
                                    break;
                                default:
                                    headItem = new EntityHead(name);
                                    break;
                            }
                        } else {
                            headItem = new EntityHead(name);
                        }
                        headItem.withDisplayName(headsCon.getDisplayName(fancyName))
                                .withPrice(headsCon.getPrice(fancyName))
                                .withLore(headsCon.getLore(fancyName));
                        if (head.startsWith("HP#")) {
                            headItem.withTexture(HeadsPlus.getInstance().getHeadsXConfig().getTextures(head));
                        } else {
                            headItem.withPlayerName(head);
                        }
                        heads.add(headItem);
                        sellheadCache.putIfAbsent(name, headItem.getItemStack());
                    }
                    storedHeads.put(name + ";" + conditions, heads);
                }
                storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
            } catch (Exception e) {
                HeadsPlus.getInstance().getLogger().severe("Error thrown when creating the head for " + name + ". If it's a custom head, please double check the name. (Error code: 6)");
                storedHeads.putIfAbsent(name + ";default", new ArrayList<>());
            }
        }
    }

    public static LinkedHashMap<String, ItemStack> getSellheadCache() {
        return sellheadCache;
    }
}
