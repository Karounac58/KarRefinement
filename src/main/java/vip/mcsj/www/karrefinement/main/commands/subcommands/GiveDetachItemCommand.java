package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.DetachDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;


public class GiveDetachItemCommand extends KarAbstractGiveCommand {
    public GiveDetachItemCommand() {
        super("givedetachitem", "§e/krf givedetachitem <玩家名> 保护符拆卸工具 —— §b获取保护符拆卸工具");
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
        if (!DetachDataManager.enabled) {
            p.sendMessage("§c§l请前往detach.yml配置文件中开启保护符拆卸功能");
            return true;
        }
        if(args[1].equalsIgnoreCase("保护符拆卸工具")){
            p.getInventory().addItem(DetachDataManager.createPaperDetachItem());
        }else if(args[1].equalsIgnoreCase("宝石拆卸工具")){
            p.getInventory().addItem(DetachDataManager.createSpeStoneDetachItem());
        }

        return true;
    }
}
