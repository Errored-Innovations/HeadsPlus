package io.github.thatsmusic99.headsplus.config.headsx;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.icons.*;
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
        new ChallengeSection.Easy(),
        new ChallengeSection.EasyMedium(),
        new ChallengeSection.Medium(),
        new ChallengeSection.MediumHard(),
        new ChallengeSection.Hard(),
        new ChallengeSection.Tedious(),
        new ChallengeSection.TediousPainful(),
        new ChallengeSection.Painful(),
        new ChallengeSection.PainfulDeadly(),
        new ChallengeSection.Deadly()
    );

    static Icon getIconFromSingleLetter(String s) {
        for (Icon i : icons) {
            if (i.getSingleLetter().equalsIgnoreCase(s) && !(i instanceof Air)) {
                return i;
            }
        }
        return null;
    }

    default Material getMaterial() {
        return Material.getMaterial(HeadsPlus.getInstance().getItems().getConfig().getString("icons." + getIconName() + ".material"));
    }

    default List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    default String getDisplayName() {
        return HeadsPlus.getInstance().getItems().getConfig().getString("icons." + getIconName() + ".display-name");
    }

    String getSingleLetter();
}
