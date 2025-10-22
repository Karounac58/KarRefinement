package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

import java.util.List;
import java.util.UUID;

public class DarkChangeCommand extends KarAbstractCommand {


    public DarkChangeCommand() {
        super("darkchange", "karrefinement.darkchange", "§e/krf darkchange —— §b暗改玩家淬炼", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }

        if(args.length < 2){
            sender.sendMessage(usage);
            return true;
        }
        OfflinePlayer offp = Bukkit.getOfflinePlayer(args[1]);
        switch(args[0]){
            case "set":
                if(args.length < 4){
                    sender.sendMessage(usage);
                    return true;
                }
                boolean isSuccess = Boolean.valueOf(args[2]);
                int count = Integer.parseInt(args[3]);
                KarRefinement.dcdm.changePlayerDarkChangeInfo(offp, isSuccess, count);
                sender.sendMessage(Message.messages.get("darkchange_success"));
                break;
            case "seen":
                List<Object> playerDarkChangeData = KarRefinement.dcdm.getPlayerDarkChangeData(offp);
                if(playerDarkChangeData == null){
                    sender.sendMessage(Message.messages.get("darkchange_not_found"));
                    return true;
                }
                sender.sendMessage(Message.messages.get("darkchange_msg1").replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(playerDarkChangeData.get(0)))).getName()));
                sender.sendMessage(Message.messages.get("darkchange_msg2").replace("{success}", String.valueOf(playerDarkChangeData.get(1))));
                sender.sendMessage(Message.messages.get("darkchange_msg3").replace("{count}", String.valueOf(playerDarkChangeData.get(2))));
                break;
            case "clear":
                int i = KarRefinement.dcdm.deletePlayerDarkChangeData(offp);
                if(i == 0){
                    sender.sendMessage(Message.messages.get("darkchange_not_found"));
                }else{
                    sender.sendMessage(Message.messages.get("darkchange_clear"));
                }
                break;
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
