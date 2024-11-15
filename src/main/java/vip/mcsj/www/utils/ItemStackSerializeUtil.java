package vip.mcsj.www.utils;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemStackSerializeUtil {


    /**
     * 序列化ItemStack为String
     * @param itemStack 需要序列化的ItemStack
     * @return ItemStack序列化后的字符串
     */
    public static String itemStackSerialize(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }


    /**
     * 反序列化String为ItemStack
     * @param str ItemStack序列化后的字符串
     * @return 反序列化字符串后得到的ItemStack
     */
    public static ItemStack itemStackDeserialize(String str) {
        YamlConfiguration yml = new YamlConfiguration();
        ItemStack item;
        try {
            yml.loadFromString(str);
            item = yml.getItemStack("item");
        } catch (InvalidConfigurationException ex) {
            item = new ItemStack(Material.AIR, 1);
        }
        return item;

    }
}
