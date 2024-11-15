package vip.mcsj.www.main.events;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import vip.mcsj.www.datamanager.EquipmentDataManager;

public class PreventOffHand implements Listener {


    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e){
        ItemStack offHandItem = e.getOffHandItem();
        if(offHandItem == null || offHandItem.getType() == Material.AIR){
            return;
        }
        EquipmentDataManager edm = new EquipmentDataManager(offHandItem);
        if(edm.carifyEquipmentLevel() > 0){
            e.getPlayer().sendMessage("§c你不能将淬炼装备放在副手!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMoveItemToOffHand(InventoryClickEvent e){
        if(e.getInventory().getType() != InventoryType.CRAFTING){
            return;
        }
        if(e.getSlot() != 40){
            return;
        }
        ItemStack newOffHandItem = e.getCursor();
        if(newOffHandItem == null || newOffHandItem.getType() == Material.AIR){
            return;
        }
        EquipmentDataManager edm = new EquipmentDataManager(newOffHandItem);
        if(edm.carifyEquipmentLevel() > 0){
            e.getWhoClicked().sendMessage("§c你不能将淬炼装备放在副手!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerNumToOffHand(InventoryClickEvent e){
        if(e.getInventory().getType() != InventoryType.CRAFTING){
            return;
        }
        if(!(e.getClick() == ClickType.NUMBER_KEY)){
            return;
        }
        if(e.getSlot() != 40){
            return;
        }
        e.getWhoClicked().sendMessage("§c为防止淬炼物品放在副手，你不能进行这个操作!");
        e.setCancelled(true);
    }
}
