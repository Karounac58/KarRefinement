package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.gui.KarCompoundPieceGui;
import vip.mcsj.www.karrefinement.gui.KarTransformStarGui;
import vip.mcsj.www.karrefinement.service.SoundDataManager;

public class KarCompoundPieceListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        if(e.getInventory() == null || e.getClickedInventory() == null) return;

        if(!(e.getClickedInventory().getHolder() instanceof KarCompoundPieceGui.KarCompoundPieceGuiInvHolder)) return;

        if(!(e.getSlot() == KarCompoundPieceGui.originSlot || e.getSlot() == KarCompoundPieceGui.afterSlot)){
            e.setCancelled(true);
        }

        ItemStack item = null;
        Player p = (Player) e.getWhoClicked();

        if(KarCompoundPieceGui.cpItems.get("ConfirmButton").getSlots().contains(e.getSlot())){
            ItemStack itemPiece = inv.getItem(KarCompoundPieceGui.originSlot);

            if(KarCompoundPieceGui.judgeInventoryClickMethod(itemPiece,p)){
                item = KarCompoundPieceGui.karCompoundPieceMethod(itemPiece,p);
                ItemStack item1 = inv.getItem(KarCompoundPieceGui.afterSlot);
                if (item1 != null && item1.isSimilar(item)) {
                    item.setAmount(item1.getAmount() + 1);
                }
                inv.setItem(KarCompoundPieceGui.afterSlot, item);
                p.sendMessage(Message.messages.get("compoundpiece_success").replace("{item}", item.getItemMeta().getDisplayName()));
                
                Sound successSound = SoundDataManager.getSound("KarCompoundPieceGui", "Success");
                if(successSound != null){
                    p.playSound(p.getLocation(), successSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        if(e.getInventory() == null || e.getClickedInventory() == null) return;

        if(!(e.getClickedInventory().getHolder() instanceof KarCompoundPieceGui.KarCompoundPieceGuiInvHolder)) return;

        if(!(e.getSlot() == KarCompoundPieceGui.originSlot || e.getSlot() == KarCompoundPieceGui.afterSlot)){
            e.setCancelled(true);
        }

        ItemStack item = null;
        Player p = (Player) e.getWhoClicked();

        if(KarCompoundPieceGui.cpItems.get("ConfirmButton").getSlots().contains(e.getSlot())){
            ItemStack itemPiece = inv.getItem(KarCompoundPieceGui.originSlot);

            if(!KarCompoundPieceGui.judgeInventoryClickMethod2(itemPiece,p) && !KarCompoundPieceGui.judgeInventoryClickMethod(itemPiece,p)){
                p.sendMessage(Message.messages.get("compoundpiece_failed"));
                
                Sound failSound = SoundDataManager.getSound("KarCompoundPieceGui", "Fail");
                if(failSound != null){
                    p.playSound(p.getLocation(), failSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                }
            }else if(KarCompoundPieceGui.judgeInventoryClickMethod2(itemPiece,p)){
                item = KarCompoundPieceGui.karCompoundPieceMethod2(itemPiece,p);
                ItemStack item1 = inv.getItem(KarCompoundPieceGui.afterSlot);
                if (item1 != null && item1.isSimilar(item)) {
                    item.setAmount(item1.getAmount() + 1);
                }
                inv.setItem(KarCompoundPieceGui.afterSlot, item);
                p.sendMessage(Message.messages.get("compoundpiece_success").replace("{item}", item.getItemMeta().getDisplayName()));
                
                Sound successSound = SoundDataManager.getSound("KarCompoundPieceGui", "Success");
                if(successSound != null){
                    p.playSound(p.getLocation(), successSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(inv.getHolder() instanceof KarCompoundPieceGui.KarCompoundPieceGuiInvHolder){
            ItemStack item1 = e.getInventory().getItem(KarCompoundPieceGui.originSlot);
            ItemStack item2 = e.getInventory().getItem(KarCompoundPieceGui.afterSlot);

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
