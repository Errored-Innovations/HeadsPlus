package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

public class EntityDataManager {

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
}
