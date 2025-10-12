package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import vip.mcsj.www.karrefinement.effect.*;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EffectDataManager {
    private static Map<String, BukkitTask> taskMap = new ConcurrentHashMap<>();

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

    //药水效果
    public static void handlePlayerPotionEffect(Player p){
        Level minLevel = LevelDataManager.getMinLevel(p);
        if(minLevel != null){
            List<PotionEffect> potionEffects = minLevel.getPotionEffects();
            if(minLevel.getPotionEffects().size() != 0){
                for (PotionEffect potionEffect : potionEffects) {
                    p.removePotionEffect(potionEffect.getType());
                    p.addPotionEffect(potionEffect);
                }
            }
        }
    }
    //套装特效
    public static void handlePlayerEffect(Player p){
        int i = judgeRightLevelEquipment(p);
        switch (i){
            case 0:
                if(taskMap.get(p.getName()) != null) {
                    CrownEffect effect = new CrownEffect(p);
                    effect.stopEffect();
                }
                break;
            case 1:
                if(taskMap.get(p.getName()) == null) {
                    p.sendMessage(ChatColor.GREEN + "激活六星套装效果！");
                    CrownEffect effect = new CrownEffect(p);
                    effect.startEffect();
                }
                break;
            case 2:
                if(taskMap.get(p.getName()) == null) {
                    p.sendMessage(ChatColor.GREEN + "激活九星套装效果！");
                    CrownEffect2 effect2 = new CrownEffect2(p);
                    effect2.startEffect();
                }
                break;
            case 3:
                if(taskMap.get(p.getName()) == null) {
                    p.sendMessage(ChatColor.GREEN + "激活十二星套装效果！");
                    CrownEffect3 effect3 = new CrownEffect3(p);
                    effect3.startEffect();
                }
                break;
            case 4:
                if(taskMap.get(p.getName()) == null) {
                    p.sendMessage(ChatColor.GREEN + "激活十五星套装效果！");
                    CrownEffect4 effect4 = new CrownEffect4(p);
                    effect4.startEffect();
                }
                break;
            case 5:
                if(taskMap.get(p.getName()) == null) {
                    p.sendMessage(ChatColor.GREEN + "激活十八星套装效果！");
                    CrownEffect5 effect5 = new CrownEffect5(p);
                    effect5.startEffect();
                }
                break;
        }
    }

    public static boolean arrayRange(int[] levels,int min,int swordLevel){
//        return range(levels[0],min,max) && range(levels[1],min,max) && range(levels[2],min,max) && range(levels[3],min,max) && levels[4] >= swordLevel;
        return levels[0] >= min && levels[1] >= min && levels[2] >= min && levels[3] >= min && levels[4] >= swordLevel;
    }

    public static Map<String,BukkitTask> getTaskMap() {
        return taskMap;
    }

}
