package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.managers.SellableHeadsManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@CommandInfo(
        commandname = "sellhead",
        permission = "headsplus.sellhead",
        maincommand = false,
        usage = "/sellhead [All|Head ID] [#]",
        descriptionPath = "descriptions.sellhead"
)
public class SellHead implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final MessagesManager hpc = MessagesManager.get();
    private final int[] slots;

    public SellHead() {
        slots = new int[45];
        slots[44] = 45; // off-hand slot
        for (int i = 0; i < 44; i++) {
            slots[i] = i;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             @NotNull String[] args) {
        try {
            if (!(MainConfig.get().getMainFeatures().SELL_HEADS && HeadsPlus.get().isVaultEnabled())) {
                hpc.sendMessage("commands.errors.disabled", sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                hpc.sendMessage("commands.errors.not-a-player", sender);
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 0) {
                // Open the GUI
                if (MainConfig.get().getSellingHeads().USE_GUI && player.hasPermission("headsplus.sellhead.gui")) {
                    HashMap<String, String> context = new HashMap<>();
                    context.put("section", "mobs");
                    InventoryManager.getManager(player).open(InventoryManager.InventoryType.SELLHEAD_CATEGORY, context);
                    return true;
                } else {
                    // Get the item in the player's hand
                    ItemStack item = player.getInventory().getItemInMainHand();
                    // If the item exists and is sellable,
                    if (!PersistenceManager.get().isSellable(item)) {
                        MessagesManager.get().sendMessage("commands.sellhead.false-head", player);
                        return true;
                    }
                    // Get the ID
                    String id = PersistenceManager.get().getSellType(item);
                    if (!SellableHeadsManager.get().isRegistered(id)) {
                        MessagesManager.get().sendMessage("commands.sellhead.false-head", player);
                        return true;
                    }
                    double price = SellableHeadsManager.get().getPrice(id) * item.getAmount();
                    if (PersistenceManager.get().hasSellPrice(item)) {
                        price = PersistenceManager.get().getSellPrice(item) * item.getAmount();
                    }
                    SellData data = new SellData(player);
                    data.addID(id, item.getAmount());
                    data.addSlot(player.getInventory().getHeldItemSlot(), item.getAmount());
                    pay(player, data, price);
                }
            } else {
                if (args[0].equalsIgnoreCase("all")) {
                    getValidHeads(player, null, -1);
                } else if (CachedValues.MATCH_PAGE.matcher(args[0]).matches()) {
                    getValidHeads(player, null, Integer.parseInt(args[0]));
                } else {
                    int limit = -1;
                    if (CachedValues.MATCH_PAGE.matcher(args[args.length - 1]).matches()) {
                        limit = HPUtils.isInt(args[args.length - 1]);
                        args = Arrays.copyOfRange(args, 0, args.length - 1);
                    }
                    getValidHeads(player, getIDs(args), limit);
                }
            }

        } catch (NumberFormatException e) {
            hpc.sendMessage("commands.errors.invalid-input-int", sender);
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (sellhead)", true, sender);
        }
        return false;
    }

    public void getValidHeads(Player player, HashSet<String> ids, int limit) {
        double price = 0;
        SellData data = new SellData(player);
        for (int slot : slots) {
            ItemStack item = player.getInventory().getItem(slot);
            if (slot == player.getInventory().getSize() - 2) continue;
            if (item == null
                    || !(item.getItemMeta() instanceof SkullMeta)
                    || !PersistenceManager.get().isSellable(item)) continue;
            double headPrice;
            String id = PersistenceManager.get().getSellType(item);
            if (ids != null) {
                if (!ids.contains(id)) continue;
            }

            if (PersistenceManager.get().hasSellPrice(item)) {
                headPrice = PersistenceManager.get().getSellPrice(item);
            } else if (SellableHeadsManager.get().isRegistered(id)) {
                headPrice = SellableHeadsManager.get().getPrice(id);
            } else {
                continue;
            }

            if (limit <= item.getAmount() && limit != -1) {
                data.addSlot(slot, limit);
                data.addID(id, limit);
                price += headPrice * limit;
                break;
            } else {
                data.addSlot(slot, item.getAmount());
                data.addID(id, item.getAmount());
                price += headPrice * item.getAmount();
                if (limit != -1) {
                    limit -= item.getAmount();
                }
            }
        }
        if (price > 0) {
            pay(player, data, price);
        } else {
            hpc.sendMessage("commands.sellhead.no-heads", player);
        }
    }

    private void pay(Player player, SellData data, double price) {
        double balance = HeadsPlus.get().getEconomy().getBalance(player);
        SellHeadEvent event = new SellHeadEvent(price, player, balance, balance + price, data.ids);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            EconomyResponse response = HeadsPlus.get().getEconomy().depositPlayer(player, price);
            if (response.transactionSuccess()) {
                if (price == 0) return;
                removeItems(player, data);
                hpc.sendMessage("commands.sellhead.sell-success", player, "{price}",
                        MainConfig.get().fixBalanceStr(price), "{balance}",
                        MainConfig.get().fixBalanceStr(balance + price));
            } else {
                hpc.sendMessage("commands.errors.cmd-fail", player);
            }
        }
    }

    public static class SellData {
        private final HashMap<String, Integer> ids = new HashMap<>();
        private final HashMap<Integer, Integer> slots = new HashMap<>();
        private final UUID player;

        public SellData(Player player) {
            this.player = player.getUniqueId();
        }

        public HashMap<String, Integer> getIds() {
            return ids;
        }

        public UUID getPlayer() {
            return player;
        }

        public void addID(String id, int amount) {
            if (ids.containsKey(id)) {
                int currAmount = ids.get(id);
                ids.put(id, currAmount + amount);
            } else {
                ids.put(id, amount);
            }
        }

        public void addSlot(int slot, int amount) {
            slots.put(slot, amount);
        }
    }

    private void removeItems(Player player, SellData data) {
        for (int slot : data.slots.keySet()) {
            ItemStack item = player.getInventory().getItem(slot);
            int limit = data.slots.get(slot);
            if (item == null) return;
            if (item.getAmount() > limit && limit != -1) {
                item.setAmount(item.getAmount() - limit);
                break;
            } else {
                player.getInventory().setItem(slot, new ItemStack(Material.AIR));
            }
        }
    }

    private HashSet<String> getIDs(String[] args) {
        HashSet<String> ids = new HashSet<>();
        if (args.length == 1) {
            String mobId = args[0].toUpperCase();
            ids.add("crafting_" + args[0]);
            if (mobId.equals("PLAYER")) {
                ids.add("mobs_PLAYER");
                return ids;
            }
            if (!EntityDataManager.ableEntities.contains(mobId)) return ids;
            for (String condition : ConfigMobs.get().getConfigSection(mobId).getKeys(false)) {
                for (EntityDataManager.DroppedHeadInfo head :
                        EntityDataManager.getStoredHeads().get(mobId + ";" + condition)) {
                    String id = "mobs_" + mobId + ":" + condition + ":" + head.getId();
                    ids.add(id);
                }
            }

        } else if (args.length == 2) {
            if (args[1].equals("crafting")) {
                ids.add("crafting_" + args[0]);
            } else if (args[1].equals("mobs")) {
                String mobId = args[0].toUpperCase();
                if (mobId.equals("PLAYER")) {
                    ids.add("mobs_PLAYER");
                    return ids;
                }
                for (String condition : ConfigMobs.get().getConfigSection(mobId).getKeys(false)) {
                    for (EntityDataManager.DroppedHeadInfo head :
                            EntityDataManager.getStoredHeads().get(mobId + ";" + condition)) {
                        String id = "mobs_" + mobId + ":" + condition + ":" + head.getId();
                        ids.add(id);
                    }
                }
            }
        } else {
            if (!args[2].startsWith("HP#") && !args[2].equals("{mob-default}")) args[2] = "HP#" + args[2];
            if (args[1].equals("mobs")) {
                for (String condition : ConfigMobs.get().getConfigSection(args[0].toUpperCase()).getKeys(false)) {
                    ids.add(args[1] + "_" + args[0].toUpperCase() + ":" + condition + ":" + args[2]);
                }
            } else {
                ids.add(args[1] + "_" + args[0] + args[2]);
            }
        }
        return ids;
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().SELL_HEADS;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            List<String> headIdList = new ArrayList<>(SellableHeadsManager.get().getKeys());
            headIdList.add("all");
            headIdList.add("PLAYER");
            headIdList.addAll(EntityDataManager.ableEntities);
            StringUtil.copyPartialMatches(args[0], headIdList, suggestions);
        } else if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], Arrays.asList("crafting", "mobs"), suggestions);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], HeadManager.get().getKeys(), suggestions);
        }
        Collections.sort(suggestions);
        return suggestions;
    }
}
