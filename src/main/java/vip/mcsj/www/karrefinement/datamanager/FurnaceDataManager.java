package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.Furnace;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnaceDataManager {
    public static Map<String, Furnace> furnaces = new HashMap<>();
    public static boolean furnaceEnabled = true;

    public static void init(){
        if(!furnaces.isEmpty()){
            furnaces.clear();
        }
        YamlConfiguration yaml = FileUtil.getCustomFileYaml("furnace.yml");
        furnaceEnabled = yaml.getBoolean("enabled");
        
        for (String key : yaml.getKeys(false)) {
            if(key.equals("enabled")){
                continue;
            }
            String name = yaml.getString(key+".Name");
            String guiName = yaml.getString(key + ".GuiName");
            int cmd = yaml.getInt(key + ".CustomModelData");
            int minLevel = yaml.getInt(key + ".MinLevel");
            int maxLevel = yaml.getInt(key + ".MaxLevel");
            double success = yaml.getDouble(key + ".Success");
            List<String> lore = yaml.getStringList(key + ".Lore");

            furnaces.put(key, new Furnace(name, guiName, cmd, minLevel, maxLevel, success, lore));
        }
    }

    public static ItemStack createFurnace(String key){
        Furnace f = furnaces.get(key);
        if(f == null){
            return null;
        }
        ItemStack furnace = new ItemStack(Material.FURNACE);
        ItemMeta meta = furnace.getItemMeta();
        meta.setDisplayName(f.getName());
        meta.setLore(f.getLore());
        furnace.setItemMeta(meta);
        ReflectionUtils.setCustomModelData(furnace,f.getCmd());
        NBT.modify(furnace, nbt -> {
            nbt.setString("karfurnace",key);
        });

        return furnace;
    }


}
