package vip.mcsj.www.karrefinement.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KarAbstractGiveCommand extends KarAbstractCommand{

    public KarAbstractGiveCommand(String name,String usage) {
        super(name, "karrefinement.give."+name, usage, false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
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
        return false;
    }

    protected Player getTargetPlayer(String playerName){
        return Bukkit.getPlayer(playerName);
    }
}
