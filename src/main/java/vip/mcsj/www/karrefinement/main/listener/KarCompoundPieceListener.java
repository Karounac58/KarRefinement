package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.gui.KarCompoundPieceGui;
import vip.mcsj.www.karrefinement.gui.KarTransformStarGui;

public class KarCompoundPieceListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        if(e.getInventory() == null || e.getClickedInventory() == null) return;

        if(!(e.getClickedInventory().getHolder() instanceof KarCompoundPieceGui.KarCompoundPieceGuiInvHolder)) return;

        if(!(e.getSlot() == 10 || e.getSlot() == 16)){
            e.setCancelled(true);
        }

        ItemStack item = null;
        Player p = (Player) e.getWhoClicked();
        if(e.getSlot() == 22){
            ItemStack itemPiece = inv.getItem(10);
            item = KarCompoundPieceGui.karCompoundPieceMethod(itemPiece,p);
            if(item == null){
                p.sendMessage("§c§l合成失败，请检查是否为碎片或碎片数量！");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            }else{
                inv.setItem(16, item);
                p.sendMessage("§a§l合成成功，你合成了"+item.getItemMeta().getDisplayName());
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        }


    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(inv.getHolder() instanceof KarCompoundPieceGui.KarCompoundPieceGuiInvHolder){
            ItemStack item1 = e.getInventory().getItem(10);
            ItemStack item2 = e.getInventory().getItem(16);

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
