package vip.mcsj.www.karrefinement.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.MCVersions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    private static int[] getSpigotVersion(){
        return Arrays.stream(Bukkit.getBukkitVersion().split("-")[0].split("\\.")).filter( s ->{
                try{
                    Integer.parseInt(s);
                    return true;
                }catch (Exception e){
                    return false;
                }
            }
        ).mapToInt(Integer::parseInt).toArray();
    }

    public static MCVersions judgeVersion(){
        int[] spigotVersion = getSpigotVersion();

        if(spigotVersion[0] == 26){
            return MCVersions.v121;
        }

        if(spigotVersion[1] > 20){
            return MCVersions.v121;
        }else if(spigotVersion[1] < 13){
            return MCVersions.v1122;
        }else{
            return MCVersions.v1201;
        }
    }

    public static ItemStack setCustomModelData(ItemStack item, int cmd) {
        if (item == null || item.getType() == Material.AIR) {
            return item;
        }

        int[] version = getSpigotVersion();
        if (version[1] < 14) {
            return item;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }

        try {
            // 从 ItemMeta 接口获取方法，而非实现类
            Method setCustomModelData = ItemMeta.class.getMethod("setCustomModelData", Integer.class);
            setCustomModelData.invoke(itemMeta, cmd);
            item.setItemMeta(itemMeta);
        } catch (NoSuchMethodException e) {
            // 1.14 以下版本没有此方法，忽略
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }


}
