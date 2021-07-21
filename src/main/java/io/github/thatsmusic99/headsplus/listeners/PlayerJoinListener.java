package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.crafting.RecipeEnumUser;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends HeadsPlusListener<PlayerJoinEvent> {
	
	public static boolean reloaded = false;
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

	public void onEvent(PlayerJoinEvent e) {
	    HeadsPlus hp = HeadsPlus.get();
	    Player player = e.getPlayer();
	    addData("player", player.getName());
		if (!addData("has-update-permission", player.hasPermission("headsplus.notify"))) {
		    if (addData("update-enabled", MainConfig.get().getUpdates().CHECK_FOR_UPDATES)) {
                if (addData("has-update", HeadsPlus.getUpdate() != null)) {
                    new FancyMessage().text(hpc.getString("update.update-found", player))
                    .tooltip(hpc.getString("update.current-version", player).replaceAll("\\{version}", hp.getDescription().getVersion())
							+ "\n" + hpc.getString("update.new-version", player).replaceAll("\\{version}", String.valueOf(HeadsPlus.getUpdate()[0]))
							+ "\n" + hpc.getString("update.description", player).replaceAll("\\{description}", String.valueOf(HeadsPlus.getUpdate()[1]))).link("https://www.spigotmc.org/resources/headsplus-1-8-x-1-13-x.40265/updates/").send(player);
                }
            }
        }
		Bukkit.getScheduler().runTaskLater(HeadsPlus.get(), () -> {
		    if (!player.isOnline())
		        return;
            MaskListener.checkMask(player, player.getInventory().getHelmet());
        }, 20);

        if(hp.getConfig().getBoolean("plugin.autograb.enabled")) {
            if (!hp.getServer().getOnlineMode()) {
                hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                String uuid = AutograbManager.grabUUID(player.getName(), 3, null);
                //TODO sync post request?
                // This actually runs async, the grabTexture doesn't though (see the actual methods)
                AutograbManager.grabProfile(uuid);
            } else {
                // here too?
                AutograbManager.grabTexture(player, false, null);
            }
        }

        HPPlayer.getHPPlayer(player);
        if (!reloaded) {
            reloaded = true;
            Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), RecipeEnumUser::new);
        }

	}

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerJoinEvent.class, "PlayerJoinEvent", this), HeadsPlus.get());
        addPossibleData("player", "<Player>");
        addPossibleData("has-update-permission", "true", "false");
        addPossibleData("update-enabled", "true", "false");
        addPossibleData("has-update", "true", "false");
    }
}
