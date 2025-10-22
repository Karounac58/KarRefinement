package vip.mcsj.www.karrefinement.main.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.gui.KarCompoundStoneGui;
import vip.mcsj.www.karrefinement.gui.KarCompoundStoneInvHolder;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class OpenCompoundGuiCommand extends KarAbstractCommand {

    public OpenCompoundGuiCommand() {
        super("opencompoundgui", "karrefinement.opencompoundgui", "§e/krf opencompoundgui <玩家名> —— §b打开宝石合石界面", false);
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

        Inventory inv2 = Bukkit.createInventory(new KarCompoundStoneInvHolder(),54, PlaceholderAPI.setPlaceholders(p, KarCompoundStoneGui.title));
        KarCompoundStoneGui.initial(inv2,p);
        KarCompoundStoneGui.openGuiForPlayer(inv2,p);
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
