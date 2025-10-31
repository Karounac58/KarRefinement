package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FurnaceListener implements Listener {

    @EventHandler(priority =  EventPriority.MONITOR)
    public void PlayerInteractEvent(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.hasBlock() && e.getClickedBlock().getType().equals(Material.FURNACE)){
            Player p = e.getPlayer();
            Furnace furnace = (Furnace) e.getClickedBlock().getState();
            furnace.setMetadata("FurnaceOwner",new FixedMetadataValue(KarRefinement.instance,p.getName()));
        }
    }

    @EventHandler(priority =  EventPriority.MONITOR)
    public void FurnaceBurnEvent(FurnaceBurnEvent e) {
        Furnace furnace = (Furnace) e.getBlock().getState();
        ItemStack fuel = e.getFuel().clone();
        ItemStack smelt = furnace.getInventory().getSmelting();
        Stone stone = StoneDataManager.getStone(fuel);

        if (EquipmentDataManager.isEquipmentLegal(smelt)) {
            if (stone != null) {
                furnace.setMetadata("FurnaceFuel", new FixedMetadataValue(KarRefinement.instance, fuel));
                e.setBurning(true);
                e.setBurnTime(200);
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority =  EventPriority.MONITOR)
    public void FurnaceSmeltEvent(FurnaceSmeltEvent e) {
        ItemStack smelt = e.getSource();
        Furnace furnace = (Furnace) e.getBlock().getState();
        if (furnace.hasMetadata("FurnaceFuel")) {
            ItemStack stone = (ItemStack) furnace.getMetadata("FurnaceFuel").get(0).value();
            String name = furnace.hasMetadata("FurnaceOwner") ? furnace.getMetadata("FurnaceOwner").get(0).asString() : "";
            Player p = Bukkit.getPlayer(name);
            smelt.setAmount(1);
            ItemStack stone1 = stone.clone();
            KarRefinementGui.KarRefinementMethod(stone1, smelt, p);
            e.setResult(smelt);
            furnace.removeMetadata("FurnaceFuel", KarRefinement.instance);
        } else if (smelt != null && EquipmentDataManager.isEquipmentLegal(smelt)) {
            e.setResult(smelt);
        }
    }


    @EventHandler(priority =  EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e){
        if (e.getInventory().getType() == InventoryType.FURNACE) {
            Furnace furnace = (Furnace) e.getInventory().getHolder();
            ItemStack smelting = furnace.getInventory().getSmelting();
            if(smelting != null && smelting.getType() != Material.AIR && EquipmentDataManager.isEquipmentLegal(smelting) && furnace.getBurnTime() > 0){
                e.setCancelled(true);
            }
        }
    }
}
