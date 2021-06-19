package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.crafting.RecipeEnumUser;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class HPPlayerJoinEvent extends HeadsPlusListener<PlayerJoinEvent> {
	
	public static boolean reloaded = false;
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

	public void onEvent(PlayerJoinEvent e) {
	    HeadsPlus hp = HeadsPlus.getInstance();
	    Player player = e.getPlayer();
	    addData("player", player.getName());
		if (!addData("has-update-permission", player.hasPermission("headsplus.notify"))) {
		    if (addData("update-enabled", hp.getConfiguration().getMechanics().getBoolean("update.notify"))) {
                if (addData("has-update", HeadsPlus.getUpdate() != null)) {
                    new FancyMessage().text(hpc.getString("update.update-found", player))
                    .tooltip(hpc.getString("update.current-version", player).replaceAll("\\{version}", hp.getDescription().getVersion())
							+ "\n" + hpc.getString("update.new-version", player).replaceAll("\\{version}", String.valueOf(HeadsPlus.getUpdate()[0]))
							+ "\n" + hpc.getString("update.description", player).replaceAll("\\{description}", String.valueOf(HeadsPlus.getUpdate()[1]))).link("https://www.spigotmc.org/resources/headsplus-1-8-x-1-13-x.40265/updates/").send(player);
                }
            }
        }
		Bukkit.getScheduler().runTaskLater(HeadsPlus.getInstance(), () -> {
		    if (!player.isOnline())
		        return;
            HPMaskEvents.checkMask(player, player.getInventory().getHelmet());
        }, 20);

        if(hp.getConfig().getBoolean("plugin.autograb.enabled")) {
            if (!hp.getServer().getOnlineMode()) {
                hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                String uuid = AutograbManager.grabUUID(player.getName(), 3, null);
                //TODO sync post request?
                AutograbManager.grabProfile(uuid);
            } else {
                // here too?
                AutograbManager.grabTexture(player, false, null);
            }
        }

        HPPlayer.getHPPlayer(player);
        if (!reloaded) {
            reloaded = true;
            Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.getInstance(), RecipeEnumUser::new);
        }

	}

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerJoinEvent.class, "PlayerJoinEvent", this), HeadsPlus.getInstance());
        addPossibleData("player", "<Player>");
        addPossibleData("has-update-permission", "true", "false");
        addPossibleData("update-enabled", "true", "false");
        addPossibleData("has-update", "true", "false");
    }
}
