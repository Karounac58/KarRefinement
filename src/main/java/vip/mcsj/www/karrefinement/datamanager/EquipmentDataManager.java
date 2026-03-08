package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.JoinMessage;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.EquipmentService;
import vip.mcsj.www.karrefinement.service.LoreBuilder;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.*;

/**
 * 装备数据管理器（门面类）
 * 原 1144 行已拆分为 LoreBuilder / EquipmentRepository / EquipmentService
 * 此类保留原有静态 API 以兼容现有调用方，逐步迁移后可删除
 */
public class EquipmentDataManager{

    // ==================== 静态配置字段（保留，供 LoreBuilder 等引用） ====================

    public static final Map<String, List<String>> canRefinementEquipment = new HashMap<>();
    public static final Map<Integer, Double> forgeSuccessList = new HashMap<>();
    public static final Map<String, String> chinesenames = new HashMap<>();

    public static boolean allowAfterItemIsRefinement = true;
    public static int transformCost = 1000000;
    public static boolean allowTransformOther = true;
    public static boolean allowTransformSpeStoneConflict = false;

    public static String mainLore = "";
    public static String speStoneLore = "";
    public static List<String> sortOrder = new ArrayList<>();

    public static Map<String, JoinMessage> joinMessage = new HashMap<>();

    public static boolean enableDisplayNameInfo = true;
    public static String displayNameSuffix = "";
    public static int broadcastLevel = 6;

    // ==================== 实例字段 ====================

    private final ItemStack equipmentItem;
    private final Player p;
    private final EquipmentService service;


    public EquipmentDataManager(ItemStack equipmentItem) {
        this.equipmentItem = equipmentItem;
        this.p = null;
        this.service = new EquipmentService(equipmentItem);
    }

    public EquipmentDataManager(ItemStack equipmentItem, Player p) {
        this.equipmentItem = equipmentItem;
        this.p = p;
        this.service = new EquipmentService(equipmentItem);
    }

    // ==================== 静态初始化方法 ====================

    public static void init() {
        if (!canRefinementEquipment.isEmpty()) {
            canRefinementEquipment.clear();
        }
        if (!chinesenames.isEmpty()) {
            chinesenames.clear();
        }
//        EquipmentTypeManager.init();

        KarRefinement.instance.reloadConfig();
        mainLore = KarRefinement.instance.getConfig().getString("mainLore");
        speStoneLore = KarRefinement.instance.getConfig().getString("speStoneLore");
        enableDisplayNameInfo = KarRefinement.instance.getConfig().getBoolean("DisplayNameInfo.enabled");
        displayNameSuffix = KarRefinement.instance.getConfig().getString("DisplayNameInfo.suffix");
        sortOrder = KarRefinement.instance.getConfig().getStringList("settings.SortOrder");
        broadcastLevel = KarRefinement.instance.getConfig().getInt("broadcastlevel");
        ConfigurationSection jmCS = KarRefinement.instance.getConfig().getConfigurationSection("settings.JoinMessage");
        if (jmCS != null) {
            Set<String> keys1 = jmCS.getKeys(false);
            for (String key : keys1) {
                String permission = jmCS.getString(key + ".permission");
                List<String> message = jmCS.getStringList(key + ".msg");
                joinMessage.put(key, new JoinMessage(permission, message));
            }
        }
        YamlConfiguration chineseNameYaml = FileUtil.getCustomFileYaml("chinesename.yml");
        chineseNameYaml.getKeys(false).forEach(key -> {
            chinesenames.put(key, chineseNameYaml.getString(key));
        });
        initForgeData();
        initTransformData();
    }

    public static void initForgeData() {
        if (!forgeSuccessList.isEmpty()) {
            forgeSuccessList.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("forge.yml");
        Set<String> chanceSet = customFileYaml.getKeys(false);
        for (String key : chanceSet) {
            Double chance = customFileYaml.getDouble(key + ".Chance");
            forgeSuccessList.put(Integer.parseInt(key), chance);
        }
    }

    public static void initTransformData() {
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("transform.yml");
        allowAfterItemIsRefinement = customFileYaml.getBoolean("allowAfterItemIsRefinement");
        transformCost = customFileYaml.getInt("money");
        allowTransformOther = customFileYaml.getBoolean("allowTransformOther");
        allowTransformSpeStoneConflict = customFileYaml.getBoolean("allowTransformSpeStoneConflict");
    }

    // ==================== 实例方法 — 委托到 EquipmentService ====================

    public int carifyEquipmentLevel() {
        return service.getLevel();
    }

    public static int carifyEquipmentLevel(ItemStack equipmentItem) {
        return EquipmentService.getLevel(equipmentItem);
    }

    public static int getEquipmentLevel(ItemStack item) {
        return EquipmentService.getLevel(item);
    }

    public boolean injuryUpStar() {
        return service.injuryUpStar();
    }

    public boolean setRefinementLevel(int level) {
        return service.setRefinementLevel(level);
    }

    public boolean setRefinementLevel(int level, Map<String, List<String>> map, int paperLevel,
                                      List<SpeStone> speStones, int soulLevel,Player p) {
        return service.setRefinementLevel(level, map, paperLevel, speStones, soulLevel, p);
    }

    public int injuryDownStar(int protectPaperLevel, Stone stone) {
        return service.injuryDownStar(protectPaperLevel, stone);
    }

    public Map<String, List<String>> removeNowItemRefinementInfo(int nowLevel) {
        return service.removeRefinementInfo(nowLevel);
    }

    // ==================== 静态方法 — 委托到 LoreBuilder / EquipmentService ====================

    public static Map<String, List<String>> getEquipmentInfoLore(ItemStack equipmentItem) {
        return LoreBuilder.getEquipmentInfoLore(equipmentItem);
    }

    public static int randomDownLevel(int[] nums) {
        return EquipmentService.randomDownLevel(nums);
    }

    public static boolean isEquipmentLegal(ItemStack itemEquipment) {
        for (String s : canRefinementEquipment.keySet()) {
            List<String> equipmentList = canRefinementEquipment.get(s);
            for (String s1 : equipmentList) {
                if (s1.equals(itemEquipment.getType().name())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getEquipmentIdentifier(ItemStack itemEquipment) {
        for (String s : canRefinementEquipment.keySet()) {
            List<String> equipmentList = canRefinementEquipment.get(s);
            for (String s1 : equipmentList) {
                if (s1.equals(itemEquipment.getType().name())) {
                    return s;
                }
            }
        }
        return null;
    }

    public static int getMinLevelFromEquipments(Player p) {
        ItemStack[] armors = p.getInventory().getArmorContents();
        for (int i = 0; i < armors.length; i++) {
            if (armors[i] == null) {
                return 0;
            }
        }
        int minLevel = 100000;
        for (ItemStack armor : armors) {
            int equipmentLevel = EquipmentService.getLevel(armor);
            if (equipmentLevel < minLevel) {
                minLevel = equipmentLevel;
            }
        }
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.AIR) {
            return 0;
        }
        int equipmentLevel = EquipmentService.getLevel(itemInMainHand);
        if (equipmentLevel < minLevel) {
            minLevel = equipmentLevel;
        }
        return minLevel;
    }
}
