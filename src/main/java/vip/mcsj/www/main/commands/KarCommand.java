package vip.mcsj.www.main.commands;

import org.bukkit.entity.Player;

//命令接口
public interface KarCommand {
    boolean execute(Player p,String[] args);
    String getUsage();
    String getPermission();

}
