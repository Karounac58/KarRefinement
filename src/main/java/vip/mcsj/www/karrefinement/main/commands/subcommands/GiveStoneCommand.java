package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;
import vip.mcsj.www.karrefinement.main.commands.KarCmdManager;

public class GiveStoneCommand implements KarCommand {
    private final KarCmdManager manager;

    public GiveStoneCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.giveStone(p,args);
        return false;
    }

    @Override
    public String getUsage() {
        return "/karrefinement givestone <player-name> <stone-name> <number>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.givestone";
    }
}
