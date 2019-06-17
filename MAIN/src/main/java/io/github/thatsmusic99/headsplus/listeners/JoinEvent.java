package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.crafting.RecipeEnumUser;
import io.github.thatsmusic99.headsplus.locale.Locale;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinEvent implements Listener { 
	
	public static boolean reloaded = false;
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("headsplus.notify")) {
		    if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("update.notify")) {
                if (HeadsPlus.getUpdate() != null) {
                    Locale l = LocaleManager.getLocale();
                    new FancyMessage().text(hpc.getString("update-found"))
                    .tooltip(ChatColor.translateAlternateColorCodes('&', l.getCurrentVersion() + HeadsPlus.getInstance().getDescription().getVersion())
							+ "\n" + ChatColor.translateAlternateColorCodes('&', l.getNewVersion() + HeadsPlus.getUpdate()[2])
							+ "\n" + ChatColor.translateAlternateColorCodes('&', l.getDescription() + HeadsPlus.getUpdate()[1])).link("https://www.spigotmc.org/resources/headsplus-1-8-x-1-13-x.40265/updates/").send(e.getPlayer());
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer().getInventory().getArmorContents()[3] != null) {
                    NMSManager nms = HeadsPlus.getInstance().getNMS();
                    NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
                    if (e.getPlayer().getInventory().getArmorContents()[3].getType().equals(nms.getSkullMaterial(1).getType())) {

                        HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
                        String s = nbt.getType(e.getPlayer().getInventory().getArmorContents()[3]).toLowerCase();
                        if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
                            HPPlayer pl = HPPlayer.getHPPlayer(e.getPlayer());
                            pl.addMask(s);
                        }
                    }
                }
            }
        }.runTaskLater(HeadsPlus.getInstance(), 20);

        if(HeadsPlus.getInstance().getConfig().getBoolean("plugin.autograb.enabled")) {
            HeadsPlus.getInstance().getHeadsXConfig().grabProfile(e.getPlayer().getUniqueId());
        }

        if (!reloaded) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new RecipeEnumUser();
                }
            }.runTaskLater(HeadsPlus.getInstance(), 20);
            reloaded = true;
        }

	}
}
