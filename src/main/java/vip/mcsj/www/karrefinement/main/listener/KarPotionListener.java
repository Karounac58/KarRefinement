package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.datamanager.PotionDataManager;

public class KarPotionListener implements Listener {
    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent e){
        if(PotionDataManager.isPotionLegal(e.getItem())){
            Player p = e.getPlayer();
            new PotionDataManager(e.getItem(), p).drinkPotion();
            p.sendMessage(Message.messages.get("potion_drink").replace("{potion}",e.getItem().getItemMeta().getDisplayName()));
        }
    }
}
