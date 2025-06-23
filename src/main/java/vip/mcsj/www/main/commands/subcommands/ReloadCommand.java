package vip.mcsj.www.main.commands.subcommands;

import org.bukkit.entity.Player;
import vip.mcsj.www.main.commands.KarCommand;
import vip.mcsj.www.main.commands.KarCmdManager;

public class ReloadCommand implements KarCommand {
    private final KarCmdManager manager;

    public ReloadCommand(KarCmdManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean execute(Player p, String[] args) {
        manager.reload();
        p.sendMessage("§a§l配置文件重载成功");
        return false;
    }

    @Override
    public String getUsage() {
        return "/karrefinement reload";
    }

    @Override
    public String getPermission() {
        return "karrefinement.use.reload";
    }
}
