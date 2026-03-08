package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.gui.KarTransformStarGui;

public class KarTransformGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if(inv == null) return;

        if(e.getClickedInventory() == null) return;

        if(!(e.getClickedInventory().getHolder() instanceof KarTransformStarGui.KarTransformStarGuiInvHolder)) return;

        if(!(e.getSlot() == KarTransformStarGui.originSlot || e.getSlot() == KarTransformStarGui.afterSlot)){
            e.setCancelled(true);
        }

        if(KarTransformStarGui.tItems.get("ConfirmButton").getSlots().contains(e.getSlot())){
            ItemStack item1 = inv.getItem(KarTransformStarGui.originSlot);
            ItemStack item2 = inv.getItem(KarTransformStarGui.afterSlot);
            KarTransformStarGui.KarTransformMethod(item1,item2,(Player) e.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(inv.getHolder() instanceof KarTransformStarGui.KarTransformStarGuiInvHolder){
            ItemStack item1 = e.getInventory().getItem(KarTransformStarGui.originSlot);
            ItemStack item2 = e.getInventory().getItem(KarTransformStarGui.afterSlot);

            Player p = (Player) e.getPlayer();
            if (item1 != null) {
                p.getInventory().addItem(item1);
            }
            if(item2 != null) {
                p.getInventory().addItem(item2);
            }
        }
    }
}
