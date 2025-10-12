package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBT;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.main.KarRefinement;

public class FurnaceListener implements Listener {
    private boolean isBurnning = false;

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerInteractEvent(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.hasBlock() && e.getClickedBlock().getType().equals(Material.FURNACE)){
            Player p = e.getPlayer();
            Furnace furnace = (Furnace) e.getClickedBlock().getState();
            furnace.setMetadata("FurnaceOwner",new FixedMetadataValue(KarRefinement.instance,p.getName()));
            isBurnning = false;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void FurnaceBurnEvent(FurnaceBurnEvent e) {
        Furnace furnace = (Furnace) e.getBlock().getState();
        ItemStack fuel = e.getFuel();
        ItemStack smelt = furnace.getInventory().getSmelting();

        String stoneIdentify = NBT.get(fuel, nbt -> (String) nbt.getString("refinementstone"));
        if (EquipmentDataManager.isEquipmentLegal(smelt)) {
            if (stoneIdentify != null) {
                ItemStack stone = new StoneDataManager(stoneIdentify).createStone();
                furnace.setMetadata("FurnaceFuel", new FixedMetadataValue(KarRefinement.instance, stone));
                e.setBurning(true);
                e.setBurnTime(200);
                isBurnning = true;
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void FurnaceSmeltEvent(FurnaceSmeltEvent e) {
        ItemStack smelt = e.getSource();
        Furnace furnace = (Furnace) e.getBlock().getState();
        if (furnace.hasMetadata("FurnaceFuel")) {
            ItemStack itemStone = (ItemStack) furnace.getMetadata("FurnaceFuel").get(0).value();
            String name = furnace.hasMetadata("FurnaceOwner") ? furnace.getMetadata("FurnaceOwner").get(0).asString() : "";
            Player p = Bukkit.getPlayer(name);
            smelt.setAmount(1);
            KarRefinementGui.KarRefinementMethod(itemStone, smelt, p);
            e.setResult(smelt);
            furnace.removeMetadata("FurnaceFuel", KarRefinement.instance);
            isBurnning = false;
        } else if (EquipmentDataManager.isEquipmentLegal(smelt)) {
            e.setResult(smelt);
        }
    }


    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e){
        if(e.getClickedInventory() != null
                && e.getClickedInventory().getType() == InventoryType.FURNACE
                && e.getSlotType() != InventoryType.SlotType.RESULT){
            if(isBurnning){
                e.setCancelled(true);
            }
        }
    }
}
