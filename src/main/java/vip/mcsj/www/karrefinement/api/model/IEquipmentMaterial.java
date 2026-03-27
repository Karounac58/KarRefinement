package vip.mcsj.www.karrefinement.api.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface IEquipmentMaterial {

    String getRawType();

    Material getMaterial();

    short getData();

    ItemStack getCachedItem();

    boolean isModItem();
}
