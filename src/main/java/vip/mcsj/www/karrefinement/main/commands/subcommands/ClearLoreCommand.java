package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;
import vip.mcsj.www.karrefinement.main.commands.KarCmdManager;

public class ClearLoreCommand implements KarCommand {
    private final KarCmdManager manager;

    public ClearLoreCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.clearLore(p);
        return true;
    }

    @Override
    public String getUsage() {
        return "/karrefinement clearlore";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.clearlore";
    }
}
