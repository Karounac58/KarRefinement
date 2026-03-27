package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GiveStoneCommand extends KarAbstractGiveCommand {
    public GiveStoneCommand() {
        super("givestone", "§e/krf givestone <玩家名> <淬炼石名> <数量> —— §b获取淬炼石");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        if(args.length < 3){
            sender.sendMessage(usage);
            return true;
        }

        Player p = getTargetPlayer(args[0]);
        if(p == null){
            sender.sendMessage( "§c§l找不到这个玩家！");
            return true;
        }

        ItemStack stone = KarRefinementAPI.getService(StoneDataManager.class).create(args[1]);
        if (stone == null) {
            p.sendMessage(ChatColor.RED + "没有这个淬炼石");
            return true;
        }
        int i = parseInt(sender,args[2],"§c§l参数必须为数字！");
        for (int j = 0; j < i; j++) {
            p.getInventory().addItem(stone);
        }
        return true;
    }
}
