package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import vip.mcsj.www.karrefinement.datamanager.PotionDataManager;

public class KarPotionListener implements Listener {
    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent e){
        if(PotionDataManager.isPotionLegal(e.getItem())){
            Player p = e.getPlayer();
            new PotionDataManager(e.getItem(), p).drinkPotion();
            p.sendMessage("§a§l成功激活"+e.getItem().getItemMeta().getDisplayName()+"§a§l增幅效果");
        }
    }
}
