package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.AdhesiveDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GiveAdhesiveCommand extends KarAbstractGiveCommand {
    public GiveAdhesiveCommand() {
        super("giveadhesive", "§e/krf giveadhesive <玩家名> <粘合剂名> —— §b获取宝石粘合剂");
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

//        p.getInventory().addItem(AdhesiveDataManager.create(args[1]));
        p.getInventory().addItem(KarRefinementAPI.createAdhesive(args[1]));
        return true;
    }
}
