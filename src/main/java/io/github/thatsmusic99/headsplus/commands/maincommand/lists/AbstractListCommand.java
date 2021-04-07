package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;

import java.util.List;

public abstract class AbstractListCommand implements IHeadsPlusCommand {

    protected final HeadsPlus hp;
    protected final MainConfig config;
    protected final HeadsPlusMessagesManager hpc;

    public AbstractListCommand(HeadsPlus hp) {
        this.hp = hp;
        this.config = hp.getConfiguration();
        this.hpc = hp.getMessagesConfig();
    }

    public abstract List<String> getList();
    public abstract String getPath();
    public abstract String getListType();
    public abstract String getType();
    public abstract String getFullName();
}
