package io.github.thatsmusic99.headsplus.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class FlagHandler {

    //
    public StateFlag HEAD_DROP = new StateFlag("head-drop", true);
    public StateFlag HEAD_CRAFT = new StateFlag("head-craft", true);
    public StateFlag ALLOW_MASKS = new StateFlag("allow-mask-use", true);
    public SetFlag<String> HEAD_DENIED_IDS = new SetFlag<>("head-denied-ids", new StringFlag("head-denied-id"));
    public SetFlag<String> HEAD_ALLOWED_IDS = new SetFlag<>("head-allowed-ids", new StringFlag("head-allowed-id"));
    private static FlagHandler handler = null;

    public FlagHandler() {
        handler = this;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        // Register the flags
        registry.register(HEAD_DROP);
        registry.register(HEAD_CRAFT);
        registry.register(ALLOW_MASKS);
        registry.register(HEAD_DENIED_IDS);
        registry.register(HEAD_ALLOWED_IDS);
    }

    public static FlagHandler getHandler() {
        return handler;
    }

    public static boolean isHandling() {
        return handler != null && HeadsPlus.get().canUseWG();
    }

    public static boolean canDrop(Location location, EntityType type) {
        if (!isHandling()) return true;
        return query(location, type, getHandler().HEAD_DROP);
    }

    public static boolean canCraft(Location location, EntityType type) {
        if (!isHandling()) return true;
        return query(location, type, getHandler().HEAD_CRAFT);
    }

    public static boolean canUseMasks(Player player) {
        if (!isHandling()) return true;
        LocalPlayer wrappedPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        // Get the region manager for the world we're inWorld
        RegionManager manager = container.get(wrappedPlayer.getWorld());
        // Uses the query cache
        RegionQuery query = container.createQuery();
        if (manager == null) return true;
        // Get all the applicable regions, as some will overlap
        ApplicableRegionSet regions = query.getApplicableRegions(wrappedPlayer.getLocation());
        // Return the flag status
        return regions.testState(wrappedPlayer, getHandler().ALLOW_MASKS);
    }

    private static boolean query(Location location, EntityType type, StateFlag flag) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        // Get the region manager for the world we're inWorld
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        // Uses the query cache
        RegionQuery query = container.createQuery();
        if (manager == null) return true;
        // Get all the applicable regions, as some will overlap
        ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));
        // We'll assume there's no player here
        if (regions.testState(null, flag)) {
            Set<String> allowedMobs = regions.queryValue(null, getHandler().HEAD_ALLOWED_IDS);
            if (allowedMobs != null && !allowedMobs.isEmpty()) {
                return allowedMobs.contains(type.name());
            }
            Set<String> deniedMobs = regions.queryValue(null, getHandler().HEAD_DENIED_IDS);
            if (deniedMobs != null && !deniedMobs.isEmpty()) {
                return !deniedMobs.contains(type.name());
            }
            return true;
        }
        return false;
    }

}
