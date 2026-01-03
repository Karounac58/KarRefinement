package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.object.EquipmentMaterial;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class EquipmentTypeManager {

    private static final Logger log = Logger.getLogger("Minecraft");

    // 存储各部位的装备材料
    public static final Map<String, List<EquipmentMaterial>> equipmentMap = new HashMap<>();


    // 部位常量
    public static final String HAND = "Hand";
    public static final String HELMET = "Helmet";
    public static final String CHESTPLATE = "Chestplate";
    public static final String LEGGINGS = "Leggings";
    public static final String BOOTS = "Boots";

    /**
     * 初始化装备配置
     */
    public static void init() {
        equipmentMap.clear();

        File file = new File(KarRefinement.instance.getDataFolder(), "items.yml");

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // 加载各部位装备
        loadSlot(config, HAND);
        loadSlot(config, HELMET);
        loadSlot(config, CHESTPLATE);
        loadSlot(config, LEGGINGS);
        loadSlot(config, BOOTS);

        for (String key : config.getKeys(false)) {
            List<String> materials = config.getStringList(key);
            List<EquipmentMaterial> list = new ArrayList<>();
            EquipmentDataManager.canRefinementEquipment.put(key,materials);
            for (String mat : materials) {
                EquipmentMaterial em = new EquipmentMaterial(mat);
                if (em.getMaterial() != null) {
                    list.add(em);
                }
            }

            KarRefinement.types.addAll(list);
        }

        log.info("[KarRefinement] 装备配置加载完成，共加载 " + getTotalCount() + " 种装备");
    }

    /**
     * 加载指定部位的装备
     */
    private static void loadSlot(FileConfiguration config, String slot) {
        List<String> materials = config.getStringList(slot);
        List<EquipmentMaterial> list = new ArrayList<>();

        for (String mat : materials) {
            EquipmentMaterial em = new EquipmentMaterial(mat);
            if (em.getMaterial() != null) {
                list.add(em);
            }
        }

        equipmentMap.put(slot, list);
        log.info("[KarRefinement] 加载 " + slot + " 装备: " + list.size() + " 种");
    }

    /**
     * 获取指定部位的所有装备材料
     */
    public static List<EquipmentMaterial> getEquipments(String slot) {
        return equipmentMap.getOrDefault(slot, Collections.emptyList());
    }

    /**
     * 检查物品是否属于指定部位
     */
    public static boolean isValidEquipment(String slot, ItemStack item) {
        if (item == null) return false;

        List<EquipmentMaterial> list = equipmentMap.get(slot);
        if (list == null) return false;

        for (EquipmentMaterial em : list) {
            if (em.matches(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查物品属于哪个部位
     */
    public static String getSlot(ItemStack item) {
        if (item == null) return null;

        for (Map.Entry<String, List<EquipmentMaterial>> entry : equipmentMap.entrySet()) {
            for (EquipmentMaterial em : entry.getValue()) {
                if (em.matches(item)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 检查物品是否为有效装备（任意部位）
     */
    public static boolean isAnyValidEquipment(ItemStack item) {
        return getSlot(item) != null;
    }

    /**
     * 获取总装备数量
     */
    public static int getTotalCount() {
        int count = 0;
        for (List<EquipmentMaterial> list : equipmentMap.values()) {
            count += list.size();
        }
        return count;
    }

    /**
     * 重载配置
     */
    public static void reload() {
        init();
    }
}