package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.api.gui.GuiContext;
import vip.mcsj.www.karrefinement.api.gui.GuiSlot;
import vip.mcsj.www.karrefinement.api.gui.GuiSlotRegistry;
import vip.mcsj.www.karrefinement.api.gui.GuiType;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.gui.KarCompoundStoneGui;
import vip.mcsj.www.karrefinement.gui.holder.KarCompoundStoneInvHolder;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.service.gui.SimpleGuiContext;

import java.util.Arrays;
import java.util.List;

public class KarCompoundGUIListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof KarCompoundStoneInvHolder)) {
            return;
        }
        Player p = (Player) e.getPlayer();
        if (KarCompoundStoneGui.compoundOrNot.getOrDefault(e.getPlayer().getUniqueId(),0) == 1) {
            Bukkit.getScheduler().runTask(KarRefinement.instance, () -> {
                p.openInventory(e.getInventory());
            });
            return;
        } else {
            ItemStack stone1 = e.getInventory().getItem(KarCompoundStoneGui.originSlot1);
            ItemStack stone2 = e.getInventory().getItem(KarCompoundStoneGui.originSlot2);
            ItemStack stone3 = e.getInventory().getItem(KarCompoundStoneGui.resultSlot);
            if (stone1 != null) {
                p.getInventory().addItem(stone1);
            }
            if (stone2 != null) {
                p.getInventory().addItem(stone2);
            }
            if(stone3 != null){
                p.getInventory().addItem(stone3);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() == null) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }
        Inventory inv = e.getClickedInventory();
        if (!(inv.getHolder() instanceof KarCompoundStoneInvHolder)) {
            return;
        }
        if(KarCompoundStoneGui.compoundOrNot.getOrDefault(e.getWhoClicked().getUniqueId(),0) == 1){
            e.setCancelled(true);
            return;
        }
        List<Integer> buttonIndex = KarCompoundStoneGui.cItems.get("ConfirmButton").getSlots();
        List<Integer> putIndex = Arrays.asList(
                KarCompoundStoneGui.originSlot1,
                KarCompoundStoneGui.originSlot2,
                KarCompoundStoneGui.resultSlot
        );
        if (!putIndex.contains(e.getSlot())) {
            // 检查是否为自定义槽位
            if (KarRefinementAPI.getInstance() != null) {
                GuiSlotRegistry registry = KarRefinementAPI.getGuiSlotRegistry();
                for (GuiSlot slot : registry.getSlots(GuiType.COMPOUND_STONE)) {
                    if (slot.getSlotIndex() == e.getSlot()) {
                        e.setCancelled(true);
                        Player slotPlayer = (Player) e.getWhoClicked();
                        GuiContext context = new SimpleGuiContext(GuiType.COMPOUND_STONE, inv, slotPlayer, registry.getSlots(GuiType.COMPOUND_STONE));
                        slot.onClick(e, context);
                        return;
                    }
                }
            }
            e.setCancelled(true);
        }
        if (e.getClick().equals(ClickType.LEFT) && buttonIndex.contains(e.getSlot())) {
            KarCompoundStoneGui.startCompound(inv, (Player) e.getWhoClicked(),true);
        } else if (e.getClick().equals(ClickType.RIGHT) && buttonIndex.contains(e.getSlot())) {
            KarCompoundStoneGui.startCompound(inv, (Player) e.getWhoClicked(),false);
        }
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent e){
        if (e.getInventory() == null) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }
        Inventory inv = e.getClickedInventory();
        if (!(inv.getHolder() instanceof KarCompoundStoneInvHolder)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(KarRefinement.instance, () -> {
            if(e.getClickedInventory().getItem(KarCompoundStoneGui.originSlot1) != null && e.getClickedInventory().getItem(KarCompoundStoneGui.originSlot2) != null ){
                NBTItem firstNBT = new NBTItem(e.getClickedInventory().getItem(16));
                NBTItem secondNBT = new NBTItem(e.getClickedInventory().getItem(34));
                if(firstNBT.getString("refinementstone").equals(secondNBT.getString("refinementstone"))){
                    if(KarCompoundStoneGui.compoundMap.get(firstNBT.getString("refinementstone")) == null){
                        return;
                    }
                    double chance = KarCompoundStoneGui.compoundMap.get(firstNBT.getString("refinementstone")).getChance();
                    ItemStack sign = KarRefinement.cm.getItems().get(6);
                    ItemMeta meta = sign.getItemMeta();
                    meta.setLore(Arrays.asList(
                            Message.messages.get("compound_success").replace("{success}",chance+"")
                    ));
                    meta.setDisplayName("§c§l提示:");
                    sign.setItemMeta(meta);
                    inv.setItem(4,sign);
                }
            }
        },1L);
    }
}
