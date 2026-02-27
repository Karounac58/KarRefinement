package vip.mcsj.www.karrefinement.service;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.object.*;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.Map;

/**
 * 物品工厂
 * 统一管理各类物品的创建逻辑，替代分散在各 DataManager 中的 createXxx() 方法
 */
public class ItemFactory {

    public ItemStack createStone(String identifier) {
        StoneDataManager manager = new StoneDataManager(identifier);
        return manager.createStone();
    }

    public ItemStack createProtectPaper(String identifier) {
        PaperDataManager manager = new PaperDataManager(identifier);
        return manager.createProtectedPaper();
    }

    public ItemStack createSpecialStone(String identifier) {
        SpeStone stone = SpecialStoneDataManager.speStones.get(identifier);
        if (stone == null) return null;
        ItemStack item = new ItemStack(stone.getType(), 1, (short) stone.getData());
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(stone.getName());
        im.setLore(stone.getLore());
        item.setItemMeta(im);
        ReflectionUtils.setCustomModelData(item, stone.getCustomModelData());
        NBT.modify(item, nbt -> {
            nbt.setString(stone.getNbtKey(), stone.getIdentifier());
        });
        return item;
    }

    public ItemStack createInfiniteSoul(String identifier) {
        InfiniteSoulManager manager = new InfiniteSoulManager(identifier);
        return manager.createInfiniteSoul();
    }

    public ItemStack createDUPaper(String identifier) {
        return DUPaperDataManager.createDUPaper(identifier);
    }

    public ItemStack createAdhesive(String identifier) {
        return AdhesiveDataManager.createAdhesiveItem(identifier);
    }

    public ItemStack createPotion(String identifier) {
        PotionDataManager manager = new PotionDataManager(identifier);
        return manager.createPotion();
    }

    public ItemStack createDetachItem(String type) {
        if ("paper".equalsIgnoreCase(type)) {
            return DetachDataManager.createPaperDetachItem();
        } else if ("spestone".equalsIgnoreCase(type)) {
            return DetachDataManager.createSpeStoneDetachItem();
        }
        return null;
    }

    public ItemStack createFurnace(String identifier) {
        return FurnaceDataManager.createFurnace(identifier);
    }
}
