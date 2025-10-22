package vip.mcsj.www.karrefinement.main.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.datamanager.DetachDataManager;
import vip.mcsj.www.karrefinement.gui.KarCompoundPieceGui;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class OpenCompoundPieceGuiCommand extends KarAbstractCommand {
    public OpenCompoundPieceGuiCommand() {
        super("opencompoundpiecegui", "krerefinement.opencompoundpiecegui", "§e/krf opencompoundpiecegui <玩家名> —— §b打开保护符碎片合成界面", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }

        if(args.length == 0){
            sender.sendMessage(usage);
            return true;
        }

        Player p = Bukkit.getPlayer(args[0]);
        if(p == null){
            sender.sendMessage( "§c§l找不到这个玩家！");
            return true;
        }
        if (!DetachDataManager.enabled) {
            p.sendMessage("§c§l请前往detach.yml配置文件中开启保护符拆卸功能");
            return true;
        }
        Inventory inv4 = Bukkit.createInventory(new KarCompoundPieceGui.KarCompoundPieceGuiInvHolder(),KarCompoundPieceGui.size, PlaceholderAPI.setPlaceholders(p,KarCompoundPieceGui.title));
        KarCompoundPieceGui.initInv(inv4,p);
        p.openInventory(inv4);

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
