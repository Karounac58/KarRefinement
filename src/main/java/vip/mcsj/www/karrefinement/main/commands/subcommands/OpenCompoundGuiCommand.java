package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarCmdManager;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class OpenCompoundGuiCommand implements KarCommand {
    private final KarCmdManager manager;

    public OpenCompoundGuiCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.openCompoundGui(p);
        return false;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "";
    }
}
