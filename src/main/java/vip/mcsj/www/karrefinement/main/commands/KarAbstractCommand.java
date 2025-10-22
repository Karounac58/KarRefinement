package vip.mcsj.www.karrefinement.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class KarAbstractCommand implements KarCommand{
    protected final String name;
    protected final String permission;

    protected final String usage;

    protected final boolean playerOnly;

    public KarAbstractCommand(String name, String permission, String usage, boolean playerOnly) {
        this.name = name;
        this.permission = permission;
        this.usage = usage;
        this.playerOnly = playerOnly;
    }

    protected Player requirePlayer(CommandSender sender){
        if(!(sender instanceof Player)){
            sender.sendMessage("§c§l只有玩家才能使用此命令.");
            return null;
        }
        return (Player)sender;
    }

    protected int parseInt(CommandSender sender,String arg,String errMsg){
        try{
            return Integer.parseInt(arg);
        }catch(NumberFormatException e){
            sender.sendMessage(errMsg);
            return 0;
        }
    }

    protected boolean checkPermission(CommandSender sender){
        if(permission != null && !sender.hasPermission(permission)){
            sender.sendMessage("§c§l你没有权限执行此命令");
            return false;
        }
        return true;
    }
}
