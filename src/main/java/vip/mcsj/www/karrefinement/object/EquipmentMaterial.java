package vip.mcsj.www.karrefinement.object;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.model.IEquipmentMaterial;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

public class EquipmentMaterial implements IEquipmentMaterial {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final String rawType;      // 原始配置值
    private Material material;          // 解析后的Material
    private short data = 0;             // 子ID（1.12用）
    private ItemStack cachedItem;       // 缓存的ItemStack（模组物品用）
    private boolean isModItem = false;  // 是否为模组物品

    public EquipmentMaterial(String rawType) {
        this.rawType = rawType;
        parse();
    }

    /**
     * 解析材料字符串
     */
    private void parse() {
        if (rawType == null || rawType.isEmpty()) {
            log.warning("[KarRefinement] 材料配置为空");
            material = Material.STONE;
            return;
        }

        // 不含冒号：纯原版材料名 (DIAMOND_SWORD)
        if (!rawType.contains(":")) {
            material = matchMaterial(rawType);
            if (material == null) {
                log.warning("[KarRefinement] 无法识别材料: " + rawType);
                material = Material.STONE;
            }
            return;
        }

        String[] parts = rawType.split(":");

        // 两部分：可能是 MATERIAL:DATA 或 modid:itemname
        if (parts.length == 2) {
            // 先尝试原版格式
            Material mat = matchMaterial(parts[0]);
            if (mat != null) {
                try {
                    data = Short.parseShort(parts[1]);
                    material = mat;
                    return;
                } catch (NumberFormatException ignored) {
                    // 第二部分不是数字，当作模组ID处理
                }
            }
        }

        // 作为模组物品处理
        parseModItem();
    }

    /**
     * 解析模组物品
     */
    private void parseModItem() {
        isModItem = true;

        // 处理 modid:item:meta 格式
        String modId = rawType;
        int meta = 0;

        String[] parts = rawType.split(":");
        if (parts.length == 3) {
            try {
                meta = Integer.parseInt(parts[2]);
                modId = parts[0] + ":" + parts[1];
            } catch (NumberFormatException ignored) {}
        }

        // 方式1: 直接matchMaterial
        material = matchMaterial(modId);
        if (material != null) {
            data = (short) meta;
            return;
        }

        // 方式2: 转换格式后matchMaterial
        material = matchMaterial(modId.replace(":", "_").toUpperCase());
        if (material != null) {
            data = (short) meta;
            return;
        }

        // 方式3: 遍历Material查找
        material = findMaterialByKey(modId);
        if (material != null) {
            data = (short) meta;
            return;
        }

        // 方式4: 通过ItemStack方式获取
        cachedItem = createModItemStack(modId, meta);
        if (cachedItem != null) {
            material = cachedItem.getType();
            return;
        }

        log.warning("[KarRefinement] 无法识别模组材料: " + rawType);
        material = Material.STONE;
        isModItem = false;
    }

    /**
     * 兼容多版本的Material匹配
     */
    private Material matchMaterial(String name) {
        if (name == null) return null;

        // 直接匹配
        Material mat = Material.matchMaterial(name);
        if (mat != null) return mat;

        // 1.13+ 旧名称兼容
        try {
            java.lang.reflect.Method method = Material.class.getMethod(
                    "matchMaterial", String.class, boolean.class);
            mat = (Material) method.invoke(null, name, true);
            if (mat != null) return mat;
        } catch (Exception ignored) {}

        // 处理1.13+材料名变更 (GOLD -> GOLDEN, WOOD -> WOODEN等)
        String converted = convertLegacyName(name);
        if (!converted.equals(name)) {
            mat = Material.matchMaterial(converted);
            if (mat != null) return mat;
        }

        return null;
    }

    /**
     * 转换旧版材料名到新版
     */
    private String convertLegacyName(String name) {
        return name
                .replace("GOLD_", "GOLDEN_")
                .replace("WOOD_", "WOODEN_")
                .replace("_SPADE", "_SHOVEL");
    }

