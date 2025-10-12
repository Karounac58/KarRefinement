package vip.mcsj.www.karrefinement.api;

import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;

public class StoneAPI {
    public static ItemStack createStone(String stoneName){
        StoneDataManager sdm = new StoneDataManager(stoneName);
        return sdm.createStone();
    }
}
