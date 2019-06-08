package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.headsx.HeadsPlusConfigHeadsX;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellheadInventory {

    private int page;
    private int pages;
    public static final HashMap<Player, SellheadInventory> pls = new HashMap<>();
    private int[] pos() {
        int[] a = new int[28];
        a[0] = 10;
        a[1] = 11;
        a[2] = 12;
        a[3] = 13;
        a[4] = 14;
        a[5] = 15;
        a[6] = 16;
        a[7] = 19;
        a[8] = 20;
        a[9] = 21;
        a[10] = 22;
        a[11] = 23;
        a[12] = 24;
        a[13] = 25;
        a[14] = 28;
        a[15] = 29;
        a[16] = 30;
        a[17] = 31;
        a[18] = 32;
        a[19] = 33;
        a[20] = 34;
        a[21] = 37;
        a[22] = 38;
        a[23] = 39;
        a[24] = 40;
        a[25] = 41;
        a[26] = 42;
        a[27] = 43;
        return a;
    }
    private int[] glass() {
        int[] a = new int[25];
        a[0] = 0;
        a[1] = 1;
        a[2] = 2;
        a[3] = 3;
        a[4] = 5;
        a[5] = 6;
        a[6] = 7;
        a[7] = 8;
        a[8] = 9;
        a[9] = 17;
        a[10] = 18;
        a[11] = 26;
        a[12] = 27;
        a[13] = 35;
        a[14] = 36;
        a[15] = 44;
        a[16] = 45;
        a[17] = 46;
        a[18] = 47;
        a[19] = 48;
        a[20] = 49;
        a[21] = 50;
        a[22] = 51;
        a[23] = 52;
        a[24] = 53;
        return a;
    }

    public int getPages() { return pages; }
    public int getPage() { return page; }

    public Inventory changePage(boolean next, boolean start, Player p) {
        HeadsPlus hp = HeadsPlus.getInstance();
        Inventory i;
        if (next) {
            page++;
        } else {
            page--;
        }
        if (start) {
            page = 1;
        }
        i = Bukkit.createInventory(null, 54, "HeadsPlus Sellhead menu");
        HeadsPlusConfigHeads hpch = hp.getHeadsConfig();
        List<String> s = new ArrayList<>();
        for (String o : hpch.mHeads) {
            if (o.equalsIgnoreCase("sheep")) {
                if (!hpch.getConfig().getStringList(o + ".name.default").isEmpty()) {
                    s.add(o);
                }
            } else if (!hpch.getConfig().getStringList(o + ".name").isEmpty()) {
                s.add(o);
            }
        }
        for (String o : hpch.uHeads) {
            if (o.equalsIgnoreCase("llama")
                    || o.equalsIgnoreCase("horse")
                    || o.equalsIgnoreCase("parrot")) {
                if (!hpch.getConfig().getStringList(o + ".name.default").isEmpty()) {
                    s.add(o);
                }
            }
            if (!hpch.getConfig().getStringList(o + ".name").isEmpty()) {
                s.add(o);
            }
        }
        PagedLists<String> ps = new PagedLists<>(s, 28);
        pages = ps.getTotalPages();
        int io = 0;
        NMSManager nms = hp.getNMS();
        NBTManager nbt = hp.getNBTManager();
        for (String o : ps.getContentsInPage(page)) {

            ItemStack it;
            SkullMeta sm;
            HeadsPlusConfigHeadsX hpchx = hp.getHeadsXConfig();
            try {
                if (hpchx.isHPXSkull(hpch.getConfig().getStringList(o + ".name").get(0))) {
                    it = hp.getHeadsXConfig().getSkull(hpch.getConfig().getStringList(o + ".name").get(0));
                    sm = (SkullMeta) it.getItemMeta();
                } else {
                    it = nms.getSkullMaterial(1);
                    sm = (SkullMeta) it.getItemMeta();
                    sm = nms.setSkullOwner(hpch.getConfig().getStringList(o + ".name").get(0), sm);
                }
            } catch (IndexOutOfBoundsException ex) {
                if (hpchx.isHPXSkull(hpch.getConfig().getStringList(o + ".name.default").get(0))) {
                    it = hp.getHeadsXConfig().getSkull(hpch.getConfig().getStringList(o + ".name.default").get(0));
                    sm = (SkullMeta) it.getItemMeta();
                } else {
                    it = nms.getSkullMaterial(1);
                    sm = (SkullMeta) it.getItemMeta();
                    sm = nms.setSkullOwner(hpch.getConfig().getStringList(o + ".name.default").get(0), sm);
                }
            }

            sm.setDisplayName(hpch.getDisplayName(o));
            List<String> d = new ArrayList<>();
            for (String a : hpch.getLore(o)) {
                d.add(ChatColor.translateAlternateColorCodes('&', a).replaceAll("\\{price}", String.valueOf(hpch.getPrice(o))).replaceAll("\\{type}", o));
            }
            sm.setLore(d);
            it.setItemMeta(sm);
            it = nbt.setType(it, o);
            try {
                if (hpch.getConfig().getStringList(o + ".name").get(0).equalsIgnoreCase("{mob-default}")) {
                    if (o.equalsIgnoreCase("skeleton")) {
                        it.setType(nms.getSkull(0).getType());
                    } else if (o.equalsIgnoreCase("witherskeleton")) {
                        it.setType(nms.getSkull(1).getType());
                    } else if (o.equalsIgnoreCase("zombie")) {
                        it.setType(nms.getSkull(2).getType());
                    } else if (o.equalsIgnoreCase("creeper")) {
                        it.setType(nms.getSkull(4).getType());
                    } else if (o.equalsIgnoreCase("enderdragon")) {
                        it.setType(nms.getSkull(5).getType());
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            i.setItem(pos()[io], it);
            ++io;
        }
        DyeColor dc;
        try {
            dc = DyeColor.valueOf(hp.getConfiguration().getMechanics().getString("gui-glass-color").toUpperCase());
        } catch (Exception e) {
            dc = DyeColor.values()[8];
        }
        ItemStack isi = nms.getColouredBlock(MaterialTranslator.BlockType.STAINED_GLASS_PANE, dc.ordinal());
        ItemMeta ims = isi.getItemMeta();
        ims.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6"));
        isi.setItemMeta(ims);
        for (int in : glass()) {
            i.setItem(in, isi);
        }
        if (pages > page) {
            setItem(i, "Next Page", 50);
        }
        if (page != 1) {
            setItem(i, "Back", 48);
        }
        ItemStack it = new ItemStack(Material.BARRIER);
        ItemMeta is = it.getItemMeta();
        is.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close Menu");
        it.setItemMeta(is);
        i.setItem(49, it);
        ItemStack is2 = new ItemStack(Material.PAPER);
        ItemMeta im = is2.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "[" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Stats" + ChatColor.GOLD + "]");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Total pages: " + pages);
        if (hp.econ()) {
            lore.add(ChatColor.GREEN + "Current balance: " + hp.getEconomy().getBalance(p));
        }
        im.setLore(lore);
        is2.setItemMeta(im);
        i.setItem(4, is2);
        return i;
    }

    public static SellheadInventory getSI(Player p) {
        return pls.get(p);
    }

    public static void setSI(Player p, SellheadInventory so) {
        pls.put(p, so);
    }

    private void setItem(Inventory i, String s, int o) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + s);
        item.setItemMeta(im);
        i.setItem(o, item);
    }

}
