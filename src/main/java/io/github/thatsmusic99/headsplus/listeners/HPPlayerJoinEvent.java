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
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HPPlayerJoinEvent extends HeadsPlusListener<PlayerJoinEvent> {
	
	public static boolean reloaded = false;
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    public HPPlayerJoinEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerJoinEvent.class, "PlayerJoinEvent", this), HeadsPlus.getInstance());
        addPossibleData("player", "<Player>");
        addPossibleData("has-update-permission", "true", "false");
        addPossibleData("update-enabled", "true", "false");
        addPossibleData("has-update", "true", "false");
    }

	public void onEvent(PlayerJoinEvent e) {
	    HeadsPlus hp = HeadsPlus.getInstance();
	    addData("player", e.getPlayer().getName());
		if (addData("has-update-permission", e.getPlayer().hasPermission("headsplus.notify"))) {
		    if (addData("update-enabled", hp.getConfiguration().getMechanics().getBoolean("update.notify"))) {
                if (addData("has-update", HeadsPlus.getUpdate() != null)) {
                    new FancyMessage().text(hpc.getString("update.update-found", e.getPlayer()))
                    .tooltip(hpc.getString("update.current-version", e.getPlayer()).replaceAll("\\{version}", hp.getDescription().getVersion())
							+ "\n" + hpc.getString("update.new-version", e.getPlayer()).replaceAll("\\{version}", String.valueOf(HeadsPlus.getUpdate()[0]))
							+ "\n" + hpc.getString("update.description", e.getPlayer()).replaceAll("\\{description}", String.valueOf(HeadsPlus.getUpdate()[1]))).link("https://www.spigotmc.org/resources/headsplus-1-8-x-1-13-x.40265/updates/").send(e.getPlayer());
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                HPMaskEvents.checkMask(e.getPlayer(), e.getPlayer().getInventory().getHelmet());
            }
        }.runTaskLater(hp, 20);

        if(hp.getConfig().getBoolean("plugin.autograb.enabled")) {
            if (!hp.getServer().getOnlineMode()) {
                hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                String uuid = AutograbManager.grabUUID(e.getPlayer().getName(), 3, null);
                AutograbManager.grabProfile(uuid);
            } else {
                AutograbManager.grabTexture(e.getPlayer(), false, null);
            }
        }

        HPPlayer.getHPPlayer(e.getPlayer());
        if (!reloaded) {
            reloaded = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    new RecipeEnumUser();
                }
            }.runTaskAsynchronously(hp);

        }

	}
}
