package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.effect.*;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.SuitEffect;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EffectDataManager implements Service {
    private static Map<String, BukkitTask> taskMap = new ConcurrentHashMap<>();

    @Override
    public void initialize() {
        // EffectDataManager 没有配置加载，仅管理运行时任务
    }

    @Override
    public void shutdown() {
        // 关闭所有正在运行的任务
        for (BukkitTask task : taskMap.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        taskMap.clear();
    }

    public static void removeTaskFromMap(String name) {
        taskMap.remove(name);
    }

    public static void setTaskNull(String name){
        taskMap.put(name,null);
    }

    public static void addTaskToMap(String name,BukkitTask tasks) {
        taskMap.put(name,tasks);
    }

    public static BukkitTask getTask(String name){
        return taskMap.get(name);
    }

    public static int judgeRightLevelEquipment(Player p){
        int[] levels = new int[5];
        ItemStack[] armorContents = p.getInventory().getArmorContents();
        for (int i = 0; i < armorContents.length; i++) {
            if(armorContents[i] == null){
                return 0;
            }
            EquipmentDataManager equipment = new EquipmentDataManager(armorContents[i]);
            levels[i] = equipment.carifyEquipmentLevel();
        }
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
        if(itemInMainHand.getType() == Material.AIR){
            return 0;
        }
        EquipmentDataManager sword = new EquipmentDataManager(itemInMainHand);
        levels[4] = sword.carifyEquipmentLevel();
        return judgeLevel(levels);
    }

    /**
     * 根据装备等级数组判断套装等级
     * 
     * @param levels 装备等级数组 [头盔, 胸甲, 护腿, 靴子, 主手武器]
     * @return 套装等级 (0-5)
     */
    private static int judgeLevel(int[] levels){
        if(levels[0] == 0 || levels[1] == 0 || levels[2] == 0 || levels[3] == 0 || levels[4] == 0){
            return 0;
        }else if(arrayRange(levels,18,18)) {
            return 5;
        }else if(arrayRange(levels,15,15)){
            return 4;
        }else if(arrayRange(levels,12,12)){
            return 3;
        }else if(arrayRange(levels,9,9)){
            return 2;
        }else if(arrayRange(levels,6,6)){
            return 1;
        }
        return 0;
    }

    /**
     * 检查所有装备是否满足最低等级要求，且主手武器满足剑等级要求
     */
    private static boolean arrayRange(int[] levels,int min,int swordLevel){
        return levels[0] >= min && levels[1] >= min && levels[2] >= min && levels[3] >= min && levels[4] >= swordLevel;
    }

    /**
     * 加载套装效果脚本文件
     * 如果文件不存在则创建默认脚本
     * 
     * @param name 脚本文件名
     * @return 脚本内容
     */
    public static String loadSuitEffectScriptStr(String name) {
        if (!KarRefinement.instance.getDataFolder().exists()) {
            KarRefinement.instance.getDataFolder().mkdir();
        }
        File folder = new File(KarRefinement.instance.getDataFolder(), "script");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileUtil.write(file, SuitEffect.defaultScript);
            } catch (IOException ex) {
                KarRefinement.instance.getLogger().warning("创建脚本文件失败: " + name);
            }
        }
        return FileUtil.read(file);
    }
}
