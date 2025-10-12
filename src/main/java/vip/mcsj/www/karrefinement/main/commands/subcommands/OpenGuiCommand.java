package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarCmdManager;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class OpenGuiCommand implements KarCommand {
    private final KarCmdManager manager;

    public OpenGuiCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.openGui(p);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement opengui <player-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.opengui";
    }
}
