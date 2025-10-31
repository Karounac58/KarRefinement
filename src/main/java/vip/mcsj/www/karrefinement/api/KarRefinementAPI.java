package vip.mcsj.www.karrefinement.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.object.DirectUpgradePaper;
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

    public static ItemStack createStone(String stoneName){
        StoneDataManager sdm = new StoneDataManager(stoneName);
        return sdm.createStone();
    }

    public static ItemStack createPaper(String paperName){
        PaperDataManager pdm = new PaperDataManager(paperName);
        return pdm.createProtectedPaper();
    }

    public static ItemStack createSpeStone(String speStoneName){
        SpecialStoneDataManager ssdm = new SpecialStoneDataManager(speStoneName);
        return ssdm.createSpeStone();
    }

    public static ItemStack createSoul(String soulName){
        InfiniteSoulManager ism = new InfiniteSoulManager(soulName);
        return ism.createInfiniteSoul();
    }

    public static ItemStack createDUPaper(String dupaperName){
        return DUPaperDataManager.createDUPaper(dupaperName);
    }

    public static ItemStack createDetachItem(){
        return DetachDataManager.createPaperDetachItem();
    }

    public static ItemStack createDetachItemPiece(String pieceName){
        DetachDataManager ddm = new DetachDataManager(pieceName);
        return ddm.createPaperPiece();
    }

    public static ItemStack createPotion(String potionName){
        PotionDataManager pdm = new PotionDataManager(potionName);
        return pdm.createPotion();
    }

    public static ItemStack createAdhesive(String adhesiveName){
        return AdhesiveDataManager.createAdhesiveItem(adhesiveName);
    }
}
