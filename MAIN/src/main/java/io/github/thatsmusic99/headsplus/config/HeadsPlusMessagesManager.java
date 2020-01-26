package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadsPlusMessagesManager {

    private static YamlConfiguration config;
    private static HashMap<String, YamlConfiguration> locales;
    private static HashMap<Player, YamlConfiguration> players;

    public HeadsPlusMessagesManager() {
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadsPlusMainConfig mainConfig = hp.getConfiguration();
        String locale = mainConfig.getConfig().getString("locale");
        if (mainConfig.getConfig().getBoolean("smart-locale")) {
            locales = new HashMap<>();
            File langDir = new File(hp.getDataFolder() + File.separator + "locale" + File.separator);
            for (File f : langDir.listFiles()) {
                locales.put(f.getName().split("_")[0].toLowerCase(), YamlConfiguration.loadConfiguration(f));
            }
            players = new HashMap<>();
        } else {
            locales = new HashMap<>();
        }
        // Main config for non-player entities such as console
        try {
            config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, locale + ".yml"));
        } catch (Exception e) {
            hp.getLogger().info("Failed to load the locale settings! This is caused by an invalid name provided. Setting locale to en_us...");
            config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
        }
        if (config.getDouble("version") != 1.1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    hp.getLogger().info("Locale configs are outdated! Updating messages...");
                    YamlConfiguration en_us = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
                    YamlConfiguration de_de = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "de_de.yml"));
                    YamlConfiguration es_es = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "es_es.yml"));
                    YamlConfiguration fr_fr = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "fr_fr.yml"));
                    YamlConfiguration hu_hu = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "hu_hu.yml"));
                    YamlConfiguration lol_us = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "lol_us.yml"));
                    YamlConfiguration pl_pl = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "pl_pl.yml"));
                    YamlConfiguration ro_ro = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ro_ro.yml"));
                    YamlConfiguration ru_ru = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ru_ru.yml"));

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
                        en_us.set("version", 1.1);
                        en_us.options().copyDefaults(true);
                        try {
                            en_us.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
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
                        en_us.addDefault("inventory.icon.next", "&8❰ &a&lNächste &8❱");
                        en_us.addDefault("inventory.icon.next-2", "&8❰ &a&lNächste (2) &8❱");
                        en_us.addDefault("inventory.icon.next-3", "&8❰ &a&lNächste (3) &8❱");
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
                        de_de.set("version", 1.1);
                        de_de.options().copyDefaults(true);
                        try {
                            de_de.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "de_de.yml"));
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
                        es_es.set("version", 1.1);
                        es_es.options().copyDefaults(true);
                        try {
                            es_es.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "es_es.yml"));
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
                        fr_fr.set("version", 1.1);
                        fr_fr.options().copyDefaults(true);
                        try {
                            fr_fr.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "fr_fr.yml"));
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
                        hu_hu.set("version", 1.1);
                        hu_hu.options().copyDefaults(true);
                        try {
                            hu_hu.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "hu_hu.yml"));
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
                        lol_us.set("version", 1.1);
                        lol_us.options().copyDefaults(true);
                        try {
                            lol_us.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "lol_us.yml"));
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
                        pl_pl.set("version", 1.1);
                        pl_pl.options().copyDefaults(true);
                        try {
                            pl_pl.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "pl_pl.yml"));
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
                        ro_ro.set("version", 1.1);
                        ro_ro.options().copyDefaults(true);
                        try {
                            ro_ro.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ro_ro.yml"));
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
                        ru_ru.set("version", 1.1);
                        ru_ru.options().copyDefaults(true);
                        try {
                            ru_ru.save(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ru_ru.yml"));
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
        return string;
    }

    public String getString(String path, CommandSender cs) {
        return cs instanceof Player ? getString(path, (Player) cs) : getString(path);
    }

    public String getString(String path, Player player) {
        if (player == null) return getString(path);
        YamlConfiguration config = HeadsPlusMessagesManager.config;
        if (HeadsPlus.getInstance().getConfiguration().getConfig().getBoolean("smart-locale")) {
            if (players.containsKey(player)) {
                config = players.get(player);
                if (config == null) {
                    setPlayerLocale(player);
                    config = players.get(player);
                }
            }
        }
        String str = config.getString(path);
        if (str == null) return "";
        str = str.replaceAll("\\{header}", config.getString("prefix"));
        str = str.replaceAll("''", "'");
        str = str.replaceAll("^'", "");
        str = str.replaceAll("'$", "");
        formatMsg(str, player);
        if (HeadsPlus.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

    public void setPlayerLocale(Player player) {
        String locale = getLocale(player);
        String first = locale.split("_")[0].toLowerCase();
        if (locales.containsKey(first)) {
            players.put(player, locales.get(first));
        }
        HPPlayer.getHPPlayer(player).setLocale(locale, false);
    }

    public void setPlayerLocale(Player player, String locale, boolean b) {
        players.put(player, locales.get(locale));
        if (b) {
            HPPlayer.getHPPlayer(player).setLocale(locale);
        }
    }

    public void setPlayerLocale(Player player, String locale) {
        setPlayerLocale(player, locale, true);
    }
    public String getSetLocale(Player player) {
        return players.get(player).getName().split("_")[0];
    }

    private static String getLocale(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return String.valueOf(entityPlayer.getClass().getField("locale").get(entityPlayer));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            HeadsPlus.getInstance().getServer().getLogger().info("Whoops, have an error to report...");
            try {
                new DebugFileCreator().createReport(e, "Setting Smart Locale (Retrieving)");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return "en_us";
    }

    public static HashMap<String, YamlConfiguration> getLocales() {
        return locales;
    }
}
