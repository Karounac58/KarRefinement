package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.InfiniteSoulManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractGiveCommand;

public class GiveSoulCommand extends KarAbstractGiveCommand {

    public GiveSoulCommand() {
        super("givesoul", "§e/krf givesoul <玩家名> <精魂名> —— §b获取无限耐久精魂");
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

        InfiniteSoulManager soulManager = new InfiniteSoulManager(args[1]);
        p.getInventory().addItem(soulManager.createInfiniteSoul());
        return true;
    }
}
