package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

//具体命令
public class AdminUpCommand extends KarAbstractCommand {
    public AdminUpCommand() {
        super("adminup","karrefinement.adminup","§e/krf adminup —— §b为手上物品升星",true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        if(isPlayerOnly()){
            Player p = requirePlayer(sender);
            if(p != null){
                ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                if (EquipmentDataManager.isEquipmentLegal(itemInMainHand)) {
                    EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand,p);
                    manager.injuryUpStar();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean isPlayerOnly() {
        return this.playerOnly;
    }
}
