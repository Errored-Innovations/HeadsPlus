package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class XPCommand implements IHeadsPlusCommand {
    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        return null;
    }

    @Override
    public String getCmdDescription() {
        return null;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
