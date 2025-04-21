package vip.mcsj.www.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.gui.KarCompoundStoneGui;
import vip.mcsj.www.gui.KarCompoundStoneInvHolder;
import vip.mcsj.www.main.KarRefinement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static vip.mcsj.www.gui.KarCompoundStoneGui.*;
import static vip.mcsj.www.main.KarRefinement.cm;

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
            ItemStack stone1 = e.getInventory().getItem(16);
            ItemStack stone2 = e.getInventory().getItem(34);
            ItemStack stone3 = e.getInventory().getItem(19);
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
        List<Integer> buttonIndex = Arrays.asList(37, 38, 39, 40, 41, 42, 43);
        List<Integer> putIndex = Arrays.asList(16, 19, 34);
        if (!putIndex.contains(e.getSlot())) {
            e.setCancelled(true);
        }
        if (e.getClick().equals(ClickType.LEFT) && buttonIndex.contains(e.getSlot())) {
            startCompound(inv, (Player) e.getWhoClicked(),true);
        } else if (e.getClick().equals(ClickType.RIGHT) && buttonIndex.contains(e.getSlot())) {
            startCompound(inv, (Player) e.getWhoClicked(),false);
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
        if(e.getClickedInventory().getItem(16) != null && e.getClickedInventory().getItem(34) != null ){
            NBTItem firstNBT = new NBTItem(e.getClickedInventory().getItem(16));
            NBTItem secondNBT = new NBTItem(e.getClickedInventory().getItem(34));
            if(firstNBT.getString("refinementstone").equals(secondNBT.getString("refinementstone"))){
                if(KarCompoundStoneGui.compoundMap.get(firstNBT.getString("refinementstone")) == null){
                    return;
                }
                double chance = KarCompoundStoneGui.compoundMap.get(firstNBT.getString("refinementstone")).getChance();
                ItemStack sign = cm.getItems().get(6);
                ItemMeta meta = sign.getItemMeta();
                meta.setLore(Arrays.asList(
                        "§a§l成功率：§b§l"+chance
                ));
                meta.setDisplayName("§c§l提示:");
                sign.setItemMeta(meta);
                inv.setItem(4,sign);
            }
        }
    }
}
