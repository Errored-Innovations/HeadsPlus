package io.github.thatsmusic99.headsplus.config.customheads.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.SectionChangeEvent;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeSection implements Icon {

    @Override
    public String getIconName() {
        return "challenges-section";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
        String section = HeadsPlus.getInstance().getNBTManager().getChallengeSection(e.getCurrentItem());
        SectionChangeEvent event = new SectionChangeEvent(p, im.getSection(), section);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            im.showChallengeSection(section);
        }
    }

    @Override
    public Material getDefaultMaterial() {
        return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 5).getType();
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>(Arrays.asList("{section-lore}", "{msg_inventory.icon.challenge.count}"));
    }

    @Override
    public String getDefaultDisplayName() {
        return "{section-name}";
    }

    @Override
    public String getSingleLetter() {
        return "S";
    }

    @Override
    public String[] getExtraLetters() {
        String[] s = new String[10];
        s[0] = "E";
        s[1] = "R";
        s[2] = "Z";
        s[3] = "V";
        s[4] = "J";
        s[5] = "T";
        s[6] = "I";
        s[7] = "P";
        s[8] = "O";
        s[9] = "D";
        return s;
    }

    public static class Easy implements Icon {

        @Override
        public String getIconName() {
            return "challenges-easy";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("easy");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 13).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&a&lEasy&8]";
        }

        @Override
        public String getSingleLetter() {
            return "E";
        }
    }

    public static class EasyMedium implements Icon {

        @Override
        public String getIconName() {
            return "challenges-easy-medium";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("easy_medium");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 5).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&a&lEasy&8-&6&lMedium&8]";
        }

        @Override
        public String getSingleLetter() {
            return "R";
        }
    }

    public static class Medium implements Icon {

        @Override
        public String getIconName() {
            return "challenges-medium";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("medium");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 4).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&6&lMedium&8]";
        }

        @Override
        public String getSingleLetter() {
            return "Z";
        }
    }

    public static class MediumHard implements Icon {

        @Override
        public String getIconName() {
            return "challenges-medium-hard";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("medium_hard");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 1).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&6&lMedium&8-&c&lHard&8]";
        }

        @Override
        public String getSingleLetter() {
            return "V";
        }
    }

    public static class Hard implements Icon {

        @Override
        public String getIconName() {
            return "challenges-hard";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("hard");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&c&lHard&8]";
        }

        @Override
        public List<String> getLore() {
            return new ArrayList<>();
        }

        @Override
        public String getSingleLetter() {
            return "J";
        }
    }

    public static class Tedious implements Icon {

        @Override
        public String getIconName() {
            return "challenges-tedious";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("tedious");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 11).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&c&lTedious&8]";
        }

        @Override
        public String getSingleLetter() {
            return "T";
        }
    }

    public static class TediousPainful implements Icon {

        @Override
        public String getIconName() {
            return "challenges-tedious-painful";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("tedious-painful");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 10).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&c&lTedious&8-&5&lPainful&8]";
        }

        @Override
        public String getSingleLetter() {
            return "I";
        }
    }

    public static class Painful implements Icon {

        @Override
        public String getIconName() {
            return "painful";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("tedious-painful");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 2).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&5&lPainful&8]";
        }

        @Override
        public String getSingleLetter() {
            return "P";
        }
    }

    public static class PainfulDeadly implements Icon {

        @Override
        public String getIconName() {
            return "painful-deadly";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("painful-deadly");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 6).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&5Painful&8-&4Deadly&8]";
        }

        @Override
        public String getSingleLetter() {
            return "O";
        }
    }

    public static class Deadly implements Icon {

        @Override
        public String getIconName() {
            return "deadly";
        }

        @Override
        public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
            e.setCancelled(true);
            im.showChallengeSection("deadly");
        }

        @Override
        public Material getDefaultMaterial() {
            return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.TERRACOTTA, 14).getType();
        }

        @Override
        public List<String> getDefaultLore() {
            return new ArrayList<>();
        }

        @Override
        public String getDefaultDisplayName() {
            return "&8[&4Deadly&8]";
        }

        @Override
        public String getSingleLetter() {
            return "D";
        }
    }
}
