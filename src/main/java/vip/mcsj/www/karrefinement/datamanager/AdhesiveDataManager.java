package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.Adhesive;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdhesiveDataManager {
    public static Map<String, Adhesive> adhesives = new HashMap<>();

    public static void init(){
        if(!adhesives.isEmpty()){
            adhesives.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("adhesive.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            String name =  customFileYaml.getString(key+".Name");
            Material type = Material.valueOf(customFileYaml.getString(key + ".Type"));
            int data =  customFileYaml.getInt(key + ".Data");
            int customModelData =  customFileYaml.getInt(key + ".CustomModelData");
            String originGem =   customFileYaml.getString(key + ".OriginGem");
            String afterGem =   customFileYaml.getString(key + ".AfterGem");
            int requiredAmount =  customFileYaml.getInt(key + ".RequiredAmount");
            List<Integer> failedAmount =   customFileYaml.getIntegerList(key + ".FailedAmount");
            int chance =  customFileYaml.getInt(key + ".Chance");
            List<String> lore = customFileYaml.getStringList(key + ".Lore");

            Adhesive adhesive = new Adhesive(name, type, data, customModelData, lore, originGem, afterGem, requiredAmount, failedAmount, chance);
            adhesives.put(key, adhesive);
        }
    }

    public static ItemStack createAdhesiveItem(String identifier){
        Adhesive adhesive = adhesives.get(identifier);
        if(adhesive == null){
            return null;
        }

        ItemStack item = new ItemStack(adhesive.getType(), 1, (short) adhesive.getData());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(adhesive.getName());
        itemMeta.setLore(adhesive.getLore());
        item.setItemMeta(itemMeta);
        ReflectionUtils.setCustomModelData(item,adhesive.getCustomModelData());
        NBT.modify(item, nbt -> {
            nbt.setString("adhesive",identifier);
        });
        return item;
    }

    public static Adhesive getAdehesive(ItemStack item){
        return adhesives.get(new NBTItem(item).getString("adhesive"));
    }
}
