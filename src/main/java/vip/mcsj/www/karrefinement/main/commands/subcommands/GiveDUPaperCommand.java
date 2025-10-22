package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.DUPaperDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class GiveDUPaperCommand extends KarAbstractGiveCommand {

    public GiveDUPaperCommand() {
        super("givedupaper", "§e/krf givedupaper <玩家名> <直升符名> —— §b获取直升符");
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
        p.getInventory().addItem(DUPaperDataManager.createDUPaper(args[1]));
        return true;
    }
}
