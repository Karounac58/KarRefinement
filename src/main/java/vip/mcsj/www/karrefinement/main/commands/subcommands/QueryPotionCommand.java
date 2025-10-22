package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.datamanager.PotionDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

import java.util.List;

public class QueryPotionCommand extends KarAbstractCommand {
    public QueryPotionCommand() {
        super("querypotion", "karrefinement.querypotion", "§e/krf querypotion —— §b让玩家查看淬炼药水加成", true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }

        if(isPlayerOnly()){
            if(sender instanceof OfflinePlayer){
                OfflinePlayer p =  (OfflinePlayer)sender;
                PotionDataManager pdm = new PotionDataManager();
                List<Object> rs = pdm.queryPlayerPotionInfo(p);
                if (p.isOnline()) {
                    Player player1 = p.getPlayer();
                    if(rs == null){
                        player1.sendMessage(Message.messages.get("querypotion_failed"));
                        return true;
                    }
                    player1.sendMessage(Message.messages.get("querypotion_msg1"));
                    player1.sendMessage(Message.messages.get("querypotion_msg2").replace("{success}",(double)rs.get(1)*100+"")); // (double)rs.get(1)*100
                    player1.sendMessage(Message.messages.get("querypotion_msg3").replace("{duration}",PotionDataManager.formatTimeRemaining((Long)rs.get(0)))); // PotionDataManager.formatTimeRemaining((Long)rs.get(0))
                }
            }else{
                sender.sendMessage("§c§l只有玩家才能执行此命令！");
            }
        }
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
