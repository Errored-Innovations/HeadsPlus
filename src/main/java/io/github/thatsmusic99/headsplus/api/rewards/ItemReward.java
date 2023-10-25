package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemReward extends Reward<ItemStack> {

    public ItemReward(ItemStack item, long xp) {
        super(item, xp);
    }

    @Deprecated
    public ItemReward(Material material, int amount, long xp) {
        this(new ItemStack(material, amount), xp);
    }

    public static ItemReward fromConfigSection(String id, ConfigSection section) {
        String materialStr = section.getString("material");
        if (materialStr == null)
            throw new IllegalStateException("Reward type GIVE_ITEM " + id + " must have a material option!");
        // If it's a mask or HP head...
        if (materialStr.startsWith("HP#") || materialStr.startsWith("HPM#")) {
            // Get the head in question
            HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(materialStr);
            int amount = section.getInteger("amount", 1);
            ItemStack itemStack = headInfo.forceBuildHead();
            itemStack.setAmount(amount);
            return new ItemReward(itemStack, section.getLong("base-xp"));
        } else {
            Material material = Material.getMaterial(materialStr);
            if (material == null) {
                throw new IllegalStateException("Reward type GIVE_ITEM " + id + " was given a material " + materialStr + " that was not found!");
            }
            int amount = section.getInteger("amount", 1);
            return new ItemReward(new ItemStack(material, amount), section.getLong("base-xp"));
        }

    }

    @Override
    public String getDefaultRewardString(Player player, int difficulty) {
        return MessagesManager.get().getString("inventory.icon.reward.item-give", player)
                .replace("{amount}", String.valueOf(multiplyRewardValues(difficulty).getAmount()))
                .replace("{item}", HeadsPlus.capitalize(reward.getType().name().replaceAll("_", " ")));
    }

    @Override
    public void rewardPlayer(@Nullable Challenge challenge, @NotNull Player player) {
        super.rewardPlayer(challenge, player);

        // If their inventory is full, drop the item so it's not entirely lost
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), reward);
            return;
        }
        ItemStack item = this.reward;
        if (isUsingMultiplier() && challenge != null) {
            item = this.reward.clone();
            item.setAmount(this.reward.getAmount() * challenge.getDifficulty());
        }
        player.getInventory().addItem(item);
    }

    @Override
    public ItemStack multiplyRewardValues(int multiplier) {
        ItemStack item = this.reward.clone();
        item.setAmount(this.reward.getAmount() * multiplier);
        return item;
    }
}
