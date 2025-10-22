package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class SetCommand extends KarAbstractCommand {

    public SetCommand() {
        super("set","karrefinement.set","§e/krf set <等级> —— §b为手上物品设置淬炼等级",true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        if(isPlayerOnly()){
            Player p = requirePlayer(sender);
            if(p != null){
                if(args.length == 0){
                    p.sendMessage(usage);
                    return true;
                }
                int i = parseInt(sender,args[0],"§c§l参数必须为数字！");
                if(EquipmentDataManager.isEquipmentLegal(p.getInventory().getItemInMainHand())){
                    EquipmentDataManager edm = new EquipmentDataManager(p.getInventory().getItemInMainHand(),p);
                    edm.setRefinementLevel(i);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    return true;
                }
            }
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
