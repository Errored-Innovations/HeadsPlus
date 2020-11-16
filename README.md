
![Loading beautiful image ;)](https://proxy.spigotmc.org/7f8059160ab428b12bd0b5c6ca741095b43342b1?url=https%3A%2F%2Fi.imgur.com%2F4DjCHmg.png)

# HeadsPlus Project

[![](https://img.shields.io/github/v/release/Thatsmusic99/HeadsPlus.svg?label=github%20release)](https://github.com/Thatsmusic99/HeadsPlus/releases)

HeadsPlus (referred to as the HeadsPlus Project on Github) is one of Spigot's most ambitious and advanced heads plugins, providing several new and unique features to servers of which have never been seen before. Minecraft's skulls are an important asset to bringing forth incredible amounts of creativity without further intervention by resource packs, mods and data packs, so HeadsPlus makes sure they are used to the best of their ability.

Currently, HeadsPlus has 21,000+ downloads, over 50 reviews (55/60) praising the plugin, 600+ live servers with 600-1200 players, and a committed developer always looking to find ways to improve the plugin even more and make it stand out even more among other plugins.

## Installation/Cloning
HeadsPlus uses Maven to manage its dependencies, with most of these provided in a library folder within the project.

The command which is used by the developer herself is `mvn clean install` and should work fine on itself.

HeadsPlus uses the CraftBukkit API, which requires you to have run BuildTools on your system for each version.

## Using HeadsPlus in Maven
HeadsPlus uses JitPack to host its repository. To use HeadsPlus as a library in your project, add the following in your project:
```xml
    <repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.Thatsmusic99</groupId>
	    <artifactId>HeadsPlus</artifactId>
	    <version>v6.10.5-SNAPSHOT-1</version>
	</dependency>
    </dependencies>
```


## More information
- [Wiki](https://github.com/Thatsmusic99/HeadsPlus/wiki)
- [Spigot Page](https://www.spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/)
- [Issue Tracker](https://github.com/Thatsmusic99/HeadsPlus/issues)
- [Discord](https://discord.gg/eu8h3BG)

## Important documents
- [Code of Conduct](https://github.com/Thatsmusic99/HeadsPlus/blob/master/CODE_OF_CONDUCT.md)
- [License (GPLv3)](https://github.com/Thatsmusic99/HeadsPlus/blob/master/LICENSE)
