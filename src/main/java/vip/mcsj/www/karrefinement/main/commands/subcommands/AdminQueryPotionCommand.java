package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.datamanager.PotionDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

import java.util.List;

public class AdminQueryPotionCommand extends KarAbstractCommand {
    public AdminQueryPotionCommand() {
        super("adminquerypotion", "karrefinement.adminquerypotion", "§e/krf adminquerypotion <玩家名> —— §b查看玩家的淬炼药水加成", false);
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

        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
        if(p != null){
            PotionDataManager pdm = new PotionDataManager();
            List<Object> rs = pdm.queryPlayerPotionInfo(p);
            if(rs == null){
                sender.sendMessage(Message.messages.get("adminquerypotion_failed"));
                return true;
            }
            sender.sendMessage(Message.messages.get("adminquerypotion_msg1"));
            sender.sendMessage(Message.messages.get("adminquerypotion_msg2").replace("{player}", p.getName()));
            sender.sendMessage(Message.messages.get("adminquerypotion_msg3").replace("{success}",(double)rs.get(1)*100 + "")); //
            sender.sendMessage(Message.messages.get("adminquerypotion_msg4").replace("{duration}",PotionDataManager.formatTimeRemaining((Long)rs.get(0)))); // PotionDataManager.formatTimeRemaining((Long)rs.get(0))
        }else{
            sender.sendMessage("§c§l此玩家不存在！");
        }
        return false;
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
