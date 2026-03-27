package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.FurnaceDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GiveFurnaceCommand extends KarAbstractGiveCommand {
    public GiveFurnaceCommand() {
        super("givefurnace","§e/krf givefurnace <玩家名> <熔炉名> —— §b获取淬炼熔炉");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return false;
        }

        if(!FurnaceDataManager.furnaceEnabled){
            sender.sendMessage( "§c§l淬炼熔炉功能未启用！");
            return true;
        }

        if(args.length < 2){
            sender.sendMessage(usage);
        }

        Player p = getTargetPlayer(args[0]);
        if(p == null){
            sender.sendMessage( "§c§l找不到这个玩家！");
            return true;
        }
        p.getInventory().addItem(KarRefinementAPI.getService(FurnaceDataManager.class).create(args[1]));
        return true;
    }
}
