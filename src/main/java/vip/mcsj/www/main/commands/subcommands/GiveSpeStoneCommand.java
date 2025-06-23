package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.main.commands.KarCmdManager;
import vip.mcsj.www.main.commands.KarCommand;

public class GiveSpeStoneCommand implements KarCommand {
    private final KarCmdManager manager;

    public GiveSpeStoneCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.giveSpeStone(p,args);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement givespestone <player-name> <stone-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.givespestone";
    }
}
