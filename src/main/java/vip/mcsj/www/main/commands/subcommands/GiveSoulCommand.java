package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.datamanager.InfiniteSoulManager;
import vip.mcsj.www.main.commands.KarCmdManager;
import vip.mcsj.www.main.commands.KarCommand;

public class GiveSoulCommand implements KarCommand {
    private final KarCmdManager manager;

    public GiveSoulCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.giveSoul(p,args);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement givesoul <player-name> <soul-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.givesoul";
    }
}
