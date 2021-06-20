package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusException;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadsPlusMessagesManager {

    private static YamlConfiguration config;
    private static HashMap<String, YamlConfiguration> locales;
    private static HashMap<UUID, YamlConfiguration> players;
    private static HeadsPlusMessagesManager instance;

    public HeadsPlusMessagesManager() {
        instance = this;
        HeadsPlus hp = HeadsPlus.get();
        String dest = hp.getDataFolder() + File.separator + "locale" + File.separator;
        String locale = MainConfig.get().getLocalisation().LOCALE;
        locales = new HashMap<>();
        if (MainConfig.get().getBoolean("smart-locale")) {
            File langDir = new File(dest);
            for (File f : Objects.requireNonNull(langDir.listFiles())) {
                locales.put(f.getName().split("_")[0].toLowerCase(), performChecks(f, f.getName().toLowerCase()));
            }
            players = new HashMap<>();
        }
        // Main config for non-player entities such as console
        try {
            config = performChecks(new File(dest, locale + ".yml"), locale);
        } catch (Exception e) {
            hp.getLogger().info("Failed to load the locale settings! This is caused by an invalid name provided. Setting locale to en_us...");
            config = performChecks(new File(dest, "en_us.yml"), "en_us");
        }
        double version = 1.5;
        if (config.getDouble("version") != version) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    hp.getLogger().info("Locale configs are outdated! Updating messages...");
                    YamlConfiguration en_us = performChecks(new File(dest, "en_us.yml"), "en_us");
                    YamlConfiguration de_de = performChecks(new File(dest, "de_de.yml"), "de_de");
                    YamlConfiguration es_es = performChecks(new File(dest, "es_es.yml"), "es_es");
                    YamlConfiguration fr_fr = performChecks(new File(dest, "fr_fr.yml"), "fr_fr");
                    YamlConfiguration hu_hu = performChecks(new File(dest, "hu_hu.yml"), "hu_hu");
                    YamlConfiguration lol_us = performChecks(new File(dest, "lol_us.yml"), "lol_us");
                    YamlConfiguration pl_pl = performChecks(new File(dest, "pl_pl.yml"), "pl_pl");
                    YamlConfiguration ro_ro = performChecks(new File(dest, "ro_ro.yml"), "ro_ro");
                    YamlConfiguration ru_ru = performChecks(new File(dest, "ru_ru.yml"), "ru_ru");
                    YamlConfiguration nl_nl = performChecks(new File(dest, "nl_nl.yml"), "nl_nl");
                    YamlConfiguration zh_cn = performChecks(new File(dest, "zh_cn.yml"), "zh_cn");

                    {
                        en_us.addDefault("language", "English (US)");
                        en_us.addDefault("commands.errors.not-a-player", "{header} You must be a player to run this command/subcommand!");
                        en_us.addDefault("commands.locale.invalid-lang", "{header} That is not a valid language! Available languages: &c{languages}");
                        en_us.addDefault("commands.locale.changed-locale", "{header} Good day! Your language is now set to &cEnglish (US)");
                        en_us.addDefault("commands.locale.changed-locale-other", "{header} &c{player}&7's language is now set to &c{language}&7!");
                        en_us.addDefault("inventory.icon.challenge.reward", "&6Reward: &a{reward}");
                        en_us.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        en_us.addDefault("inventory.icon.challenge.count", "&7{challenge-count} challenges");
                        en_us.addDefault("inventory.icon.close", "&8❰ &c&lClose Menu &8❱");
                        en_us.addDefault("inventory.icon.favourites", "&8❰ &b&lFavorites &8❱");
                        en_us.addDefault("inventory.icon.head.price", "&cPrice &8❱ &7{price}");
                        en_us.addDefault("inventory.icon.head.favourite", "&cFavorite!");
                        en_us.addDefault("inventory.icon.head.count", "&7{head-count} heads");
                        en_us.addDefault("inventory.icon.menu", "&8❰ &a&lMain Menu &8❱");
                        en_us.addDefault("inventory.icon.start", "&8❰ &a&lFirst Page &8❱");
                        en_us.addDefault("inventory.icon.last", "&8❰ &a&lLast Page &8❱");
                        en_us.addDefault("inventory.icon.back", "&8❰ &a&lBack &8❱");
                        en_us.addDefault("inventory.icon.back-2", "&8❰ &a&lBack (2) &8❱");
                        en_us.addDefault("inventory.icon.back-3", "&8❰ &a&lBack (3) &8❱");
                        en_us.addDefault("inventory.icon.next", "&8❰ &a&lNext &8❱");
                        en_us.addDefault("inventory.icon.next-2", "&8❰ &a&lNext (2) &8❱");
                        en_us.addDefault("inventory.icon.next-3", "&8❰ &a&lNext (3) &8❱");
                        en_us.addDefault("inventory.icon.search", "&8❰ &e&lSearch Heads &8❱");
                        en_us.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStats &8❱");
                        en_us.addDefault("inventory.icon.stats.total-heads", "&aTotal Heads &8❱ &e");
                        en_us.addDefault("inventory.icon.stats.total-pages", "&aTotal Pages &8❱ &e");
                        en_us.addDefault("inventory.icon.stats.total-sections", "&aTotal Sections &8❱ &e");
                        en_us.addDefault("inventory.icon.stats.current-balance", "&aCurrent Balance &8❱ &e");
                        en_us.addDefault("inventory.icon.stats.current-section", "&aCurrent Section &8❱ &e");
                        en_us.addDefault("textmenus.profile.player", "Player");
                        en_us.addDefault("textmenus.profile.completed-challenges", "Completed Challenges");
                        en_us.addDefault("textmenus.profile.total-heads-dropped", "Total Heads Dropped");
                        en_us.addDefault("textmenus.profile.total-heads-sold", "Total Heads Sold");
                        en_us.addDefault("textmenus.profile.total-heads-crafted", "Total Heads Crafted");
                        en_us.addDefault("textmenus.profile.current-level", "Current Level");
                        en_us.addDefault("textmenus.profile.xp-until-next-level", "XP until next level");
                        en_us.addDefault("textmenus.blacklist", "Blacklist");
                        en_us.addDefault("textmenus.whitelist", "Whitelist");
                        en_us.addDefault("textmenus.blacklistw", "World Blacklist");
                        en_us.addDefault("textmenus.whitelistw", "World Whitelist");
                        en_us.addDefault("textmenus.info.version", "Version");
                        en_us.addDefault("textmenus.info.author", "Author");
                        en_us.addDefault("textmenus.info.language", "Language");
                        en_us.addDefault("textmenus.info.contributors", "Contributors");
                        en_us.addDefault("textmenus.info.spigot", "SpigotMC link");
                        en_us.addDefault("textmenus.info.discord", "Discord Server");
                        en_us.addDefault("textmenus.info.github", "Github");
                        en_us.addDefault("textmenus.head-info.type", "Type");
                        en_us.addDefault("textmenus.head-info.display-name", "Display Name");
                        en_us.addDefault("textmenus.head-info.price", "Price");
                        en_us.addDefault("textmenus.head-info.interact-name", "Interact Name");
                        en_us.addDefault("textmenus.head-info.chance", "Chance");
                        en_us.addDefault("textmenus.help.usage", "Usage");
                        en_us.addDefault("textmenus.help.description", "Description");
                        en_us.addDefault("textmenus.help.permission", "Permission");
                        en_us.addDefault("textmenus.help.further-usages", "Further Usages");
                        en_us.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        en_us.addDefault("inventory.icon.reward.group-add", "&7Group {group} &7(&a+&7)");
                        en_us.addDefault("inventory.icon.reward.group-remove", "&7Group {group} &7(&c-&7)");
                        en_us.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(s)");
                        en_us.addDefault("commands.addhead.bad-texture", "{header} The texture you have provided is invalid. It must be a Minecraft texture URL (https://textures.minecraft.net) or a Base64 encoded string.");
                        en_us.addDefault("commands.addhead.cancelled", "{header} Cancelled head creation.");
                        en_us.addDefault("commands.addhead.custom-head-added", "{header} Added new head with ID &c{id}&7!");
                        en_us.addDefault("commands.addhead.displayname", "{header} Type in the display name for the head (including colour codes).");
                        en_us.addDefault("commands.addhead.id", "{header} Type in the ID of the head (e.g. brown_sheep, red_flower_bush)");
                        en_us.addDefault("commands.addhead.id-taken", "{header} That ID has been taken (&c{id}&7)!");
                        en_us.addDefault("commands.addhead.price", "{header} Type in the price of the head (or \"default\" for the default value).");
                        en_us.addDefault("commands.addhead.section", "{header} Type in the section the head will be put in. (Available sections: &c{sections}&7)");
                        en_us.addDefault("commands.addhead.texture", "{header} Type in or copy and paste the texture for the head. (This may require several messages, so when you are done, type \"done\" in chat and enter.)");
                        en_us.addDefault("inventory.icon.challenge.progress", "&7Progress &8❱ &c{heads}&7/&c{total}");
                        en_us.addDefault("inventory.icon.challenge.pinned", "&cPinned!");
                        en_us.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lPinned Challenges &8❱");
                        en_us.addDefault("commands.debug.verbose.enabled", "{header} Enabled the debugging verbose for event &c{event} &7and arguments &c{args}&7!");
                        en_us.addDefault("commands.debug.verbose.disabled", "{header} Disabled the debugging verbose!");
                        en_us.addDefault("commands.restore.restored-head", "{header} Restored {head}!");
                        en_us.addDefault("commands.restore.invalid-head", "{header} {head} is not a valid ID!");
                        en_us.addDefault("descriptions.hp.restore", "Restores or repairs a head that was already provided by the plugin.");
                        en_us.set("version", version);
                        en_us.options().copyDefaults(true);
                        try {
                            en_us.save(new File(dest, "en_us.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        de_de.addDefault("language", "Deutsch (DE)");
                        de_de.addDefault("commands.errors.not-a-player", "{header} Sie müssen ein Spieler sein, um diesen Befehl / Unterbefehl auszuführen!");
                        de_de.addDefault("commands.locale.invalid-lang", "{header} Das ist keine gültige Sprache! Verfügbare Sprachen: &c{languages}");
                        de_de.addDefault("commands.locale.changed-locale", "{header} Guten Tag! Ihre Sprache ist jetzt auf &cDeutsch (DE) &7eingestellt.");
                        de_de.addDefault("commands.locale.changed-locale-other", "{header} Die Sprache von &c{player} &7ist jetzt auf &c{language} &7eingestellt!");
                        de_de.addDefault("inventory.icon.challenge.reward", "&6Belohnung: &a{reward}");
                        de_de.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        de_de.addDefault("inventory.icon.challenge.count", "&7Herausforderungen: {challenge-count}");
                        de_de.addDefault("inventory.icon.close", "&8❰ &c&lMenü schließen &8❱");
                        de_de.addDefault("inventory.icon.favourites", "&8❰ &b&lFavoriten &8❱");
                        de_de.addDefault("inventory.icon.head.price", "&cPreis &8❱ &7{price}");
                        de_de.addDefault("inventory.icon.head.favourite", "&cFavorit!");
                        de_de.addDefault("inventory.icon.head.count", "&7{head-count} Köpfe");
                        de_de.addDefault("inventory.icon.menu", "&8❰ &a&lHauptmenü &8❱");
                        de_de.addDefault("inventory.icon.start", "&8❰ &a&lErste Seite &8❱");
                        de_de.addDefault("inventory.icon.last", "&8❰ &a&lLetzte Seite &8❱");
                        de_de.addDefault("inventory.icon.back", "&8❰ &a&lZurückkehren &8❱");
                        de_de.addDefault("inventory.icon.back-2", "&8❰ &a&lZurückkehren (2) &8❱");
                        de_de.addDefault("inventory.icon.back-3", "&8❰ &a&lZurückkehren (3) &8❱");
                        de_de.addDefault("inventory.icon.next", "&8❰ &a&lNächste &8❱");
                        de_de.addDefault("inventory.icon.next-2", "&8❰ &a&lNächste (2) &8❱");
                        de_de.addDefault("inventory.icon.next-3", "&8❰ &a&lNächste (3) &8❱");
                        de_de.addDefault("inventory.icon.search", "&8❰ &e&lKöpfe suchen &8❱");
                        de_de.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStatistiken &8❱");
                        de_de.addDefault("inventory.icon.stats.total-heads", "&aKöpfe gesamt &8❱ &e");
                        de_de.addDefault("inventory.icon.stats.total-pages", "&aAlle Seiten &8❱ &e");
                        de_de.addDefault("inventory.icon.stats.total-sections", "&aAbschnitte insgesamt &8❱ &e");
                        de_de.addDefault("inventory.icon.stats.current-balance", "&aAktueller Kontostand &8❱ &e");
                        de_de.addDefault("inventory.icon.stats.current-section", "&aAktueller Bereich &8❱ &e");
                        de_de.addDefault("textmenus.profile.player", "Spieler");
                        de_de.addDefault("textmenus.profile.completed-challenges", "Herausforderungen abgeschlossen");
                        de_de.addDefault("textmenus.profile.total-heads-dropped", "Insgesamt fielen die Köpfe");
                        de_de.addDefault("textmenus.profile.total-heads-sold", "Total verkaufte Köpfe");
                        de_de.addDefault("textmenus.profile.total-heads-crafted", "Totale Köpfe gefertigt");
                        de_de.addDefault("textmenus.profile.current-level", "Aktuelles Level");
                        de_de.addDefault("textmenus.profile.xp-until-next-level", "XP bis zum nächsten Level");
                        de_de.addDefault("textmenus.blacklist", "Blacklist");
                        de_de.addDefault("textmenus.whitelist", "Whitelist");
                        de_de.addDefault("textmenus.blacklistw", "Welten-Blacklist");
                        de_de.addDefault("textmenus.whitelistw", "Welten-Whitelist");
                        de_de.addDefault("textmenus.info.version", "Ausführung");
                        de_de.addDefault("textmenus.info.author", "Autorin");
                        de_de.addDefault("textmenus.info.language", "Sprache");
                        de_de.addDefault("textmenus.info.contributors", "Mitwirkende");
                        de_de.addDefault("textmenus.info.spigot", "SpigotMC Link");
                        de_de.addDefault("textmenus.info.discord", "Discord-Server");
                        de_de.addDefault("textmenus.info.github", "Github");
                        de_de.addDefault("textmenus.head-info.type", "Sorte");
                        de_de.addDefault("textmenus.head-info.display-name", "Anzeigename");
                        de_de.addDefault("textmenus.head-info.price", "Pries");
                        de_de.addDefault("textmenus.head-info.interact-name", "Interaktionsname");
                        de_de.addDefault("textmenus.head-info.chance", "Möglichkeit");
                        de_de.addDefault("textmenus.help.usage", "Verwendung");
                        de_de.addDefault("textmenus.help.description", "Beschreibung");
                        de_de.addDefault("textmenus.help.permission", "Genehmigung");
                        de_de.addDefault("textmenus.help.further-usages", "Weitere Verwendung");
                        de_de.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        de_de.addDefault("inventory.icon.reward.group-add", "&7Gruppe {group} &7(&a+&7)");
                        de_de.addDefault("inventory.icon.reward.group-remove", "&7Gruppe {group} &7(&c-&7)");
                        de_de.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(n)");
                        de_de.addDefault("commands.addhead.bad-texture", "{header} Die von Ihnen angegebene Textur ist ungültig. Es muss sich um eine Minecraft-Textur-URL (https://textures.minecraft.net) oder eine Base64-codierte Zeichenfolge handeln.");
                        de_de.addDefault("commands.addhead.cancelled", "{header} Abgebrochene Kopferstellung.");
                        de_de.addDefault("commands.addhead.custom-head-added", "{header} Neuer Kopf mit ID {id} hinzugefügt!");
                        de_de.addDefault("commands.addhead.displayname", "{header} Geben Sie den Anzeigenamen für den Kopf ein (einschließlich Farbcodes).");
                        de_de.addDefault("commands.addhead.id", "{header} Geben Sie die ID des Kopfes ein (z. B. brown_sheep, red_flower_bush).");
                        de_de.addDefault("commands.addhead.id-taken", "{header} Diese ID wurde genommen (&c{id}&7)!");
                        de_de.addDefault("commands.addhead.price", "{header} Geben Sie den Preis des Kopfes ein (oder \"default\" für den Standardwert).");
                        de_de.addDefault("commands.addhead.section", "{header} Geben Sie den Abschnitt ein, in den der Kopf eingefügt werden soll. (Verfügbare Abschnitte: &c{sections}&7)");
                        de_de.addDefault("commands.addhead.texture", "{header} Geben Sie die Textur für den Kopf ein oder kopieren Sie sie und fügen Sie sie ein. (Dies kann mehrere Nachrichten erfordern. Wenn Sie fertig sind, geben Sie im Chat \"done\" ein und geben Sie ein.)");
                        de_de.addDefault("inventory.icon.challenge.progress", "&7Fortschritt &8❱ &c{heads}&7/&c{total}");
                        de_de.addDefault("inventory.icon.challenge.pinned", "&cGepinnt!");
                        de_de.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lFestgesteckte Herausforderungen &8❱");
                        de_de.addDefault("commands.debug.verbose.enabled", "{header} Das ausführliche Debuggen für Ereignis &c{event} &7und Argumente &c{args} &7wurde aktiviert!");
                        de_de.addDefault("commands.debug.verbose.disabled", "{header} Deaktiviert das ausführliche Debuggen!");
                        de_de.addDefault("commands.restore.restored-head", "{header} {head} erfolgreich wiederhergestellter!");
                        de_de.addDefault("commands.restore.invalid-head", "{header} {head} ist keine gültige ID!");
                        de_de.addDefault("descriptions.hp.restore", "Stellt einen Kopf wieder her oder repariert ihn, der bereits vom Plugin bereitgestellt wurde.");
                        de_de.set("version", version);
                        de_de.options().copyDefaults(true);
                        try {
                            de_de.save(new File(dest, "de_de.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        es_es.addDefault("language", "Español (ES)");
                        es_es.addDefault("commands.errors.not-a-player", "{header} ¡Debes ser un jugador para ejecutar este comando/subcomando!");
                        es_es.addDefault("commands.locale.invalid-lang", "{header} ¡Ese no es un idioma válido! Idiomas disponibles: &c{languages}");
                        es_es.addDefault("commands.locale.changed-locale", "{header} ¡Hola! Su idioma ahora está configurado en &cEspañol (ES)");
                        es_es.addDefault("commands.locale.changed-locale-other", "{header} El idioma de &c{player} &7ahora está configurado en &c{language}&7!");
                        es_es.addDefault("inventory.icon.challenge.reward", "&6Recompensa: &a{reward}");
                        es_es.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        es_es.addDefault("inventory.icon.challenge.count", "&7{challenge-count} desafíos");
                        es_es.addDefault("inventory.icon.close", "&8❰ &c&lCerrar menú &8❱");
                        es_es.addDefault("inventory.icon.favourites", "&8❰ &b&lFavoritas &8❱");
                        es_es.addDefault("inventory.icon.head.price", "&cPrecio &8❱ &7{price}");
                        es_es.addDefault("inventory.icon.head.favourite", "&cFavorita!");
                        es_es.addDefault("inventory.icon.head.count", "&7{head-count} cabezas");
                        es_es.addDefault("inventory.icon.menu", "&8❰ &a&lMenú principal &8❱");
                        es_es.addDefault("inventory.icon.start", "&8❰ &a&lPrimera página &8❱");
                        es_es.addDefault("inventory.icon.last", "&8❰ &a&lÚltima página &8❱");
                        es_es.addDefault("inventory.icon.back", "&8❰ &a&lRetroceder &8❱");
                        es_es.addDefault("inventory.icon.back-2", "&8❰ &a&lRetroceder 2 &8❱");
                        es_es.addDefault("inventory.icon.back-3", "&8❰ &a&lRetroceder 3 &8❱");
                        es_es.addDefault("inventory.icon.next", "&8❰ &a&lSiguiente &8❱");
                        es_es.addDefault("inventory.icon.next-2", "&8❰ &a&lSiguiente (2) &8❱");
                        es_es.addDefault("inventory.icon.next-3", "&8❰ &a&lSiguiente (3) &8❱");
                        es_es.addDefault("inventory.icon.search", "&8❰ &e&lBuscar cabezas &8❱");
                        es_es.addDefault("inventory.icon.stats.icon", "&8❰ &a&lEstadísticas &8❱");
                        es_es.addDefault("inventory.icon.stats.total-heads", "&aCabezas totales &8❱ &e");
                        es_es.addDefault("inventory.icon.stats.total-pages", "&aPaginas totales &8❱ &e");
                        es_es.addDefault("inventory.icon.stats.total-sections", "&aSecciones totales &8❱ &e");
                        es_es.addDefault("inventory.icon.stats.current-balance", "&aSaldo actual &8❱ &e");
                        es_es.addDefault("inventory.icon.stats.current-section", "&aSección actual &8❱ &e");
                        es_es.addDefault("textmenus.profile.player", "Jugador");
                        es_es.addDefault("textmenus.profile.completed-challenges", "Desafíos completados");
                        es_es.addDefault("textmenus.profile.total-heads-dropped", "Total de cabezas caídas");
                        es_es.addDefault("textmenus.profile.total-heads-sold", "Total de cabezas vendidas");
                        es_es.addDefault("textmenus.profile.total-heads-crafted", "Total de cabezas hechas a mano");
                        es_es.addDefault("textmenus.profile.current-level", "Nivel actual");
                        es_es.addDefault("textmenus.profile.xp-until-next-level", "XP hasta el siguiente nivel");
                        es_es.addDefault("textmenus.blacklist", "Lista-negra");
                        es_es.addDefault("textmenus.whitelist", "Lista-blanca");
                        es_es.addDefault("textmenus.blacklistw", "Lista-negra de mundos");
                        es_es.addDefault("textmenus.whitelistw", "Lista-blanca de mundos");
                        es_es.addDefault("textmenus.info.version", "Versión");
                        es_es.addDefault("textmenus.info.author", "Autora");
                        es_es.addDefault("textmenus.info.language", "Idioma");
                        es_es.addDefault("textmenus.info.contributors", "Colaboradores");
                        es_es.addDefault("textmenus.info.spigot", "Enlace SpigotMC");
                        es_es.addDefault("textmenus.info.discord", "Servidor Discord");
                        es_es.addDefault("textmenus.info.github", "Github");
                        es_es.addDefault("textmenus.head-info.type", "Tipo");
                        es_es.addDefault("textmenus.head-info.display-name", "Nombre para mostrar");
                        es_es.addDefault("textmenus.head-info.price", "Precio");
                        es_es.addDefault("textmenus.head-info.interact-name", "Nombre de Interact");
                        es_es.addDefault("textmenus.head-info.chance", "Posibilidad");
                        es_es.addDefault("textmenus.help.usage", "Uso");
                        es_es.addDefault("textmenus.help.description", "Descripción");
                        es_es.addDefault("textmenus.help.permission", "Permiso");
                        es_es.addDefault("textmenus.help.further-usages", "Usos adicionales");
                        es_es.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        es_es.addDefault("inventory.icon.reward.group-add", "&7Grupo {group} &7(&a+&7)");
                        es_es.addDefault("inventory.icon.reward.group-remove", "&7Grupo {group} &7(&c-&7)");
                        es_es.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(s)");
                        es_es.addDefault("commands.addhead.bad-texture", "{header} La textura que ha proporcionado no es válida. Debe ser una URL de textura de Minecraft (https://textures.minecraft.net) o una cadena codificada en Base64.");
                        es_es.addDefault("commands.addhead.cancelled", "{header} Se canceló la creación de la cabeza.");
                        es_es.addDefault("commands.addhead.custom-head-added", "{header} ¡Se agregó una nueva cabeza con ID &c{id}&7!");
                        es_es.addDefault("commands.addhead.displayname", "{header} Escriba el nombre para mostrar de la cabeza (incluidos los códigos de color).");
                        es_es.addDefault("commands.addhead.id", "{header} Escriba el nombre para mostrar de la cabeza (incluido Escriba la ID de la cabeza (por ejemplo, brown_sheep, red_flower_bush)");
                        es_es.addDefault("commands.addhead.id-taken", "{header} ¡Se ha tomado esa identificación (&c{id}&7)!");
                        es_es.addDefault("commands.addhead.price", "{header} Escriba el precio de la cabeza (o \"default\" para el valor predeterminado).");
                        es_es.addDefault("commands.addhead.section", "{header} Escriba la sección en la que se colocará la cabeza. (Secciones disponibles: &c{secciones}&7)");
                        es_es.addDefault("commands.addhead.texture", "{header} Escriba o copie y pegue la textura de la cabeza. (Esto puede requerir varios mensajes, así que cuando haya terminado, escriba \"done\" en el chat e ingrese).");
                        es_es.addDefault("inventory.icon.challenge.progress", "&7Progreso &8❱ &c{heads}&7/&c{total}");
                        es_es.addDefault("inventory.icon.challenge.pinned", "&c¡Anclado!");
                        es_es.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lDesafíos fijados &8❱");
                        es_es.addDefault("commands.debug.verbose.enabled", "{header} ¡Habilitado la depuración detallada para el evento &c{event} &7y los argumentos &c{args}&7!");
                        es_es.addDefault("commands.debug.verbose.disabled", "{header} Deshabilitada la depuración detallada!");
                        es_es.addDefault("commands.restore.restored-head", "{header} ¡Restaurado/a {cabeza}!");
                        es_es.addDefault("commands.restore.invalid-head", "{header} ¡{head} no es una identificación válida!");
                        es_es.addDefault("descriptions.hp.restore", "Restaura o repara un cabezal que ya fue proporcionado por el complemento.");
                        es_es.set("version", version);
                        es_es.options().copyDefaults(true);
                        try {
                            es_es.save(new File(dest, "es_es.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        fr_fr.addDefault("language", "Français (FR)");
                        fr_fr.addDefault("commands.errors.not-a-player", "{header} Vous devez être un joueur pour exécuter cette commande / sous-commande!");
                        fr_fr.addDefault("commands.locale.invalid-lang", "{header} Ce n'est pas une langue valable! Langues disponibles: &c{languages}");
                        fr_fr.addDefault("commands.locale.changed-locale", "{header} Bonjour! Votre langue est désormais définie sur &cFrançais (FR)");
                        fr_fr.addDefault("commands.locale.changed-locale-other", "{header} La langue de &c{player} &7est désormais définie sur &c{language}&7!");
                        fr_fr.addDefault("inventory.icon.challenge.reward", "&6Récompense: &a{reward}");
                        fr_fr.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        fr_fr.addDefault("inventory.icon.challenge.count", "&7{challenge-count} défis");
                        fr_fr.addDefault("inventory.icon.close", "&8❰ &c&lFermer le menu &8❱");
                        fr_fr.addDefault("inventory.icon.favourites", "&8❰ &b&lTêtes Préférées &8❱");
                        fr_fr.addDefault("inventory.icon.head.price", "&cPrix &8❱ &7{price}");
                        fr_fr.addDefault("inventory.icon.head.favourite", "&cPréférée!");
                        fr_fr.addDefault("inventory.icon.head.count", "&7{head-count} têtes");
                        fr_fr.addDefault("inventory.icon.menu", "&8❰ &a&lMenu principal &8❱");
                        fr_fr.addDefault("inventory.icon.start", "&8❰ &a&lPremière page &8❱");
                        fr_fr.addDefault("inventory.icon.last", "&8❰ &a&lDernière page &8❱");
                        fr_fr.addDefault("inventory.icon.back", "&8❰ &a&lRetour &8❱");
                        fr_fr.addDefault("inventory.icon.back-2", "&8❰ &a&lRetour (2) &8❱");
                        fr_fr.addDefault("inventory.icon.back-3", "&8❰ &a&lRetour (3) &8❱");
                        fr_fr.addDefault("inventory.icon.next", "&8❰ &a&lProchain &8❱");
                        fr_fr.addDefault("inventory.icon.next-2", "&8❰ &a&lProchain (2) &8❱");
                        fr_fr.addDefault("inventory.icon.next-3", "&8❰ &a&lProchain (3) &8❱");
                        fr_fr.addDefault("inventory.icon.search", "&8❰ &e&lTêtes de recherche &8❱");
                        fr_fr.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStatistiques &8❱");
                        fr_fr.addDefault("inventory.icon.stats.total-heads", "&aTêtes totales &8❱ &e");
                        fr_fr.addDefault("inventory.icon.stats.total-pages", "&aPages totales &8❱ &e");
                        fr_fr.addDefault("inventory.icon.stats.total-sections", "&aSections totales &8❱ &e");
                        fr_fr.addDefault("inventory.icon.stats.current-balance", "&aSolde actuel &8❱ &e");
                        fr_fr.addDefault("inventory.icon.stats.current-section", "&aSection actuelle &8❱ &e");
                        fr_fr.addDefault("textmenus.profile.player", "Joueu(r/se)");
                        fr_fr.addDefault("textmenus.profile.completed-challenges", "Défis terminés");
                        fr_fr.addDefault("textmenus.profile.total-heads-dropped", "Total des têtes chuté");
                        fr_fr.addDefault("textmenus.profile.total-heads-sold", "Total des têtes vendues");
                        fr_fr.addDefault("textmenus.profile.total-heads-crafted", "Total de têtes fabriquées");
                        fr_fr.addDefault("textmenus.profile.current-level", "Niveau actuel");
                        fr_fr.addDefault("textmenus.profile.xp-until-next-level", "XP jusqu'au niveau suivant");
                        fr_fr.addDefault("textmenus.blacklist", "Blacklist");
                        fr_fr.addDefault("textmenus.whitelist", "Whitelist");
                        fr_fr.addDefault("textmenus.blacklistw", "Blacklist du monde");
                        fr_fr.addDefault("textmenus.whitelistw", "Whitelist du monde");
                        fr_fr.addDefault("textmenus.info.version", "Version");
                        fr_fr.addDefault("textmenus.info.author", "Auteure");
                        fr_fr.addDefault("textmenus.info.language", "Langue");
                        fr_fr.addDefault("textmenus.info.contributors", "Contributeurs");
                        fr_fr.addDefault("textmenus.info.spigot", "SpigotMC lein");
                        fr_fr.addDefault("textmenus.info.discord", "Serveur Discord");
                        fr_fr.addDefault("textmenus.info.github", "Github");
                        fr_fr.addDefault("textmenus.head-info.type", "Sorte");
                        fr_fr.addDefault("textmenus.head-info.display-name", "Afficher un nom");
                        fr_fr.addDefault("textmenus.head-info.price", "Prix");
                        fr_fr.addDefault("textmenus.head-info.interact-name", "Nom d'interaction");
                        fr_fr.addDefault("textmenus.head-info.chance", "Possibilité");
                        fr_fr.addDefault("textmenus.help.usage", "Usage");
                        fr_fr.addDefault("textmenus.help.description", "Description");
                        fr_fr.addDefault("textmenus.help.permission", "Autorisation");
                        fr_fr.addDefault("textmenus.help.further-usages", "Autres Usages");
                        fr_fr.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        fr_fr.addDefault("inventory.icon.reward.group-add", "&7Groupe {group} &7(&a+&7)");
                        fr_fr.addDefault("inventory.icon.reward.group-remove", "&7Groupe {group} &7(&c-&7)");
                        fr_fr.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(s)");
                        fr_fr.addDefault("commands.addhead.bad-texture", "{header} La texture que vous avez fournie n'est pas valide. Il doit s'agir d'une URL de texture Minecraft (https://textures.minecraft.net) ou d'une chaîne encodée en Base64.");
                        fr_fr.addDefault("commands.addhead.cancelled", "{header} Création de tête annulée.");
                        fr_fr.addDefault("commands.addhead.custom-head-added", "{header} Ajout d'une nouvelle tête avec ID &c{id}&7!");
                        fr_fr.addDefault("commands.addhead.displayname", "{header} Tapez le nom d'affichage de la tête (y compris les codes de couleur).");
                        fr_fr.addDefault("commands.addhead.id", "{header} Tapez l'ID de la tête (par exemple brown_sheep, red_flower_bush).");
                        fr_fr.addDefault("commands.addhead.id-taken", "{header} Cet ID a été pris (&c{id}&7)!");
                        fr_fr.addDefault("commands.addhead.price", "{header} Tapez le prix de la tête (ou \"default\" pour la valeur par défaut).");
                        fr_fr.addDefault("commands.addhead.section", "{header} Tapez la section dans laquelle la tête sera insérée. (Sections disponibles: &c{sections}&7)");
                        fr_fr.addDefault("commands.addhead.texture", "{header} Tapez ou copiez et collez la texture de la tête. (Cela peut nécessiter plusieurs messages, donc lorsque vous avez terminé, tapez \"done\" dans le chat et entrez.)");
                        fr_fr.addDefault("inventory.icon.challenge.progress", "&7Le progrès &8❱ &c{heads}&7/&c{total}");
                        fr_fr.addDefault("inventory.icon.challenge.pinned", "&cÉpinglé!");
                        fr_fr.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lDéfis épinglés &8❱");
                        fr_fr.addDefault("commands.debug.verbose.enabled", "{header} Activé le débogage détaillé pour l'événement &c{event} &7et les arguments &c{args}&7!");
                        fr_fr.addDefault("commands.debug.verbose.disabled", "{header} Désactivé le débogage détaillé!");
                        fr_fr.addDefault("commands.restore.restored-head", "{header} Restauré {head}!");
                        fr_fr.addDefault("commands.restore.invalid-head", "{header} {head} n''est pas un identifiant valide!");
                        fr_fr.addDefault("descriptions.hp.restore", "Restaure ou répare une tête déjà fournie par le plugin.");
                        fr_fr.set("version", version);
                        fr_fr.options().copyDefaults(true);
                        try {
                            fr_fr.save(new File(dest, "fr_fr.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        hu_hu.addDefault("language", "Magyar (MA)");
                        hu_hu.addDefault("commands.errors.not-a-player", "{header} A parancs/alparancs futtatásához játékosnak kell lennie!");
                        hu_hu.addDefault("commands.locale.invalid-lang", "{header} Ez nem érvényes nyelv! Elérhető nyelvek: &c{languages}");
                        hu_hu.addDefault("commands.locale.changed-locale", "{header} Helló! Az Ön nyelve most &cmagyarra &7van állítva &c(MA)");
                        hu_hu.addDefault("commands.locale.changed-locale-other", "{header} A (z) &c{player} &7nyelve most &c{language} &7lett!");
                        hu_hu.addDefault("inventory.icon.challenge.reward", "&6Jutalom: &a{reward}");
                        hu_hu.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        hu_hu.addDefault("inventory.icon.challenge.count", "&7{challenge-count} kihívások");
                        hu_hu.addDefault("inventory.icon.close", "&8❰ &c&lBezárás menü &8❱");
                        hu_hu.addDefault("inventory.icon.favourites", "&8❰ &b&lÉrdekes &8❱");
                        hu_hu.addDefault("inventory.icon.head.price", "&cÁr &8❱ &7{price}");
                        hu_hu.addDefault("inventory.icon.head.favourite", "&cKedvenc!");
                        hu_hu.addDefault("inventory.icon.head.count", "&7{head-count} fejek");
                        hu_hu.addDefault("inventory.icon.menu", "&8❰ &a&lFőmenü &8❱");
                        hu_hu.addDefault("inventory.icon.start", "&8❰ &a&lElső oldal &8❱");
                        hu_hu.addDefault("inventory.icon.last", "&8❰ &a&lUtolsó oldal &8❱");
                        hu_hu.addDefault("inventory.icon.back", "&8❰ &a&lMenjen vissza &8❱");
                        hu_hu.addDefault("inventory.icon.back-2", "&8❰ &a&lMenjen vissza (2) &8❱");
                        hu_hu.addDefault("inventory.icon.back-3", "&8❰ &a&lMenjen vissza (3) &8❱");
                        hu_hu.addDefault("inventory.icon.next", "&8❰ &a&lKövetkező &8❱");
                        hu_hu.addDefault("inventory.icon.next-2", "&8❰ &a&lKövetkező (2) &8❱");
                        hu_hu.addDefault("inventory.icon.next-3", "&8❰ &a&lKövetkező (3) &8❱");
                        hu_hu.addDefault("inventory.icon.search", "&8❰ &e&lKeresési fejek &8❱");
                        hu_hu.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStatisztikák &8❱");
                        hu_hu.addDefault("inventory.icon.stats.total-heads", "&aÖsszes fej &8❱ &e");
                        hu_hu.addDefault("inventory.icon.stats.total-pages", "&aÖsszes oldal &8❱ &e");
                        hu_hu.addDefault("inventory.icon.stats.total-sections", "&aÖsszes szakasz &8❱ &e");
                        hu_hu.addDefault("inventory.icon.stats.current-balance", "&aAktuális egyenleg &8❱ &e");
                        hu_hu.addDefault("inventory.icon.stats.current-section", "&aAktuális szakasz &8❱ &e");
                        hu_hu.addDefault("textmenus.profile.player", "Lejátszó");
                        hu_hu.addDefault("textmenus.profile.completed-challenges", "Befejezett kihívások");
                        hu_hu.addDefault("textmenus.profile.total-heads-dropped", "Az összes fej csökkent");
                        hu_hu.addDefault("textmenus.profile.total-heads-sold", "Eladott összes fej");
                        hu_hu.addDefault("textmenus.profile.total-heads-crafted", "Összes készített fej");
                        hu_hu.addDefault("textmenus.profile.current-level", "Jelenlegi szint");
                        hu_hu.addDefault("textmenus.profile.xp-until-next-level", "XP a következő szintre");
                        hu_hu.addDefault("textmenus.blacklist", "Feketelista");
                        hu_hu.addDefault("textmenus.whitelist", "Fehérlista");
                        hu_hu.addDefault("textmenus.blacklistw", "Világ Feketelista");
                        hu_hu.addDefault("textmenus.whitelistw", "Világ Fehérlista");
                        hu_hu.addDefault("textmenus.info.version", "Változat");
                        hu_hu.addDefault("textmenus.info.author", "Szerző");
                        hu_hu.addDefault("textmenus.info.language", "Nyelv");
                        hu_hu.addDefault("textmenus.info.contributors", "Közreműködők");
                        hu_hu.addDefault("textmenus.info.spigot", "SpigotMC link");
                        hu_hu.addDefault("textmenus.info.discord", "Discord Server");
                        hu_hu.addDefault("textmenus.info.github", "Github");
                        hu_hu.addDefault("textmenus.head-info.type", "Típus");
                        hu_hu.addDefault("textmenus.head-info.display-name", "Megjelenítendő név");
                        hu_hu.addDefault("textmenus.head-info.price", "Ár");
                        hu_hu.addDefault("textmenus.head-info.interact-name", "Interact név");
                        hu_hu.addDefault("textmenus.head-info.chance", "Véletlen");
                        hu_hu.addDefault("textmenus.help.usage", "Használat");
                        hu_hu.addDefault("textmenus.help.description", "Leírás");
                        hu_hu.addDefault("textmenus.help.permission", "Permission");
                        hu_hu.addDefault("textmenus.help.further-usages", "További felhasználások");
                        hu_hu.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        hu_hu.addDefault("inventory.icon.reward.group-add", "&7Csoport {group} &7(&a+&7)");
                        hu_hu.addDefault("inventory.icon.reward.group-remove", "&7Csoport {group} &7(&c-&7)");
                        hu_hu.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(ok)");
                        hu_hu.addDefault("commands.addhead.bad-texture", "{header} A megadott textúra érvénytelen. Ennek Minecraft textúra URL-jének (https://textures.minecraft.net) vagy Base64 kódolt karakterláncnak kell lennie.");
                        hu_hu.addDefault("commands.addhead.cancelled", "{header} A fej létrehozása megszakítva.");
                        hu_hu.addDefault("commands.addhead.custom-head-added", "{header} Hozzáadott új fej, azonosítóval &c{id}&7!");
                        hu_hu.addDefault("commands.addhead.displayname", "{header} Írja be a fej megjelenített nevét (a színkódokkal együtt).");
                        hu_hu.addDefault("commands.addhead.id", "{header} Írja be a fej azonosítóját (például brown_sheep, red_flower_bush)");
                        hu_hu.addDefault("commands.addhead.id-taken", "{header} Ezt az azonosítót elvették (&c{id}&7)!");
                        hu_hu.addDefault("commands.addhead.price", "{header} Írja be a fej árát (vagy az \"alapértelmezett\" az alapértelmezett értékhez).");
                        hu_hu.addDefault("commands.addhead.section", "{header} Írja be azt a szekciót, amelybe a fej kerül. (Elérhető szakaszok: &c{szakaszok}&7)");
                        hu_hu.addDefault("commands.addhead.texture", "{header} Írja be, vagy másolja és illessze be a fej textúráját. (Ehhez több üzenet szükséges, ezért ha kész, írja be a \"kész\" elemet a chatbe, és írja be.)");
                        hu_hu.addDefault("inventory.icon.challenge.progress", "&7Haladás &8❱ &c{heads}&7/&c{total}");
                        hu_hu.addDefault("inventory.icon.challenge.pinned", "&cRögzítette!");
                        hu_hu.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lÖsszetett kihívások &8❱");
                        hu_hu.addDefault("commands.debug.verbose.enabled", "{header} Engedélyezte az &c{event} &7esemény és az &c{args} &7argumentumok hibakereső részletezését!");
                        hu_hu.addDefault("commands.debug.verbose.disabled", "{header} Letiltotta a hibakeresési részleteket!");
                        hu_hu.addDefault("commands.restore.restored-head", "{header} Visszaállítva {head}!");
                        hu_hu.addDefault("commands.restore.invalid-head", "{header} A {head} nem érvényes azonosító!");
                        hu_hu.addDefault("descriptions.hp.restore", "Visszaállítja vagy megjavítja a plugin által biztosított fejet.");
                        hu_hu.set("version", version);
                        hu_hu.options().copyDefaults(true);
                        try {
                            hu_hu.save(new File(dest, "hu_hu.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        lol_us.addDefault("language", "LOLCAT (Kingdom of Cats)");
                        lol_us.addDefault("commands.errors.not-a-player", "{header} u nu playah!");
                        lol_us.addDefault("commands.locale.invalid-lang", "{header} nU such langwaaj :( kewl langs: &c{languages}");
                        lol_us.addDefault("commands.locale.changed-locale", "{header} AAHHAHAHAHAHHA, ur langwaaj iz nao de bestz! &c(LOLCAT)");
                        lol_us.addDefault("commands.locale.changed-locale-other", "{header} &c{player}&7'z langwaaj iz nao de kewl &c{language}&7!");
                        lol_us.addDefault("inventory.icon.challenge.reward", "&6Treetz: &a{reward}");
                        lol_us.addDefault("inventory.icon.challenge.xp", "&6ex-pee: &a{xp}");
                        lol_us.addDefault("inventory.icon.challenge.count", "&7{challenge-count} chaleng's");
                        lol_us.addDefault("inventory.icon.close", "&8❰ &c&lS H U T &8❱");
                        lol_us.addDefault("inventory.icon.favourites", "&8❰ &b&lkewl hedz :3 &8❱");
                        lol_us.addDefault("inventory.icon.head.price", "&cMONEYS &8❱ &7{price}");
                        lol_us.addDefault("inventory.icon.head.favourite", "&cfav tbh");
                        lol_us.addDefault("inventory.icon.head.count", "&7{head-count} hedz");
                        lol_us.addDefault("inventory.icon.menu", "&8❰ &a&lmane thingie &8❱");
                        lol_us.addDefault("inventory.icon.start", "&8❰ &a&l1st &8❱");
                        lol_us.addDefault("inventory.icon.last", "&8❰ &a&lhaha lozr page &8❱");
                        lol_us.addDefault("inventory.icon.back", "&8❰ &a&lrevrs!! &8❱");
                        lol_us.addDefault("inventory.icon.back-2", "&8❰ &a&lrevrs!!! (2) &8❱");
                        lol_us.addDefault("inventory.icon.back-3", "&8❰ &a&lrevrs!!!! (3) &8❱");
                        lol_us.addDefault("inventory.icon.next", "&8❰ &a&lNYOOOM &8❱");
                        lol_us.addDefault("inventory.icon.next-2", "&8❰ &a&lNYOOOOM (2) &8❱");
                        lol_us.addDefault("inventory.icon.next-3", "&8❰ &a&lNYOOOOOM (3) &8❱");
                        lol_us.addDefault("inventory.icon.search", "&8❰ &e&lfind dis &8❱");
                        lol_us.addDefault("inventory.icon.stats.icon", "&8❰ &a&lnumbrz &8❱");
                        lol_us.addDefault("inventory.icon.stats.total-heads", "&alotsa heds &8❱ &e");
                        lol_us.addDefault("inventory.icon.stats.total-pages", "&apagez &8❱ &e");
                        lol_us.addDefault("inventory.icon.stats.total-sections", "&asecshunz &8❱ &e");
                        lol_us.addDefault("inventory.icon.stats.current-balance", "&abalance rn &8❱ &e");
                        lol_us.addDefault("inventory.icon.stats.current-section", "&asection rn &8❱ &e");
                        lol_us.addDefault("textmenus.profile.player", "Playrr");
                        lol_us.addDefault("textmenus.profile.completed-challenges", "Complet'd Chalengs");
                        lol_us.addDefault("textmenus.profile.total-heads-dropped", "heds droppd");
                        lol_us.addDefault("textmenus.profile.total-heads-sold", "heds selld");
                        lol_us.addDefault("textmenus.profile.total-heads-crafted", "heds maid");
                        lol_us.addDefault("textmenus.profile.current-level", "level rn");
                        lol_us.addDefault("textmenus.profile.xp-until-next-level", "XP til de big one");
                        lol_us.addDefault("textmenus.blacklist", "Naughty list");
                        lol_us.addDefault("textmenus.whitelist", "Nice list");
                        lol_us.addDefault("textmenus.blacklistw", "Woldz naughty list");
                        lol_us.addDefault("textmenus.whitelistw", "Woldz nice list");
                        lol_us.addDefault("textmenus.info.version", "Vershun");
                        lol_us.addDefault("textmenus.info.author", "kool kat");
                        lol_us.addDefault("textmenus.info.language", "langwaaj");
                        lol_us.addDefault("textmenus.info.contributors", "kool kats");
                        lol_us.addDefault("textmenus.info.spigot", "SpigotMC linkz");
                        lol_us.addDefault("textmenus.info.discord", "Discord serv");
                        lol_us.addDefault("textmenus.info.github", "Gitty");
                        lol_us.addDefault("textmenus.head-info.type", "Tip");
                        lol_us.addDefault("textmenus.head-info.display-name", "publicz nam");
                        lol_us.addDefault("textmenus.head-info.price", "Moneyz");
                        lol_us.addDefault("textmenus.head-info.interact-name", "clicky name");
                        lol_us.addDefault("textmenus.head-info.chance", "maybe??");
                        lol_us.addDefault("textmenus.help.usage", "how 2");
                        lol_us.addDefault("textmenus.help.description", "uhh what");
                        lol_us.addDefault("textmenus.help.permission", "permy");
                        lol_us.addDefault("textmenus.help.further-usages", "how 2 but worse");
                        lol_us.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        lol_us.addDefault("inventory.icon.reward.group-add", "&7Groop {group} &7(&a+&7)");
                        lol_us.addDefault("inventory.icon.reward.group-remove", "&7Groop {group} &7(&c-&7)");
                        lol_us.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(z)");
                        lol_us.addDefault("commands.addhead.bad-texture", "{header} Deh texturrr u hav givn iz nawt ok. It muzt be a Minecraft texturrr URL (https://textures.minecraft.net) or a Base64 yarn");
                        lol_us.addDefault("commands.addhead.cancelled", "{header} stop'd makin heds.");
                        lol_us.addDefault("commands.addhead.custom-head-added", "{header} Add'd new hed wif ID &c{id}&7!");
                        lol_us.addDefault("commands.addhead.displayname", "{header} Putz name of hed here (including colour codes).");
                        lol_us.addDefault("commands.addhead.id", "{header} Putz ID of hed here (leik brown_sheep, red_flower_bush)");
                        lol_us.addDefault("commands.addhead.id-taken", "{header} oH NO dat ID got stole D: (&c{id}&7)!");
                        lol_us.addDefault("commands.addhead.price", "{header} Putz price of hed here (or \"default\" 4 de normal).");
                        lol_us.addDefault("commands.addhead.section", "{header} Putz secshun of hed here (secshuns rn: &c{sections}&7)");
                        lol_us.addDefault("commands.addhead.texture", "{header} Putz texturrr of hed here (may needz lotsa lettrs, so when dun, screm \"done\" in de chat)");
                        lol_us.addDefault("inventory.icon.challenge.progress", "&7rn &8❱ &c{heads}&7/&c{total}");
                        lol_us.addDefault("inventory.icon.challenge.pinned", "&cSHOT!");
                        lol_us.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lshot chal &8❱");
                        lol_us.addDefault("commands.debug.verbose.enabled", "{header} kewl thingie on 4 event &c{event} &7'n' arrrrrgs &c{args}&7!");
                        lol_us.addDefault("commands.debug.verbose.disabled", "{header} kewl thingie iz off!");
                        lol_us.addDefault("commands.restore.restored-head", "{header} summumd {head}!");
                        lol_us.addDefault("commands.restore.invalid-head", "{header} {head} iz not ur frend :(");
                        lol_us.addDefault("descriptions.hp.restore", "summun an old frend");

                        lol_us.set("version", version);
                        lol_us.options().copyDefaults(true);
                        try {
                            lol_us.save(new File(dest, "lol_us.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        pl_pl.addDefault("language", "Polski (PL)");
                        pl_pl.addDefault("commands.errors.not-a-player", "{header} Musisz być graczem, aby uruchomić to polecenie/podkomendę!");
                        pl_pl.addDefault("commands.locale.invalid-lang", "{header} To nie jest poprawny język! Dostępne języki: &c{languages}");
                        pl_pl.addDefault("commands.locale.changed-locale", "{header} Dobry dzień! Twój język jest teraz ustawiony na &cPolski (PL)");
                        pl_pl.addDefault("commands.locale.changed-locale-other", "{header} Język &c{player} &7jest teraz ustawiony na &c{language}&7!");
                        pl_pl.addDefault("inventory.icon.challenge.reward", "&6Nagroda: &a{reward}");
                        pl_pl.addDefault("inventory.icon.challenge.xp", "&6Dośw.: &a{xp}");
                        pl_pl.addDefault("inventory.icon.challenge.count", "&7{challenge-count} wyzwania");
                        pl_pl.addDefault("inventory.icon.close", "&8❰ &c&lZamknij menu &8❱");
                        pl_pl.addDefault("inventory.icon.favourites", "&8❰ &b&lUlubione &8❱");
                        pl_pl.addDefault("inventory.icon.head.price", "&cCena &8❱ &7{price}");
                        pl_pl.addDefault("inventory.icon.head.favourite", "&cUlubiona!");
                        pl_pl.addDefault("inventory.icon.head.count", "&7{head-count} głowy");
                        pl_pl.addDefault("inventory.icon.menu", "&8❰ &a&lMenu główne &8❱");
                        pl_pl.addDefault("inventory.icon.start", "&8❰ &a&lPierwsza strona &8❱");
                        pl_pl.addDefault("inventory.icon.last", "&8❰ &a&lOstatnia strona &8❱");
                        pl_pl.addDefault("inventory.icon.back", "&8❰ &a&lWróć &8❱");
                        pl_pl.addDefault("inventory.icon.back-2", "&8❰ &a&lWróć (2) &8❱");
                        pl_pl.addDefault("inventory.icon.back-3", "&8❰ &a&lWróć (3) &8❱");
                        pl_pl.addDefault("inventory.icon.next", "&8❰ &a&lNastępna &8❱");
                        pl_pl.addDefault("inventory.icon.next-2", "&8❰ &a&lNastępna (2) &8❱");
                        pl_pl.addDefault("inventory.icon.next-3", "&8❰ &a&lNastępna (3) &8❱");
                        pl_pl.addDefault("inventory.icon.search", "&8❰ &e&lSzukaj głów &8❱");
                        pl_pl.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStatystyka &8❱");
                        pl_pl.addDefault("inventory.icon.stats.total-heads", "&aŁączna liczba głów &8❱ &e");
                        pl_pl.addDefault("inventory.icon.stats.total-pages", "&aWszystkie strony &8❱ &e");
                        pl_pl.addDefault("inventory.icon.stats.total-sections", "&aWszystkie sekcje &8❱ &e");
                        pl_pl.addDefault("inventory.icon.stats.current-balance", "&aAktualne saldo &8❱ &e");
                        pl_pl.addDefault("inventory.icon.stats.current-section", "&aAktualna sekcja &8❱ &e");
                        pl_pl.addDefault("textmenus.profile.player", "Gracz");
                        pl_pl.addDefault("textmenus.profile.completed-challenges", "Ukończone wyzwania");
                        pl_pl.addDefault("textmenus.profile.total-heads-dropped", "Łączna liczba upuszczonych głów");
                        pl_pl.addDefault("textmenus.profile.total-heads-sold", "Całkowita liczba sprzedanych głów");
                        pl_pl.addDefault("textmenus.profile.total-heads-crafted", "Łączna liczba wytworzonych głów");
                        pl_pl.addDefault("textmenus.profile.current-level", "Aktualny poziom");
                        pl_pl.addDefault("textmenus.profile.xp-until-next-level", "Dośw. do następnego poziomu");
                        pl_pl.addDefault("textmenus.blacklist", "Czarna lista");
                        pl_pl.addDefault("textmenus.whitelist", "Biała lista");
                        pl_pl.addDefault("textmenus.blacklistw", "Czarna lista światów");
                        pl_pl.addDefault("textmenus.whitelistw", "Biała lista światów");
                        pl_pl.addDefault("textmenus.info.version", "Wersja");
                        pl_pl.addDefault("textmenus.info.author", "Autor");
                        pl_pl.addDefault("textmenus.info.language", "Język");
                        pl_pl.addDefault("textmenus.info.contributors", "Współtwórcy");
                        pl_pl.addDefault("textmenus.info.spigot", "Łącze SpigotMC");
                        pl_pl.addDefault("textmenus.info.discord", "Serwer Discord");
                        pl_pl.addDefault("textmenus.info.github", "Github");
                        pl_pl.addDefault("textmenus.head-info.type", "Rodzaj");
                        pl_pl.addDefault("textmenus.head-info.display-name", "Wyświetlana nazwa");
                        pl_pl.addDefault("textmenus.head-info.price", "Cena");
                        pl_pl.addDefault("textmenus.head-info.interact-name", "Nazwa interakcji");
                        pl_pl.addDefault("textmenus.head-info.chance", "Szansa");
                        pl_pl.addDefault("textmenus.help.usage", "Stosowanie");
                        pl_pl.addDefault("textmenus.help.description", "Opis");
                        pl_pl.addDefault("textmenus.help.permission", "Uprawnienie");
                        pl_pl.addDefault("textmenus.help.further-usages", "Dalsze zastosowania");
                        pl_pl.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        pl_pl.addDefault("inventory.icon.reward.group-add", "&7Grupa {group} &7(&a+&7)");
                        pl_pl.addDefault("inventory.icon.reward.group-remove", "&7Grupa {group} &7(&c-&7)");
                        pl_pl.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(y)");
                        pl_pl.addDefault("commands.addhead.bad-texture", "{header} Podana tekstura jest nieprawidłowa. Musi to być adres URL tekstury Minecraft (https://textures.minecraft.net) lub ciąg zakodowany w Base64.");
                        pl_pl.addDefault("commands.addhead.cancelled", "{header} Anulowano tworzenie głowy.");
                        pl_pl.addDefault("commands.addhead.custom-head-added", "{header} Dodano nową głowę z identyfikatorem &c{id}&7!");
                        pl_pl.addDefault("commands.addhead.displayname", "{header} Wpisz nazwę wyświetlaną głowy (w tym kody kolorów).");
                        pl_pl.addDefault("commands.addhead.id", "{header} Wpisz identyfikator głowy (np. brown_sheep, red_flower_bush)");
                        pl_pl.addDefault("commands.addhead.id-taken", "{header} Ten identyfikator został zabrany (&c{id}&7)!");
                        pl_pl.addDefault("commands.addhead.price", "{header} Wpisz cenę głowicy (lub „default” dla wartości domyślnej).");
                        pl_pl.addDefault("commands.addhead.section", "{header} Wpisz sekcję, w której zostanie umieszczona głowa. (Dostępne sekcje: &c{sections}&7)");
                        pl_pl.addDefault("commands.addhead.texture", "{header} Wpisz lub skopiuj i wklej teksturę głowy. (Może to wymagać kilku wiadomości, więc kiedy skończysz, wpisz „done” na czacie i wejdź).");
                        pl_pl.addDefault("inventory.icon.challenge.progress", "&7Postęp &8❱ &c{heads}&7/&c{total}");
                        pl_pl.addDefault("inventory.icon.challenge.pinned", "&cPrzypięte!");
                        pl_pl.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lPrzypięte wyzwania &8❱");
                        pl_pl.addDefault("commands.debug.verbose.enabled", "{header} Włączono szczegółowe debugowanie dla zdarzenia &c{event} &7i argumentów &c{args}&7!");
                        pl_pl.addDefault("commands.debug.verbose.disabled", "{header} Wyłączono szczegółowe debugowanie!");
                        pl_pl.addDefault("commands.restore.restored-head", "{header} Przywrócono {head}!");
                        pl_pl.addDefault("commands.restore.invalid-head", "{header} {head} nie jest prawidłowym identyfikatorem!");
                        pl_pl.addDefault("descriptions.hp.restore", "Przywraca lub naprawia głowicę, która została już dostarczona przez wtyczkę.");

                        pl_pl.set("version", version);
                        pl_pl.options().copyDefaults(true);
                        try {
                            pl_pl.save(new File(dest, "pl_pl.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        ro_ro.addDefault("language", "Romana (RO)");
                        ro_ro.addDefault("commands.errors.not-a-player", "{header} Trebuie sa fiți un jucator pentru a rula aceasta comanda/subcomanda!");
                        ro_ro.addDefault("commands.locale.invalid-lang", "{header} Acesta nu este un limbaj valabil! Limbi disponibile: &c{languages}");
                        ro_ro.addDefault("commands.locale.changed-locale", "{header} Salut! Limba dvs. este acum setata pe limba &cRomana (RO)");
                        ro_ro.addDefault("commands.locale.changed-locale-other", "{header} Limba &c{player} &7este acum setata pe &c{language}&7!");
                        ro_ro.addDefault("inventory.icon.challenge.reward", "&6Recompensa: &a{reward}");
                        ro_ro.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        ro_ro.addDefault("inventory.icon.challenge.count", "&7{challenge-count} provocari");
                        ro_ro.addDefault("inventory.icon.close", "&8❰ &c&lInchideti meniul &8❱");
                        ro_ro.addDefault("inventory.icon.favourites", "&8❰ &b&lFavorite &8❱");
                        ro_ro.addDefault("inventory.icon.head.price", "&cPret &8❱ &7{price}");
                        ro_ro.addDefault("inventory.icon.head.favourite", "&cFavorita!");
                        ro_ro.addDefault("inventory.icon.head.count", "&7{head-count} capete");
                        ro_ro.addDefault("inventory.icon.menu", "&8❰ &a&lMeniu principal &8❱");
                        ro_ro.addDefault("inventory.icon.start", "&8❰ &a&lPrima pagina &8❱");
                        ro_ro.addDefault("inventory.icon.last", "&8❰ &a&lUltima pagina &8❱");
                        ro_ro.addDefault("inventory.icon.back", "&8❰ &a&lReveni &8❱");
                        ro_ro.addDefault("inventory.icon.back-2", "&8❰ &a&lReveni (2) &8❱");
                        ro_ro.addDefault("inventory.icon.back-3", "&8❰ &a&lReveni (3) &8❱");
                        ro_ro.addDefault("inventory.icon.next", "&8❰ &a&lUrmator &8❱");
                        ro_ro.addDefault("inventory.icon.next-2", "&8❰ &a&lUrmator (2) &8❱");
                        ro_ro.addDefault("inventory.icon.next-3", "&8❰ &a&lUrmator (3) &8❱");
                        ro_ro.addDefault("inventory.icon.search", "&8❰ &e&lCapete de cautare &8❱");
                        ro_ro.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStatistici &8❱");
                        ro_ro.addDefault("inventory.icon.stats.total-heads", "&aCapete totale &8❱ &e");
                        ro_ro.addDefault("inventory.icon.stats.total-pages", "&aTotal pagini &8❱ &e");
                        ro_ro.addDefault("inventory.icon.stats.total-sections", "&aSectiuni totale &8❱ &e");
                        ro_ro.addDefault("inventory.icon.stats.current-balance", "&aSold curent &8❱ &e");
                        ro_ro.addDefault("inventory.icon.stats.current-section", "&aSecțiunea actuală &8❱ &e");
                        ro_ro.addDefault("textmenus.profile.player", "Jucator");
                        ro_ro.addDefault("textmenus.profile.completed-challenges", "Provocari finalizate");
                        ro_ro.addDefault("textmenus.profile.total-heads-dropped", "Total capete aruncate");
                        ro_ro.addDefault("textmenus.profile.total-heads-sold", "Total capete vandute");
                        ro_ro.addDefault("textmenus.profile.total-heads-crafted", "Total capete artizanale");
                        ro_ro.addDefault("textmenus.profile.current-level", "Nivelul actual");
                        ro_ro.addDefault("textmenus.profile.xp-until-next-level", "XP pana la nivelul urmator");
                        ro_ro.addDefault("textmenus.blacklist", "Lista neagra");
                        ro_ro.addDefault("textmenus.whitelist", "Lista alba");
                        ro_ro.addDefault("textmenus.blacklistw", "Lista neagra lumilor");
                        ro_ro.addDefault("textmenus.whitelistw", "Lista alba lumilor");
                        ro_ro.addDefault("textmenus.info.version", "Versiune");
                        ro_ro.addDefault("textmenus.info.author", "Autor");
                        ro_ro.addDefault("textmenus.info.language", "Limba");
                        ro_ro.addDefault("textmenus.info.contributors", "Contribuabili");
                        ro_ro.addDefault("textmenus.info.spigot", "SpigotMC link");
                        ro_ro.addDefault("textmenus.info.discord", "Discord Server");
                        ro_ro.addDefault("textmenus.info.github", "Github");
                        ro_ro.addDefault("textmenus.head-info.type", "Categorie");
                        ro_ro.addDefault("textmenus.head-info.display-name", "Numele afisat");
                        ro_ro.addDefault("textmenus.head-info.price", "Pret");
                        ro_ro.addDefault("textmenus.head-info.interact-name", "Numele interactiunii");
                        ro_ro.addDefault("textmenus.head-info.chance", "Sansa");
                        ro_ro.addDefault("textmenus.help.usage", "Folosire");
                        ro_ro.addDefault("textmenus.help.description", "Descriere");
                        ro_ro.addDefault("textmenus.help.permission", "Permisiune");
                        ro_ro.addDefault("textmenus.help.further-usages", "Utilizari ulterioare");
                        ro_ro.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        ro_ro.addDefault("inventory.icon.reward.group-add", "&7Grupul {group} &7(&a+&7)");
                        ro_ro.addDefault("inventory.icon.reward.group-remove", "&7Grupul {group} &7(&c-&7)");
                        ro_ro.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(e)");
                        ro_ro.addDefault("commands.addhead.bad-texture", "{header} Textura pe care ati furnizat-o este nevalida. Trebuie sa fie o adresa URL de textura Minecraft (https://textures.minecraft.net) sau un sir codat Base64.");
                        ro_ro.addDefault("commands.addhead.cancelled", "{header} Crearea capului anulata.");
                        ro_ro.addDefault("commands.addhead.custom-head-added", "{header} S-a adaugat un nou cap cu ID &c{id}&7!");
                        ro_ro.addDefault("commands.addhead.displayname", "{header} Introduceti numele afisat pentru cap (inclusiv codurile de culoare).");
                        ro_ro.addDefault("commands.addhead.id", "{header} Introduceti ID-ul capului (de exemplu, brown_sheep, red_flower_bush)");
                        ro_ro.addDefault("commands.addhead.id-taken", "{header} ID-ul a fost luat (&c{id}&7)!");
                        ro_ro.addDefault("commands.addhead.price", "{header} Introduceti pretul capului (sau „default” pentru valoarea implicita).");
                        ro_ro.addDefault("commands.addhead.section", "{header} Tastati sectiunea in care va fi introdus capul (sectiuni disponibile: &c{section}&7)");
                        ro_ro.addDefault("commands.addhead.texture", "{header} Introduceți sau copiați și lipiți textura pentru cap. (Acest lucru poate necesita mai multe mesaje, așa că atunci când ați terminat, tastați „done” în chat și introduceți.)");
                        ro_ro.addDefault("inventory.icon.challenge.progress", "&7Progres &8❱ &c{heads}&7/&c{total}");
                        ro_ro.addDefault("inventory.icon.challenge.pinned", "&cFixat!");
                        ro_ro.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lProvocari fixate &8❱");
                        ro_ro.addDefault("commands.debug.verbose.enabled", "{header} A activat depanarea detaliată pentru eveniment &c{event} &7și argumente &c{args}&7!");
                        ro_ro.addDefault("commands.debug.verbose.disabled", "{header} Dezactivat detaliile de depanare!");
                        ro_ro.addDefault("commands.restore.restored-head", "{header} {head} restaurat!");
                        ro_ro.addDefault("commands.restore.invalid-head", "{header} {head} nu este un ID valid!");
                        ro_ro.addDefault("descriptions.hp.restore", "Restaureaza sau repara un cap care a fost deja furnizat de plugin.");

                        ro_ro.set("version", version);
                        ro_ro.options().copyDefaults(true);
                        try {
                            ro_ro.save(new File(dest, "ro_ro.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        ru_ru.addDefault("language", "Русский (RU)");
                        ru_ru.addDefault("commands.errors.not-a-player", "{header} Вы должны быть игроком для запуска этой команды/подкоманды!");
                        ru_ru.addDefault("commands.locale.invalid-lang", "{header} Это не правильный язык! Доступные языки: &c{languages}");
                        ru_ru.addDefault("commands.locale.changed-locale", "{header} Здравствуйте! Ваш язык теперь установлен на &cрусский (RU)");
                        ru_ru.addDefault("commands.locale.changed-locale-other", "{header} Язык &c{player} &7теперь установлен на &c{language}&7!");
                        ru_ru.addDefault("inventory.icon.challenge.reward", "&6Награда: &a{reward}");
                        ru_ru.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
                        ru_ru.addDefault("inventory.icon.challenge.count", "&7{challenge-count} проблемы");
                        ru_ru.addDefault("inventory.icon.close", "&8❰ &c&lЗакрыть меню &8❱");
                        ru_ru.addDefault("inventory.icon.favourites", "&8❰ &b&lИзбранные &8❱");
                        ru_ru.addDefault("inventory.icon.head.price", "&cЦена &8❱ &7{price}");
                        ru_ru.addDefault("inventory.icon.head.favourite", "&cизлюбленный!");
                        ru_ru.addDefault("inventory.icon.head.count", "&7{head-count} руководители");
                        ru_ru.addDefault("inventory.icon.menu", "&8❰ &a&lГлавное меню &8❱");
                        ru_ru.addDefault("inventory.icon.start", "&8❰ &a&lПервая страница &8❱");
                        ru_ru.addDefault("inventory.icon.last", "&8❰ &a&lПредыдущая страница &8❱");
                        ru_ru.addDefault("inventory.icon.back", "&8❰ &a&lназад &8❱");
                        ru_ru.addDefault("inventory.icon.back-2", "&8❰ &a&lназад (2) &8❱");
                        ru_ru.addDefault("inventory.icon.back-3", "&8❰ &a&lназад (3) &8❱");
                        ru_ru.addDefault("inventory.icon.next", "&8❰ &a&lследующий &8❱");
                        ru_ru.addDefault("inventory.icon.next-2", "&8❰ &a&lследующий (2) &8❱");
                        ru_ru.addDefault("inventory.icon.next-3", "&8❰ &a&lследующий (3) &8❱");
                        ru_ru.addDefault("inventory.icon.search", "&8❰ &e&lПоиск головы &8❱");
                        ru_ru.addDefault("inventory.icon.stats.icon", "&8❰ &a&lСтатистика &8❱");
                        ru_ru.addDefault("inventory.icon.stats.total-heads", "&aВсего голов &8❱ &e");
                        ru_ru.addDefault("inventory.icon.stats.total-pages", "&aВсего страниц &8❱ &e");
                        ru_ru.addDefault("inventory.icon.stats.total-sections", "&aВсего разделов &8❱ &e");
                        ru_ru.addDefault("inventory.icon.stats.current-balance", "&aТекущий баланс &8❱ &e");
                        ru_ru.addDefault("inventory.icon.stats.current-section", "&aТекущий раздел &8❱ &e");
                        ru_ru.addDefault("textmenus.profile.player", "игрок");
                        ru_ru.addDefault("textmenus.profile.completed-challenges", "Завершенные испытания");
                        ru_ru.addDefault("textmenus.profile.total-heads-dropped", "Всего голов выпало");
                        ru_ru.addDefault("textmenus.profile.total-heads-sold", "Всего голов продано");
                        ru_ru.addDefault("textmenus.profile.total-heads-crafted", "Всего созданных голов");
                        ru_ru.addDefault("textmenus.profile.current-level", "Текущий уровень");
                        ru_ru.addDefault("textmenus.profile.xp-until-next-level", "XP до следующего уровня");
                        ru_ru.addDefault("textmenus.blacklist", "Черный список");
                        ru_ru.addDefault("textmenus.whitelist", "Белый список");
                        ru_ru.addDefault("textmenus.blacklistw", "Черный список миров");
                        ru_ru.addDefault("textmenus.whitelistw", "Белый список миров");
                        ru_ru.addDefault("textmenus.info.version", "Версия");
                        ru_ru.addDefault("textmenus.info.author", "автор");
                        ru_ru.addDefault("textmenus.info.language", "язык");
                        ru_ru.addDefault("textmenus.info.contributors", "Авторы");
                        ru_ru.addDefault("textmenus.info.spigot", "SpigotMC ссылка");
                        ru_ru.addDefault("textmenus.info.discord", "Discord Server");
                        ru_ru.addDefault("textmenus.info.github", "Github");
                        ru_ru.addDefault("textmenus.head-info.type", "Тип");
                        ru_ru.addDefault("textmenus.head-info.display-name", "Показать имя");
                        ru_ru.addDefault("textmenus.head-info.price", "Цена");
                        ru_ru.addDefault("textmenus.head-info.interact-name", "Имя взаимодействия");
                        ru_ru.addDefault("textmenus.head-info.chance", "возможность");
                        ru_ru.addDefault("textmenus.help.usage", "использование");
                        ru_ru.addDefault("textmenus.help.description", "Описание");
                        ru_ru.addDefault("textmenus.help.permission", "разрешение");
                        ru_ru.addDefault("textmenus.help.further-usages", "Дальнейшее использование");
                        ru_ru.addDefault("inventory.icon.reward.currency", "&7${amount}");
                        ru_ru.addDefault("inventory.icon.reward.group-add", "&7группа {group} &7(&a+&7)");
                        ru_ru.addDefault("inventory.icon.reward.group-remove", "&7группа {group} &7(&c-&7)");
                        ru_ru.addDefault("inventory.icon.reward.item-give", "&7{amount} {item}(ы)");
                        ru_ru.addDefault("commands.addhead.bad-texture", "{header} Предоставленная вами текстура недействительна. Это должен быть URL текстуры Minecraft (https://textures.minecraft.net) или строка в кодировке Base64.");
                        ru_ru.addDefault("commands.addhead.cancelled", "{header} Отменено создание головы.");
                        ru_ru.addDefault("commands.addhead.custom-head-added", "{header} Добавлена новая голова с идентификатором &c{id}&7!");
                        ru_ru.addDefault("commands.addhead.displayname", "{header} Введите отображаемое имя для головы (включая цветовые коды).");
                        ru_ru.addDefault("commands.addhead.id", "{header} Введите идентификатор головы (например, brown_sheep, red_flower_bush)");
                        ru_ru.addDefault("commands.addhead.id-taken", "{header} Этот идентификатор был взят (&c{id}&7)!");
                        ru_ru.addDefault("commands.addhead.price", "{header} Введите цену головы (или «default» для значения по умолчанию).");
                        ru_ru.addDefault("commands.addhead.section", "{header} Введите раздел, в который будет вставлена голова. (Доступные разделы: &c{sections}&7)");
                        ru_ru.addDefault("commands.addhead.texture", "{header} Введите или скопируйте и вставьте текстуру для головы. (Для этого может потребоваться несколько сообщений, поэтому, когда вы закончите, введите «done» в чате и введите.)");
                        ru_ru.addDefault("inventory.icon.challenge.progress", "&7Прогресс &8❱ &c{heads}&7/&c{total}");
                        ru_ru.addDefault("inventory.icon.challenge.pinned", "&cЗакрепленные!");
                        ru_ru.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lЗакрепленные вызовы&8❱");
                        ru_ru.addDefault("commands.debug.verbose.enabled", "{header} Включена подробная отладка для события &c{event} &7и аргументов &c{args}&7!");
                        ru_ru.addDefault("commands.debug.verbose.disabled", "{header} Отключена подробная отладка!");
                        ru_ru.addDefault("commands.restore.restored-head", "{header} Восстановил {head}!");
                        ru_ru.addDefault("commands.restore.invalid-head", "{header} {head} не является действительным идентификатором!");
                        ru_ru.addDefault("descriptions.hp.restore", "Восстанавливает или ремонтирует голову, которая уже была предоставлена плагином.");

                        ru_ru.set("version", version);
                        ru_ru.options().copyDefaults(true);
                        try {
                            ru_ru.save(new File(dest, "ru_ru.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        nl_nl.addDefault("inventory.icon.challenge.progress", "&7Vooruitgang &8❱ &c{heads}&7/&c{total}");
                        nl_nl.addDefault("inventory.icon.challenge.pinned", "&cVastgemaakt!");
                        nl_nl.addDefault("inventory.icon.pinned-challenges", "&8❰ &b&lVastgezette uitdagingen &8❱");
                        nl_nl.addDefault("commands.debug.verbose.enabled", "{header} De uitgebreide debugging ingeschakeld voor gebeurtenis &c{event} &7en argumenten &c{args}&7!");
                        nl_nl.addDefault("commands.debug.verbose.disabled", "{header} De uitgebreide foutopsporing uitgeschakeld!");
                        nl_nl.addDefault("commands.restore.restored-head", "{header} {head} hersteld!");
                        nl_nl.addDefault("commands.restore.invalid-head", "{header} {head} is geen geldige ID!");
                        nl_nl.addDefault("descriptions.hp.restore", "Herstelt of repareert een kop die al door de plug-in werd geleverd.");

                        nl_nl.set("version", version);
                        nl_nl.options().copyDefaults(true);
                        try {
                            nl_nl.save(new File(dest, "nl_nl.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    {
                        zh_cn.addDefault("commands.debug.verbose.enabled", "{header} 为事件&c{event}&7和参数&c{args}&7启用了调试详细信息！");
                        zh_cn.addDefault("commands.debug.verbose.disabled", "{header} 禁用调试冗长！");
                        zh_cn.addDefault("commands.restore.restored-head", "{header} 已恢复{head}!");
                        zh_cn.addDefault("commands.restore.invalid-head", "{header} {head}不是有效的ID!");
                        zh_cn.addDefault("descriptions.hp.restore", "恢复或修复插件已提供的头部.");

                        zh_cn.set("version", version);
                        zh_cn.options().copyDefaults(true);
                        try {
                            zh_cn.save(new File(dest, "zh_cn.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.runTaskAsynchronously(hp);
        }
    }

    public String getString(String path) {
        String str = config.getString(path);
        if (str == null) return "";
        str = str.replaceAll("\\{header}", config.getString("prefix"));
        str = str.replaceAll("''", "'");
        str = str.replaceAll("^'", "");
        str = str.replaceAll("'$", "");
        formatMsg(str, null);

        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

    public String formatMsg(String string, CommandSender sender) {
        Pattern pat = Pattern.compile("\\{msg_(.*?)}");
        Matcher m = pat.matcher(string);
        while (m.find()) {
            String s = m.group(1);
            string = string.replace("{msg_" + s + "}", getString(s, sender));
        }
        if (sender instanceof Player && HeadsPlus.get().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            string = PlaceholderAPI.setPlaceholders((Player) sender, string);
        }
        string = string.replaceAll("\\{header}", config.getString("prefix"));
        string = string.replaceAll("''", "'");
        string = string.replaceAll("^'", "");
        string = string.replaceAll("'$", "");
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String favourite(String string, Player player, String id) {
        Pattern pat = Pattern.compile("\\{msg_inventory\\.icon\\.head\\.favourite}");
        Matcher m = pat.matcher(string);
        while (m.find()) {
            if (HPPlayer.getHPPlayer(player).hasHeadFavourited(id)) {
                string = string.replace("{msg_inventory.icon.head.favourite}", getString("inventory.icon.head.favourite", (CommandSender) player));
            } else {
                string = string.replace("{msg_inventory.icon.head.favourite}", "");
            }
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String completed(String string, Player player, Challenge challenge) {
        Pattern pat = Pattern.compile("\\{completed}");
        Matcher m = pat.matcher(string);
        while (m.find()) {
            if (challenge.isComplete(player)) {
                string = string.replace("{completed}", getString("command.challenges.challenge-completed", (CommandSender) player));
            } else {
                string = string.replace("{completed}", "");
            }
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String getString(String path, CommandSender cs) {
        return cs instanceof Player ? getString(path, (Player) cs) : getString(path);
    }

    public String getString(String path, Player player) {
        if (player == null) return getString(path);
        YamlConfiguration config = HeadsPlusMessagesManager.config;
        if (MainConfig.get().getLocalisation().SMART_LOCALE) {
            if (players.containsKey(player.getUniqueId()) && player.isOnline()) {
                config = players.get(player.getUniqueId());
                if (config == null) {
                    setPlayerLocale(player.getPlayer());
                    config = players.get(player.getUniqueId());
                }
            }
        }
        String str = config.getString(path);
        if (str == null) return "";
        return formatMsg(str, player.getPlayer());
    }

    public void setPlayerLocale(Player player) {
        String locale = getLocale(player);
        String first = locale.split("_")[0].toLowerCase();
        if (locales.containsKey(first)) {
            players.put(player.getUniqueId(), locales.get(first));
        }
        HPPlayer.getHPPlayer(player).setLocale(locale, false);
    }

    public void setPlayerLocale(Player player, String locale, boolean b) {
        players.put(player.getUniqueId(), locales.get(locale));
        if (b) {
            HPPlayer.getHPPlayer(player).setLocale(locale);
        }
    }

    public void setPlayerLocale(Player player, String locale) {
        setPlayerLocale(player, locale, true);
    }
    public String getSetLocale(Player player) {
        return players.get(player.getUniqueId()).getName().split("_")[0];
    }

    private static String getLocale(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return String.valueOf(entityPlayer.getClass().getField("locale").get(entityPlayer));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            HeadsPlus.get().getServer().getLogger().info("Whoops, have an error to report...");
            DebugFileCreator.createReport(new HeadsPlusException(e));
            e.printStackTrace();
        }
        return "en_us";
    }

    public static HashMap<String, YamlConfiguration> getLocales() {
        return locales;
    }

    private YamlConfiguration performChecks(File file, String name) {
        if (file == null) {
            file = new File(HeadsPlus.get().getDataFolder() + File.separator + "locale" + File.separator, name + ".yml");
        }
        YamlConfiguration config = new YamlConfiguration();
        try {

            config.load(file);
        } catch (InvalidConfigurationException ex) {
            Logger logger = HeadsPlus.get().getLogger();
            logger.severe("There is a configuration error in the plugin configuration files! Details below:");
            logger.severe(ex.getMessage());
            logger.severe("We have renamed the faulty configuration to " + name + "-errored.yml for you to inspect.");
            file.renameTo(new File(HeadsPlus.get().getDataFolder() + File.separator + "locale" + File.separator, name + "-errored.yml"));
            logger.severe("When you believe you have fixed the problems, change the file name back to " + name + ".yml and reload the configuration.");
            logger.severe("If you are unsure, please contact the developer (Thatsmusic99).");
            logger.severe("The default configuration will be loaded in response to this.");
            InputStream is = HeadsPlus.get().getResource(name + ".yml");
            try {
                file.delete();
                Files.copy(is, new File(HeadsPlus.get().getDataFolder() + File.separator + "locale" + File.separator,name + ".yml").toPath());
                file = new File(HeadsPlus.get().getDataFolder() + File.separator + "locale" + File.separator,name + ".yml");
                config.load(file);
            } catch (FileNotFoundException ignored) {

            } catch (IOException | InvalidConfigurationException e) { // This time, it's me being a dumbass!
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public void sendMessage(String str, CommandSender sender, String... replace) {
        sendMessage(str, sender, true, replace);
    }

    public void sendMessage(String str, CommandSender sender, boolean translate, String... replace) {
        if (translate) {
            str = getString(str);
        }
        for (int i = 0; i < replace.length; i += 2) {
            str = str.replace(replace[i], replace[i + 1]);
        }
        if (sender instanceof Player && MainConfig.get().getLocalisation().USE_TELLRAW) {
            try {
                new JSONParser().parse(str);
            } catch (ParseException e) {
                str = "{\"text\":\"" + str + "\"}";
            }
            final String result = str;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + result);
                }
            }.runTask(HeadsPlus.get());
        } else {
            sender.sendMessage(str);
        }
    }

    public static HeadsPlusMessagesManager get() {
        return instance;
    }
}
