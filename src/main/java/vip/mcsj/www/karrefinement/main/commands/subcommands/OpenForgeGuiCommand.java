package vip.mcsj.www.karrefinement.main.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.gui.KarForgeGui;
import vip.mcsj.www.karrefinement.gui.KarForgeInvHolder;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class OpenForgeGuiCommand extends KarAbstractCommand {


    public OpenForgeGuiCommand() {
        super("openforgegui", "karrefinement.openforgegui", "§e/krf openforgegui <玩家名> —— §b打开锻造界面", false);
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
        Inventory inv1 = Bukkit.createInventory(new KarForgeInvHolder(),45, PlaceholderAPI.setPlaceholders(p, KarForgeGui.title));
        KarForgeGui.initInv(inv1,p);
        p.openInventory(inv1);
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
