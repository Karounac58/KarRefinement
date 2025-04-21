package vip.mcsj.www.version;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface CustomMaterial {
    List<ItemStack> items = new ArrayList<>();

    List<ItemStack> getItems();
}
