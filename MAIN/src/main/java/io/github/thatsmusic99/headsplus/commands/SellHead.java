package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "sellhead",
        permission = "headsplus.sellhead",
        subcommand = "sellead",
        maincommand = false,
        usage = "/sellhead [All|Entity|#] [#]"
)
public class SellHead implements CommandExecutor, IHeadsPlusCommand {

	private final HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
	private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
	private final List<String> soldHeads = new ArrayList<>();
	private final HashMap<String, Integer> hm = new HashMap<>();
    private final String disabled = hpc.getString("commands.errors.disabled");
    private final HashMap<String, Boolean> tests = new HashMap<>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {

            HeadsPlus hp = HeadsPlus.getInstance();
		    tests.clear();
            hp.debug("- Starting tests for command: sellhead!", 1);
			if (sender instanceof Player) {
                tests.put("Instance of Player", true);
			    if (hp.canSellHeads()) {
			        tests.put("Enabled", true);
			        Player p = (Player) sender;
			        soldHeads.clear();
			        hm.clear();
			        ItemStack invi = checkHand(p);
                    if (args.length == 0 && (sender.hasPermission("headsplus.sellhead"))) { // If sold via hand
                        tests.put("No arguments", true);
                        if (hp.getConfiguration().getMechanics().getBoolean("sellhead-gui")) {
							InventoryManager.getOrCreate(p).showScreen(InventoryManager.Type.SELL);
                            printDebugResults(tests, true);
                            return true;
                        } else {
                            if (invi != null) {
                                if (nbt().isSellable(invi)) {
                                    String s = nbt().getType(invi).toLowerCase();
                                    if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
                                        Double price;
                                        if (invi.getAmount() > 0) {
                                            price = invi.getAmount() * nbt().getPrice(invi);
                                            soldHeads.add(s);
                                            hm.put(s, invi.getAmount());
                                            SellHeadEvent she = new SellHeadEvent(price, soldHeads, (Player) sender, HeadsPlus.getInstance().getEconomy().getBalance((Player) sender), HeadsPlus.getInstance().getEconomy().getBalance((Player) sender) + price, hm);
                                            Bukkit.getServer().getPluginManager().callEvent(she);
                                            if (!she.isCancelled()) {
                                                EconomyResponse zr = HeadsPlus.getInstance().getEconomy().depositPlayer((Player) sender, price);
                                                String success = hpc.getString("commands.sellhead.sell-success").replaceAll("\\{price}", Double.toString(price)).replaceAll("\\{balance}", HeadsPlus.getInstance().getConfiguration().fixBalanceStr(zr.balance));
                                                if (zr.transactionSuccess()) {

                                                    if (price > 0) {
                                                        itemRemoval((Player) sender, args, -1);
                                                        sender.sendMessage(success);
                                                        printDebugResults(tests, true);
                                                        return true;

                                                    }
                                                } else {
                                                    sender.sendMessage(hpc.getString("commands.errors.cmd-fail"));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    sender.sendMessage(hpc.getString("commands.sellhead.false-head"));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(hpc.getString("commands.sellhead.false-head"));
                                return true;
                            }
                        }
                    } else {

                        if (!sender.hasPermission("headsplus.sellhead")) {
                            tests.put("No permission", true);
                            sender.sendMessage(hpc.getString("commands.errors.no-perm"));
                        } else if (args.length > 0) {
                            tests.put("No permission", false);
                            tests.put("No arguments", false);
                            if (args[0].equalsIgnoreCase("all")) {

                                tests.put("Selling all", true);
                                sellAll(p, args);
                            } else {
                                tests.put("Selling all", false);
                                double price = 0.0;
                                int limit = -1;
                                if (args.length > 1) {
                                    if (args[1].matches("^[0-9]+$")) {
                                        limit = Integer.parseInt(args[1]);
                                    }
                                }
                                int is = 0;
                                for (ItemStack i : p.getInventory()) {
                                    if (i != null) {
                                    //    boolean found = false;
                                        if (nbt().isSellable(i)) {
                                            String st = nbt().getType(i).toLowerCase();
                                            if (st.equalsIgnoreCase(args[0])) {
                                                if (is != limit) {
                                                    price = setPrice(price, args, i, p, limit);
                                                    ++is;
                                                }

                                            }
                                        }
                                    }
                                }
                                if (HeadsPlus.getInstance().getNMSVersion().getOrder() < 4) {
                                    ItemStack i = p.getInventory().getHelmet();
                                    if (i != null) {
                                        if (nbt().isSellable(i)) {
                                            String st = nbt().getType(i).toLowerCase();
                                            if (st.equalsIgnoreCase(args[0])) {
                                                if (is != limit) {
                                                    price = setPrice(price, args, i, p, limit);
                                                }
                                            }
                                        }
                                    }
                                }

                                ItemStack is2 = nms().getOffHand(p);
                                if (is2 != null) {
                                    if (nbt().isSellable(is2)) {
                                        String st = nbt().getType(is2).toLowerCase();
                                        if (st.equalsIgnoreCase(args[0])) {
                                            if (is != limit) {
                                                price = setPrice(price, args, is2, p, limit);
                                            }
                                        }
                                    }
                                }
                                if (price == 0.0) {
                                    tests.put("Any heads", false);
                                    sender.sendMessage(hpc.getString("commands.sellhead.no-heads"));
                                    printDebugResults(tests, false);
                                    return true;
                                }
                                tests.put("Any heads", true);
                                pay(p, args, price, limit);
                                return true;
                            }
                        } else {
                            tests.put("No permission", false);
                            tests.put("No arguments", true);
                            String falseItem = hpc.getString("commands.sellhead.false-item");
                            sender.sendMessage(falseItem);
                        }
                    }
                } else {
                    sender.sendMessage(disabled);
                }
            } else {
                tests.put("Instance of Player", false);
                sender.sendMessage("[HeadsPlus] You must be a player to run this command!");
            }
        } catch (Exception e) {
		    DebugPrint.createReport(e, "Command (sellhead)", true, sender);
		}
		printDebugResults(tests, false);
        return false;
	}

	@SuppressWarnings("deprecation")
    private static ItemStack checkHand(Player p) {
		if (Bukkit.getVersion().contains("1.8")) {
			return p.getInventory().getItemInHand();
		} else {
			return p.getInventory().getItemInMainHand();
		}
	}
	@SuppressWarnings("deprecation")
	private void setHand(Player p, ItemStack i) {
		if (Bukkit.getVersion().contains("1.8")) {
			p.getInventory().setItemInHand(i);
		} else {
			p.getInventory().setItemInMainHand(i);
		}
	}
	private void itemRemoval(Player p, String[] a, int limit) {
	    int l = limit;
		if (a.length > 0) {
		    if (p.getInventory().getHelmet() != null) {
		        ItemStack is = p.getInventory().getHelmet();
		        if (nbt().isSellable(is) && !nbt().getType(is).isEmpty()) {
                    if (a[0].equalsIgnoreCase("all") || nbt().getType(is).equalsIgnoreCase(a[0])) {
                        tests.put("Remove helmet", true);
                        if (is.getAmount() > l && l != -1) {
                            is.setAmount(is.getAmount() - l);
                            l = 0;
                        } else {
                            p.getInventory().setHelmet(new ItemStack(Material.AIR));
                            HPPlayer hp = HPPlayer.getHPPlayer(p);
                            hp.clearMask();
                            if (l != -1) {
                                l = is.getAmount() - l;
                            }
                        }
                    }
                }

		    }
            if (nms().getOffHand(p) != null) {
                ItemStack is = nms().getOffHand(p);
                tests.put("Off hand", nms().getOffHand(p).isSimilar(is));
                if (nbt().isSellable(is) && !nbt().getType(is).isEmpty()) {
                    if (a[0].equalsIgnoreCase("all") || nbt().getType(is).equalsIgnoreCase(a[0])) {
                        if (is.getAmount() > l && l != -1) {
                            is.setAmount(is.getAmount() - l);
                            l = 0;
                        } else {
                            p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                            if (l != -1) {
                                l = is.getAmount() - l;
                            }
                        }
                    }
                }
            }
				for (ItemStack is : p.getInventory()) {
					if (is != null) {
					    if (nbt().isSellable(is) && !nbt().getType(is).isEmpty()) {
					        if (!a[0].equalsIgnoreCase("all")) {
					            if (!nbt().getType(is).equalsIgnoreCase(a[0])) continue;
                            }

					        if (is.getAmount() > l && l != -1) {
					            is.setAmount(is.getAmount() - l);
					            l = 0;
                            } else if (l > 0) {
                                p.getInventory().remove(is);
                                l = l - is.getAmount();
                                if (l <= -1) {
                                    l = 0;
                                }
                            } else if (l == -1){
                                p.getInventory().remove(is);

                            }

					    }
					}
				}
		} else {
		    setHand(p, new ItemStack(Material.AIR));
		}
	}
	private double setPrice(Double p, String[] a, ItemStack i, Player pl, int limit) {
		if (a.length > 0) { // More than one argument
			if (!a[0].matches("^[0-9]+$")) { // More than one head
				if (a[0].equalsIgnoreCase("all")) { // Sell everything
				    if (nbt().isSellable(i)) {
				        String s = nbt().getType(i).toLowerCase();
				        if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
				            soldHeads.add(s);
				            int o = i(s, i.getAmount(), limit, false);
				            p += o * nbt().getPrice(i);
                        }
				    }
				} else { // Selected mob
				    p = f(i, p, a[0], limit);
				}
			} else {
			    if (Integer.parseInt(a[0]) <= i.getAmount()) {
					if (nbt().isSellable(i)) {
					    String s = nbt().getType(i);
					    if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
                            p = nbt().getPrice(i) * Integer.parseInt(a[0]);
                            soldHeads.add(s);
                            i(s, i.getAmount(), limit, false);
                        }
                    }
				} else {
					pl.sendMessage(hpc.getString("commands.sellhead.not-enough-heads"));
				}
			}
		}
		return p;
	}
	private void sellAll(Player p, String[] a) {
		Double price = 0.0;

        if (HeadsPlus.getInstance().getNMSVersion().getOrder() < 4) {
            ItemStack i = p.getInventory().getHelmet();
            if (i != null) {
                if (nbt().isSellable(i)) {
                    price = setPrice(price, a, i, p, -1);
                }
            }
        }

    /*    ItemStack is2 = nms().getOffHand(p);
        if (is2 != null) {
            if (nms().isSellable(is2)) {
                price = setPrice(price, a, is2, p, -1);
            }
        } */
		for (ItemStack is : p.getInventory()) {
            if (is != null) {
                price = setPrice(price, a, is, p, -1);

            }
        }

        if (price == 0) {
            p.sendMessage(hpc.getString("commands.sellhead.no-heads"));
            return;
        }

		pay(p, a, price, -1);
	}
	private void pay(Player p, String[] a, double pr, int limit) {
		Economy econ = HeadsPlus.getInstance().getEconomy();
		SellHeadEvent she = new SellHeadEvent(pr, soldHeads, p, econ.getBalance(p), econ.getBalance(p) + pr, hm);
		Bukkit.getServer().getPluginManager().callEvent(she);
		tests.put("Event cancelled", she.isCancelled());
		if (!she.isCancelled()) {

            EconomyResponse zr = econ.depositPlayer(p, pr);
            String success = hpc.getString("commands.sellhead.sell-success").replaceAll("\\{price}", Double.toString(pr)).replaceAll("\\{balance}", HeadsPlus.getInstance().getConfiguration().fixBalanceStr(zr.balance));

            if (zr.transactionSuccess()) {
                tests.put("Transaction success", true);
                itemRemoval(p, a, limit);
                p.sendMessage(success);
            } else {
                tests.put("Transaction success", false);
                p.sendMessage(hpc.getString("commands.errors.cmd-fail"));
                printDebugResults(tests, false);
            }
        }
	}

    private Double f(ItemStack i, Double p, String s, int l) {
	    String st = nbt().getType(i).toLowerCase();
	    if (nbt().isSellable(i)) {
	        if (st.equalsIgnoreCase(s)) {
	            soldHeads.add(s);
	            int o = i(s, i.getAmount(), l, true);
	            p = (o * nbt().getPrice(i));

            }
        }
        return p;
    }

    private int i(String s, int amount, int l, boolean g) {
	    if (hm.get(s) == null) {
	        if (amount > l && l != -1) {
	            hm.put(s, l);
	            return l;
            } else {
                hm.put(s, amount);
                return amount;
            }
        }
	    if (hm.get(s) > 0) {
	        int i = hm.get(s);
	        i += amount;
	        if (i > l && l != -1) {
	            hm.put(s, l);
	            return l;
            } else {
                hm.put(s, i);
                return g ? i : amount;
            }

        } else {
            if (amount > l && l != -1) {
                hm.put(s, l);
                return l;
            } else {
                hm.put(s, amount);
                return amount;
            }
        }
    }

    @Override
    public String getCmdDescription() {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.sellhead");
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            if (args[1].matches("^[A-Za-z0-9_]+$")) {
                h.put(true, "");
            } else {
                h.put(false, hpc.getString("commands.head.alpha-names"));
            }
        } else {
            h.put(false, hpc.getString("commands.errors.invalid-args"));
        }

        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    private NMSManager nms() {
	    return HeadsPlus.getInstance().getNMS();
    }

    private NBTManager nbt() {
	    return HeadsPlus.getInstance().getNBTManager();
    }
}