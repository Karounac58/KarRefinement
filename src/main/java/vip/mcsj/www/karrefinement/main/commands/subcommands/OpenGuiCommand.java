package vip.mcsj.www.karrefinement.main.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.gui.KarRefinementInvHolder;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class OpenGuiCommand extends KarAbstractCommand {


    public OpenGuiCommand() {
        super("opengui", "karrefinement.opengui", "§e/krf opengui <玩家名> —— §b打开淬炼界面", false);
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
        Inventory inv = Bukkit.createInventory(new KarRefinementInvHolder(), 54, PlaceholderAPI.setPlaceholders(p, KarRefinementGui.title));
        KarRefinementGui.setInvInitial(inv,p);
        p.openInventory(inv);
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
