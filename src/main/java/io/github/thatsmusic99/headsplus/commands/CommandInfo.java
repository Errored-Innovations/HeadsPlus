package io.github.thatsmusic99.headsplus.commands;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    String commandname();
    String permission();
    String subcommand();
    String usage();
    boolean maincommand();

}
