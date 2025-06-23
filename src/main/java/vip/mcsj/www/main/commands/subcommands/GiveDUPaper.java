package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.datamanager.DUPaperDataManager;
import vip.mcsj.www.main.commands.KarCmdManager;
import vip.mcsj.www.main.commands.KarCommand;

public class GiveDUPaper implements KarCommand {
    private final KarCmdManager manager;

    public GiveDUPaper(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.giveDUPaper(p,args);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement givedupaper <player-name> <dupaper-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.givedupaper";
    }
}
