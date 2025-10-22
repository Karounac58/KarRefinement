package vip.mcsj.www.karrefinement.main.commands;

import vip.mcsj.www.karrefinement.main.commands.subcommands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String,KarAbstractCommand> commands = new HashMap<>();

    public CommandFactory() {
        registerCommands();
    }

    private void registerCommands(){
        register(new AdminQueryPotionCommand());
        register(new AdminUpCommand());
        register(new GiveAdhesiveCommand());
        register(new GiveDetachItemCommand());
        register(new GiveDUPaperCommand());
        register(new GivePaperCommand());
        register(new GivePotionCommand());
        register(new GiveSoulCommand());
        register(new GiveSpeStoneCommand());
        register(new GiveStoneCommand());
        register(new HelpCommand());
        register(new OpenCompoundGuiCommand());
        register(new OpenCompoundPieceGuiCommand());
        register(new OpenForgeGuiCommand());
        register(new OpenGuiCommand());
        register(new OpenItemGuiCommand());
        register(new OpenTransformGuiCommand());
        register(new QueryPotionCommand());
        register(new ReloadCommand());
        register(new SetCommand());
    }

    private void register(KarAbstractCommand command){
        commands.put(command.name.toLowerCase(),command);
    }

    public KarAbstractCommand getCommand(String name){
        return commands.get(name.toLowerCase());
    }

    public boolean hasCommand(String name){
        return commands.containsKey(name.toLowerCase());
    }
}
