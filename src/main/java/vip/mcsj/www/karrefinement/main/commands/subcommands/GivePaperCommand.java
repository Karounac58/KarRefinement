package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GivePaperCommand extends KarAbstractGiveCommand {

    public GivePaperCommand() {
        super("givepaper", "§e/krf givepaper <玩家名> <保护符名> —— §b获取保护符");
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
        p.getInventory().addItem(KarRefinementAPI.createPaper(args[1]));
        return true;
    }
}
