package com.mattdahepic.mdecore.command;

import com.mattdahepic.mdecore.command.logic.*;

public class CommandMDE extends AbstractCommand {
    public static CommandMDE instance = new CommandMDE();

    public CommandMDE () {
        registerCommandLogic(PosLogic.instance);
        registerCommandLogic(TPSLogic.instance);
        registerCommandLogic(TPXLogic.instance);
        registerCommandLogic(MDEHelpLogic.instance);
        registerCommandLogic(VersionLogic.instance);
        registerCommandLogic(KillAllLogic.instance);
        registerCommandLogic(PregenLogic.instance);
        registerCommandLogic(TickrateLogic.instance);
        registerCommandLogic(InvseeLogic.instance);
        registerCommandLogic(EnderchestLogic.instance);
        //registerCommandLogic(TrimLogic.instance);
        registerCommandLogic(TPALogic.instance);
    }

    @Override
    public String getCommandName () {
        return "mde";
    }
}
