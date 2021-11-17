package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends HeadsPlusListener<PlayerJoinEvent> {

    private final MessagesManager hpc = MessagesManager.get();

	public void onEvent(PlayerJoinEvent e) {
	    HeadsPlus hp = HeadsPlus.get();
	    Player player = e.getPlayer();
	    addData("player", player.getName());

		Bukkit.getScheduler().runTaskLater(HeadsPlus.get(), () -> {
		    if (!player.isOnline())
		        return;
            MaskListener.checkMask(player, player.getInventory().getHelmet());
        }, 20);
		
        if (MainConfig.get().getAutograbber().ENABLE_AUTOGRABBER) {
            if (!hp.getServer().getOnlineMode()) {
                hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                String uuid = AutograbManager.grabUUID(player.getName(), 3, null);
                AutograbManager.grabProfile(uuid);
            } else {
                AutograbManager.grabTexture(player, false, null);
            }
        }

        PlayerSQLManager.get().checkPlayer(player.getUniqueId(), player.getName())
                .thenAccept(ok -> PlayerSQLManager.get().loadPlayer(player.getUniqueId()));

        if (!addData("has-update-permission", player.hasPermission("headsplus.notify"))) return;
        if (!addData("update-enabled", MainConfig.get().getUpdates().CHECK_FOR_UPDATES)) return;
        if (addData("has-update", HeadsPlus.getUpdate() == null)) return;
        if (!addData("notify-admins", MainConfig.get().getUpdates().NOTIFY_ADMINS)) return;
        new FancyMessage().text(hpc.getString("update.update-found", player))
                .tooltip(hpc.getString("update.current-version", player).replaceAll("\\{version}", hp.getDescription().getVersion())
                        + "\n" + hpc.getString("update.new-version", player).replaceAll("\\{version}", String.valueOf(HeadsPlus.getUpdate()[0]))
                        + "\n" + hpc.getString("update.description", player).replaceAll("\\{description}", String.valueOf(HeadsPlus.getUpdate()[1]))).link("https://www.spigotmc.org/resources/headsplus-1-8-x-1-13-x.40265/updates/").send(player);

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
