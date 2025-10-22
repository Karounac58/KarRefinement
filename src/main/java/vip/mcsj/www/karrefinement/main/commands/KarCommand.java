package vip.mcsj.www.karrefinement.main.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//命令接口
public interface KarCommand {
    boolean execute(CommandSender sender, String[] args);
    String getUsage();
    String getPermission();
    boolean isPlayerOnly();
}
