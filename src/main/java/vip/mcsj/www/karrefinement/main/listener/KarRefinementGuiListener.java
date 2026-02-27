package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.gui.KarRefinementInvHolder;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.object.Stone;

import java.util.List;
import java.util.stream.Collectors;

public class KarRefinementGuiListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }

        if(!(e.getClickedInventory().getHolder() instanceof KarRefinementInvHolder)){
//            System.out.println("1");
            return;
        }

        //29 31
        Bukkit.getScheduler().runTaskLater(KarRefinement.instance,() -> {
            Inventory inv = e.getClickedInventory();
            ItemStack item1 = inv.getItem(29);
            ItemStack item2 = inv.getItem(33);
            if(item1 != null && item2 != null){
                NBTItem nbtItem1 = new NBTItem(item1);
                NBTItem nbtItem2 = new NBTItem(item2);
                if(nbtItem1.hasTag("refinementstone")){
                    Stone stone = StoneDataManager.getStone(item1);
                    int level = EquipmentDataManager.carifyEquipmentLevel(item2);
//                    System.out.println(level);
                    if(level == stone.getProbability().size()){
                        InvItem confirmButton = KarRefinementGui.rItems.get("ConfirmButton");
                        ItemStack invItem = KarRefinementGui.createInvItem(confirmButton,(Player) e.getWhoClicked());
                        ItemMeta im = invItem.getItemMeta();
                        List<String> lore = im.getLore();
                        lore = lore.stream().map(s -> s.replace("{chance}", Message.messages.get("refinement_gui_chance_maxlevel")))
                                .collect(Collectors.toList());
                        im.setLore(lore);
                        invItem.setItemMeta(im);
                        inv.setItem(49, invItem);
                        return;
                    }
                    Double chance = stone.getProbability().get(level);
                    InvItem confirmButton = KarRefinementGui.rItems.get("ConfirmButton");
                    ItemStack invItem = KarRefinementGui.createInvItem(confirmButton,(Player) e.getWhoClicked());
                    ItemMeta im = invItem.getItemMeta();
                    List<String> lore = im.getLore();
                    lore = lore.stream().map(s -> s.replace("{chance}",String.valueOf(chance)))
                            .collect(Collectors.toList());
                    im.setLore(lore);
                    invItem.setItemMeta(im);
                    inv.setItem(49, invItem);
                }
            }
        },1L);
    }
}
