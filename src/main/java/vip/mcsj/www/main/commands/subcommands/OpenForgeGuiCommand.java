package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.main.commands.KarCmdManager;
import vip.mcsj.www.main.commands.KarCommand;

public class OpenForgeGuiCommand implements KarCommand {
    private final KarCmdManager manager;

    public OpenForgeGuiCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.openForgeGui(p);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement openforgegui <player-name>";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.openforgegui";
    }
}
