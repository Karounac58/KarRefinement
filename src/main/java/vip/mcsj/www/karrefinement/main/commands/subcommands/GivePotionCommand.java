package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.PotionDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GivePotionCommand extends KarAbstractGiveCommand {
    public GivePotionCommand() {
        super("givepotion", "§e/krf givepotion <玩家名> <淬炼药水名> —— §b获取淬炼药水");
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

        p.getInventory().addItem(KarRefinementAPI.createPotion(args[1]));

        return true;
    }
}
