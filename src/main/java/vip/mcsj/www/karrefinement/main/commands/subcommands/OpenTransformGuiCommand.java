package vip.mcsj.www.karrefinement.main.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import vip.mcsj.www.karrefinement.gui.KarTransformStarGui;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class OpenTransformGuiCommand extends KarAbstractCommand {

    public OpenTransformGuiCommand() {
        super("opentransformgui", "karrefinement.opentransformgui", "§e/krf opentransformgui <玩家名> —— §b打开淬炼移星界面", false);
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

        Inventory inv3 = Bukkit.createInventory(new KarTransformStarGui.KarTransformStarGuiInvHolder(), KarTransformStarGui.size, PlaceholderAPI.setPlaceholders(p,KarTransformStarGui.title));
        KarTransformStarGui.initInv(inv3,p);
        p.openInventory(inv3);
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
