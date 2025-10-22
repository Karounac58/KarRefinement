package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.gui.KarTakeItemGui;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class OpenItemGuiCommand extends KarAbstractCommand {

    public OpenItemGuiCommand() {
        super("openitemgui","karrefinement.openitemgui","§e/krf openitemgui —— §b打开淬炼物品菜单",true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        if(isPlayerOnly()){
            Player p = requirePlayer(sender);
            if(p != null){
                KarTakeItemGui.openKarTakeItemGui(p);
                p.sendMessage("§a你打开了淬炼物品菜单");
                return true;
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isPlayerOnly() {
        return playerOnly;
    }
}
