package io.github.thatsmusic99.headsplus.config.customheads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.icons.*;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

public interface Icon {

    String getIconName();

    void onClick(Player p, InventoryManager im, InventoryClickEvent e);

    Material getDefaultMaterial();

    List<String> getDefaultLore();

    String getDefaultDisplayName();

    default Icon getReplacementIcon() {
        return new Glass();
    }

    static Icon getIconFromName(String s) {
        for (Icon i : icons) {
            if (i.getIconName().equalsIgnoreCase(s) && !(i instanceof Air)) {
                return i;
            }
        }
        return null;
    }

    static List<Icon> icons = Arrays.asList(
        new Challenge(),
        new Close(),
        new Favourites(),
        new Glass(),
        new Head(),

        new Nav(Nav.Page.MENU, "Main Menu", Material.NETHER_STAR),
        new Nav(Nav.Page.START, "First Page", Material.ARROW),
        new Nav(Nav.Page.LAST, "Last Page", Material.ARROW),
        new Nav(Nav.Page.BACK, "Back", Material.ARROW),
        new Nav(Nav.Page.BACK_2, "Back 2", Material.ARROW),
        new Nav(Nav.Page.BACK_3, "Back 3", Material.ARROW),
        new Nav(Nav.Page.NEXT, "Next", Material.ARROW),
        new Nav(Nav.Page.NEXT_2, "Next 2", Material.ARROW),
        new Nav(Nav.Page.NEXT_3, "Next 3", Material.ARROW),

        new Search(),
        new Stats(),
        new Air(),
        new HeadSection(),
        new ChallengeSection()
    );

    static Icon getIconFromSingleLetter(String s) {
        for (Icon i : icons) {
            if (i.getSingleLetter().equalsIgnoreCase(s) || Arrays.asList(i.getExtraLetters()).contains(s) && !(i instanceof Air)) {
                return i;
            }
        }
        return null;
    }

    static Icon getIconFromSingleLetter(String s, boolean chal) {
        for (Icon i : icons) {
            if ((i.getSingleLetter().equalsIgnoreCase(s)
                    || Arrays.asList(i.getExtraLetters()).contains(s))
                    && !(i instanceof Air)) {
                if (!(chal && i instanceof Stats)) {
                    return i;
                }
            }
        }
        return null;
    }

    default Material getMaterial() {
        Material material = Material.getMaterial(HeadsPlus.getInstance().getItems().getConfig().getString("icons." + getIconName() + ".material"));
        return material != null ? material : Material.STONE;
    }

    default List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    default String getDisplayName() {
        return HeadsPlus.getInstance().getItems().getConfig().getString("icons." + getIconName() + ".display-name");
    }

    String getSingleLetter();

    default String[] getExtraLetters() {
        return new String[0];
    }
}
