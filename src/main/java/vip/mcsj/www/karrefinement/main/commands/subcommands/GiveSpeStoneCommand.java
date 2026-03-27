package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.SpecialStoneDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GiveSpeStoneCommand extends KarAbstractGiveCommand {

    public GiveSpeStoneCommand() {
        super("givespestone", "§e/krf givespestone <玩家名> <宝石名> —— §b获取宝石");
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
        Player p = getTargetPlayer(args[0]);
        if(p == null){
            sender.sendMessage( "§c§l找不到这个玩家！");
            return true;
        }

        p.getInventory().addItem(KarRefinementAPI.createSpeStone(args[1]));

        return true;
    }
}
