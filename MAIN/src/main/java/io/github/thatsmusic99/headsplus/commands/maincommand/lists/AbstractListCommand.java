package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;

import java.util.List;

public abstract class AbstractListCommand implements IHeadsPlusCommand {

    protected final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    protected final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    public abstract List<String> getList();
    public abstract String getPath();
    public abstract String getListType();
    public abstract String getType();
}
