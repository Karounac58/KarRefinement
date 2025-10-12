package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarCmdManager;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class GivePaperCommand implements KarCommand {
    private final KarCmdManager manager;

    public GivePaperCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.givePaper(p,args);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement givepaper <player-name> <paper-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.givepaper";
    }
}
