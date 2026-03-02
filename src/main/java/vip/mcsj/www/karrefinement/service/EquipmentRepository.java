package vip.mcsj.www.karrefinement.service;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.core.PluginContext;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.datamanager.EquipmentTypeManager;
import vip.mcsj.www.karrefinement.object.JoinMessage;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.*;

/**
 * 装备配置数据仓库
 * 从 EquipmentDataManager 提取，负责配置数据加载/查询
 */
public class EquipmentRepository implements Service {

    private final PluginContext context;

    // 可淬炼装备映射
    private final Map<String, List<String>> canRefinementEquipment = new HashMap<>();
    // 装备类型名 -> 材质名 的快速查找 Set
    private final Set<String> legalMaterialNames = new HashSet<>();
    // 材质名 -> 装备标识符 的反向映射
    private final Map<String, String> materialToIdentifier = new HashMap<>();

    // 锻造成功率
    private final Map<Integer, Double> forgeSuccessList = new HashMap<>();
    // 中文名
    private final Map<String, String> chinesenames = new HashMap<>();
    // 入服消息
    private final Map<String, JoinMessage> joinMessage = new HashMap<>();

    // 配置值
    private boolean allowAfterItemIsRefinement = true;
    private int transformCost = 1000000;
    private boolean allowTransformOther = true;
    private boolean allowTransformSpeStoneConflict = false;
    private String mainLore = "";
    private String speStoneLore = "";
    private List<String> sortOrder = new ArrayList<>();
    private boolean enableDisplayNameInfo = true;
    private String displayNameSuffix = "";
    private int broadcastLevel = 6;

    public EquipmentRepository(PluginContext context) {
        this.context = context;
    }

    @Override
    public void initialize() {
        canRefinementEquipment.clear();
        legalMaterialNames.clear();
        materialToIdentifier.clear();
        chinesenames.clear();
        joinMessage.clear();
        forgeSuccessList.clear();

//        // 装备类型初始化
//        EquipmentTypeManager.init();

        // 主配置
        context.getPlugin().reloadConfig();
        mainLore = context.getPlugin().getConfig().getString("mainLore");
        speStoneLore = context.getPlugin().getConfig().getString("speStoneLore");
        enableDisplayNameInfo = context.getPlugin().getConfig().getBoolean("DisplayNameInfo.enabled");
        displayNameSuffix = context.getPlugin().getConfig().getString("DisplayNameInfo.suffix");
        sortOrder = context.getPlugin().getConfig().getStringList("settings.SortOrder");
        broadcastLevel = context.getPlugin().getConfig().getInt("broadcastlevel");

        // 入服消息
        ConfigurationSection jmCS = context.getPlugin().getConfig().getConfigurationSection("settings.JoinMessage");
        if (jmCS != null) {
            for (String key : jmCS.getKeys(false)) {
                String permission = jmCS.getString(key + ".permission");
                List<String> message = jmCS.getStringList(key + ".msg");
                joinMessage.put(key, new JoinMessage(permission, message));
            }
        }

        // 中文名
        YamlConfiguration chineseNameYaml = FileUtil.getCustomFileYaml("chinesename.yml");
        chineseNameYaml.getKeys(false).forEach(key -> {
            chinesenames.put(key, chineseNameYaml.getString(key));
        });

        // 锻造数据
        initForgeData();

        // 移星数据
        initTransformData();

        // 构建快速查找索引
        buildLookupIndex();
    }

    private void initForgeData() {
        YamlConfiguration yaml = FileUtil.getCustomFileYaml("forge.yml");
        for (String key : yaml.getKeys(false)) {
            Double chance = yaml.getDouble(key + ".Chance");
            forgeSuccessList.put(Integer.parseInt(key), chance);
        }
    }

    private void initTransformData() {
        YamlConfiguration yaml = FileUtil.getCustomFileYaml("transform.yml");
        allowAfterItemIsRefinement = yaml.getBoolean("allowAfterItemIsRefinement");
        transformCost = yaml.getInt("money");
        allowTransformOther = yaml.getBoolean("allowTransformOther");
        allowTransformSpeStoneConflict = yaml.getBoolean("allowTransformSpeStoneConflict");
    }

    /**
     * 构建材质名 -> 装备标识符的快速查找索引
     * 将 isEquipmentLegal 和 getEquipmentIdentifier 从 O(n*m) 优化为 O(1)
     */
    private void buildLookupIndex() {
        for (Map.Entry<String, List<String>> entry : canRefinementEquipment.entrySet()) {
            String identifier = entry.getKey();
            for (String materialName : entry.getValue()) {
                legalMaterialNames.add(materialName);
                materialToIdentifier.put(materialName, identifier);
            }
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 判断物品是否为合法的可淬炼装备（O(1) 查找）
     */
    public boolean isEquipmentLegal(ItemStack itemEquipment) {
        return legalMaterialNames.contains(itemEquipment.getType().name());
    }

    /**
     * 获取装备标识符（如 Hand, Helmet 等）（O(1) 查找）
     */
    public String getEquipmentIdentifier(ItemStack itemEquipment) {
        return materialToIdentifier.get(itemEquipment.getType().name());
    }

    /**
     * 获取玩家穿戴的所有装备中的最低淬炼等级
     */
    public int getMinLevelFromEquipments(Player p) {
        ItemStack[] armors = p.getInventory().getArmorContents();
        for (ItemStack armor : armors) {
            if (armor == null) {
                return 0;
            }
        }
        int minLevel = Integer.MAX_VALUE;
        for (ItemStack armor : armors) {
            int level = new de.tr7zw.nbtapi.NBTItem(armor).getInteger("refinement");
            if (level < minLevel) {
                minLevel = level;
            }
        }
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.AIR) {
            return 0;
        }
        int handLevel = new de.tr7zw.nbtapi.NBTItem(itemInMainHand).getInteger("refinement");
        if (handLevel < minLevel) {
            minLevel = handLevel;
        }
        return minLevel;
    }

    // ==================== Getters ====================

    public Map<String, List<String>> getCanRefinementEquipment() {
        return canRefinementEquipment;
    }

    public Map<Integer, Double> getForgeSuccessList() {
        return forgeSuccessList;
    }

    public Map<String, String> getChinesenames() {
        return chinesenames;
    }

    public Map<String, JoinMessage> getJoinMessage() {
        return joinMessage;
    }

    public boolean isAllowAfterItemIsRefinement() {
        return allowAfterItemIsRefinement;
    }

    public int getTransformCost() {
        return transformCost;
    }

    public boolean isAllowTransformOther() {
        return allowTransformOther;
    }

    public boolean isAllowTransformSpeStoneConflict() {
        return allowTransformSpeStoneConflict;
    }

    public String getMainLore() {
        return mainLore;
    }

    public String getSpeStoneLore() {
        return speStoneLore;
    }

    public List<String> getSortOrder() {
        return sortOrder;
    }

    public boolean isEnableDisplayNameInfo() {
        return enableDisplayNameInfo;
    }

    public String getDisplayNameSuffix() {
        return displayNameSuffix;
    }

    public int getBroadcastLevel() {
        return broadcastLevel;
    }
}
