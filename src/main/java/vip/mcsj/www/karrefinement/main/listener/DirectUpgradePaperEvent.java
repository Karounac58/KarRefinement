package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.datamanager.DUPaperDataManager;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.utils.KarUtils;

public class DirectUpgradePaperEvent implements Listener {

    @EventHandler
    public void onPlayerInteractDUPaper(InventoryClickEvent e){
        ItemStack itemDUPaper;
        ItemStack itemEquipment;
        if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
            Player p1 = (Player) e.getWhoClicked();
            if(KarEventListener.judgeInvRefinementOrNot.get(p1) == 1){
                return;
            }
            itemDUPaper = e.getCursor();
            itemEquipment = e.getCurrentItem();
            Service service = KarRefinementAPI.getService(DUPaperDataManager.class);
            if(service.isLegal(itemDUPaper)){
                if(EquipmentDataManager.isEquipmentLegal(itemEquipment)){
                    Player p = (Player) e.getWhoClicked();
                    //待实现：无限耐久精魂破碎时提示玩家。
                    if(service.up(itemDUPaper,itemEquipment)){
                        p.sendMessage(Message.messages.get("dupaper_up"));
                        p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0),1,1);
                        KarUtils.removeItemRefinement(itemDUPaper);
                    }
                }
            }
        }
    }
}
