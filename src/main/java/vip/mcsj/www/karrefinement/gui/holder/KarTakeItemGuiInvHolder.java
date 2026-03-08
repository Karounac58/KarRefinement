package vip.mcsj.www.karrefinement.gui.holder;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class KarTakeItemGuiInvHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null,54,"shit");
    }
}
