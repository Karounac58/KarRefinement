package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.main.commands.KarCommand;
import vip.mcsj.www.main.commands.KarCmdManager;

//具体命令
public class AdminUpCommand implements KarCommand {
    private final KarCmdManager manager;

    public AdminUpCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.adminUp(p);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement adminup";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.adminup";
    }
}
