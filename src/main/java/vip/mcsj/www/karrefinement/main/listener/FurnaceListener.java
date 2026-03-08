package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.FurnaceDataManager;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class FurnaceListener implements Listener {

    @EventHandler(priority =  EventPriority.MONITOR)
    public void PlayerInteractEvent(PlayerInteractEvent e){
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.hasBlock() && e.getClickedBlock().getType().equals(Material.FURNACE)){
            Player p = e.getPlayer();
            Furnace furnace = (Furnace) e.getClickedBlock().getState();
            furnace.setMetadata("FurnaceOwner",new FixedMetadataValue(KarRefinement.instance,p.getUniqueId().toString()));
        }
    }

    @EventHandler(priority =  EventPriority.MONITOR)
    public void FurnaceBurnEvent(FurnaceBurnEvent e) {
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
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
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
        ItemStack smelt = e.getSource();
        Furnace furnace = (Furnace) e.getBlock().getState();
        if (furnace.hasMetadata("FurnaceFuel")) {
            double addSuccess = 0.0;
            if(FurnaceDataManager.furnaceEnabled){
                NBTBlock nbtBlock = new NBTBlock(e.getBlock());

                if(nbtBlock.getData().hasTag("karfurnace")){
                    int equipmentLevel = EquipmentDataManager.getEquipmentLevel(smelt);
                    String key = nbtBlock.getData().getString("karfurnace");
                    vip.mcsj.www.karrefinement.object.Furnace furnace1 = FurnaceDataManager.furnaces.get(key);
                    if(equipmentLevel >= furnace1.getMinLevel() && equipmentLevel <= furnace1.getMaxLevel()) {
                        addSuccess = furnace1.getSuccess();
                    }
                }
            }
            ItemStack stoneItem = (ItemStack) furnace.getMetadata("FurnaceFuel").get(0).value();
            String uuid = furnace.hasMetadata("FurnaceOwner") ? furnace.getMetadata("FurnaceOwner").get(0).asString() : "";
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            smelt.setAmount(1);
            Stone stone = StoneDataManager.getStone(stoneItem);
            if(KarRefinementGui.KarRefinementMethod(p,smelt,stone,addSuccess)){
                KarUtils.removeItemRefinement(stoneItem);
            }
            e.setResult(smelt);
            furnace.removeMetadata("FurnaceFuel", KarRefinement.instance);
        } else if (smelt != null && EquipmentDataManager.isEquipmentLegal(smelt)) {
            e.setResult(smelt);
        }
    }


    @EventHandler(priority =  EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e){
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
        if (e.getInventory().getType() == InventoryType.FURNACE) {
            Furnace furnace = (Furnace) e.getInventory().getHolder();
            ItemStack smelting = furnace.getInventory().getSmelting();
            if(smelting != null && smelting.getType() != Material.AIR && EquipmentDataManager.isEquipmentLegal(smelting) && furnace.getBurnTime() > 0){
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onFurnacePlace(BlockPlaceEvent e) {
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
        if(!FurnaceDataManager.furnaceEnabled){
            return;
        }
        if(!(e.getBlock().getType().equals(Material.FURNACE))){
            return;
        }



        Block block = e.getBlock();
        ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(itemStack);
        if(!(nbtItem.hasTag("karfurnace"))){
            return;
        }
        String karfurnace = nbtItem.getString("karfurnace");

        BlockState state = block.getState();

        new NBTBlock(block).getData().setString("karfurnace", karfurnace);

        state.update();
    }

    @EventHandler
    public void onFurnaceBreak(BlockBreakEvent e) {
        if(!KarRefinement.instance.getConfig().getBoolean("settings.enablefurnace")) {
            return;
        }
        if(!FurnaceDataManager.furnaceEnabled){
            return;
        }
        if(!(e.getBlock().getType().equals(Material.FURNACE))){
            return;
        }
        NBTBlock nbtBlock = new NBTBlock(e.getBlock());
        if(nbtBlock.getData().hasTag("karfurnace")){
            String karfurnace = nbtBlock.getData().getString("karfurnace");
            e.setDropItems(false);
            e.getPlayer().getInventory().addItem(FurnaceDataManager.createFurnace(karfurnace));
            nbtBlock.getData().removeKey("karfurnace");
        }
    }
}
