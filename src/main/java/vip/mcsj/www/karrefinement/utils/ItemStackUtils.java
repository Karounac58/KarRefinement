package vip.mcsj.www.karrefinement.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemStackUtils {
    //实现减少玩家指定数量的物品
    public static void reducePlayerItemStack(Inventory inv, ItemStack judgeItem, int amount){
        //要减少的物品 做减法
        int k = amount;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item == null){
                continue;
            }
            if(Objects.equals(item.getItemMeta(),judgeItem.getItemMeta()) && Objects.equals(item.getType(),judgeItem.getType())){
                int singleAmount = item.getAmount();
                //每次遇到一个物品堆 就判断要减少的量是不是大于这个物品堆的数量
                if(k-singleAmount > 0){
                    item.setAmount(0);
                    k -= singleAmount;
                }else if(k - singleAmount == 0){
                    item.setAmount(0);
                    k = 0;
                }else if(k - singleAmount < 0){
                    item.setAmount(singleAmount - k);
                    k = 0;
                }
            }
        }
    }

    //判断玩家有多少指定物品，返回数量
    public static int judgePlayerItemStackAmount(Inventory inv,ItemStack judgeItem){
        int k = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item == null){
                continue;
            }
            if(Objects.equals(item.getItemMeta(),judgeItem.getItemMeta())){
                k += item.getAmount();
            }
        }
        return k;
    }
}
