package vip.mcsj.www.karrefinement.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.object.Level;

public class KarRefinementAPI {
    public static void setItemLevel(ItemStack item,int level){
        if(EquipmentDataManager.isEquipmentLegal(item)){
            new EquipmentDataManager(item).setRefinementLevel(level);
        }
    }

    public static boolean canRefinement(ItemStack item){
        return EquipmentDataManager.isEquipmentLegal(item);
    }

    public static boolean addProtectPaper(ItemStack itemPaper,ItemStack item){
        return PaperDataManager.protectorPaperUp(itemPaper,item);
    }

    public static Level getMinLevel(Player p){
        return LevelDataManager.getMinLevel(p);
    }
}