    /**
     * 通过Key查找Material（1.13+）
     */
    private Material findMaterialByKey(String modId) {
        String searchKey = modId.toLowerCase();
        for (Material m : Material.values()) {
            try {
                java.lang.reflect.Method getKeyMethod = Material.class.getMethod("getKey");
                Object key = getKeyMethod.invoke(m);
                if (key.toString().equalsIgnoreCase(searchKey)) {
                    return m;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * 创建模组物品ItemStack
     */
    @SuppressWarnings("deprecation")
    private ItemStack createModItemStack(String modId, int meta) {
        // 方式1: Bukkit.getUnsafe()
        try {
            ItemStack temp = new ItemStack(Material.STONE);
            String nbt = "{id:\"" + modId + "\",Count:1b,Damage:" + meta + "s}";
            ItemStack result = Bukkit.getUnsafe().modifyItemStack(temp, nbt);
            if (result != null && result.getType() != Material.STONE) {
                return result;
            }
        } catch (Exception ignored) {}

        // 方式2: Forge反射
        try {
            return createByForge(modId, meta);
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * 通过Forge反射创建ItemStack
     */
    private ItemStack createByForge(String modId, int meta) throws Exception {
        String[] idParts = modId.split(":", 2);
        String namespace = idParts[0];
        String path = idParts.length > 1 ? idParts[1] : idParts[0];

        Object forgeItem = null;

        // 1.12.2 Forge
        try {
            Class<?> itemClass = Class.forName("net.minecraft.item.Item");
            Class<?> rlClass = Class.forName("net.minecraft.util.ResourceLocation");
            Constructor<?> rlCon = rlClass.getConstructor(String.class, String.class);
            Object rl = rlCon.newInstance(namespace, path);

            Object registry = itemClass.getField("REGISTRY").get(null);
            forgeItem = registry.getClass().getMethod("getObject", rlClass).invoke(registry, rl);
        } catch (Exception ignored) {}

        // 1.13+ Forge
        if (forgeItem == null) {
            try {
                Class<?> forgeReg = Class.forName("net.minecraftforge.registries.ForgeRegistries");
                Object itemsReg = forgeReg.getField("ITEMS").get(null);
                Class<?> rlClass = Class.forName("net.minecraft.util.ResourceLocation");
                Constructor<?> rlCon = rlClass.getConstructor(String.class, String.class);
                Object rl = rlCon.newInstance(namespace, path);

                forgeItem = itemsReg.getClass().getMethod("getValue", rlClass).invoke(itemsReg, rl);
            } catch (Exception ignored) {}
        }

        if (forgeItem == null) return null;

        // 创建NMS ItemStack
        Class<?> nmsItemStack = Class.forName("net.minecraft.item.ItemStack");
        Class<?> itemClass = Class.forName("net.minecraft.item.Item");
        Object nmsStack;

        try {
            Constructor<?> con = nmsItemStack.getConstructor(itemClass, int.class, int.class);
            nmsStack = con.newInstance(forgeItem, 1, meta);
        } catch (Exception e) {
            Constructor<?> con = nmsItemStack.getConstructor(itemClass);
            nmsStack = con.newInstance(forgeItem);
        }

        // 转Bukkit
        String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
        Class<?> craftItemStack = Class.forName(cbPackage + ".inventory.CraftItemStack");
        return (ItemStack) craftItemStack.getMethod("asBukkitCopy", nmsItemStack).invoke(null, nmsStack);
    }

    // ==================== Getter方法 ====================

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    @Override
    public ItemStack getCachedItem() {
        return cachedItem;
    }

    public String getRawType() {
        return rawType;
    }

    public boolean isModItem() {
        return isModItem;
    }

    @SuppressWarnings("deprecation")
    public ItemStack toItemStack() {
        if (cachedItem != null) {
            return cachedItem.clone();
        }
        ItemStack item = new ItemStack(material);
        if (data != 0) {
            item.setDurability(data);
        }
        return item;
    }

    /**
     * 检查给定的ItemStack是否匹配此材料
     */
    @SuppressWarnings("deprecation")
    public boolean matches(ItemStack item) {
        if (item == null) return false;

        // 检查Material
        if (item.getType() != material) {
            // 尝试通过Key比较（模组物品）
            if (isModItem) {
                try {
                    java.lang.reflect.Method getKeyMethod = Material.class.getMethod("getKey");
                    String itemKey = getKeyMethod.invoke(item.getType()).toString();
                    String targetKey = rawType.split(":").length >= 2
                            ? rawType.split(":")[0] + ":" + rawType.split(":")[1]
                            : rawType;
                    if (!itemKey.equalsIgnoreCase(targetKey)) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        // 检查子ID（1.12）
        if (data != 0 && item.getDurability() != data) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "EquipmentMaterial{" +
                "rawType='" + rawType + '\'' +
                ", material=" + material +
                ", data=" + data +
                ", isModItem=" + isModItem +
                '}';
    }
}