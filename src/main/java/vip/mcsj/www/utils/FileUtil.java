package vip.mcsj.www.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.main.KarRefinement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static vip.mcsj.www.main.KarRefinement.instance;


public class FileUtil {

    public static YamlConfiguration getCustomFileYaml(String fileName){
        File dataFile = new File(instance.getDataFolder(),fileName);
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        return dataConfig;
    }
    /**
     *
     * @param key     用于获取值的键
     * @param fileName  插件文件夹下的存储数据的配置文件名
     * @return  根据键获取到的值
     */
    public static String getStringFromDataFile(String key,String fileName){
        File dataFile = new File(instance.getDataFolder(),fileName);
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        String msg = dataConfig.getString(key);
        return msg;
    }

    /**
     *
     * @param key       要存储的键值
     * @param value     要存储的值
     * @param fileName  插件文件夹下的存储数据的配置文件名
     * @throws Exception
     */
    public static void setStringToDataFile(String key,String value,String fileName) throws Exception{
        File dataFile = new File(instance.getDataFolder(),fileName);
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        dataConfig.set(key,value);
        dataConfig.save(dataFile);
    }

    /**
     * 配置文件初始化方法
     * @param fileName  要初始化的配置文件名
     */
    public static void FileInitialize(String fileName){
        File dataFile = new File(instance.getDataFolder(),fileName);
        if(dataFile.exists()){
            return;
        }else{
            try{
                FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
                dataConfig.save(dataFile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setDataFileNull() throws Exception{
        File dataFile = new File(instance.getDataFolder(),"data.yml");
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        Set<String> keys = dataConfig.getKeys(false);
        for (String key : keys) {
            dataConfig.set(key,null);
        }
        dataConfig.save(dataFile);
    }

    /**
     * 判断玩家是否已有配置文件
     * @param
     * @return  有则返回true
     */
    public static boolean isDataFileExists(String fileName){
        File dataFile = new File(instance.getDataFolder(),fileName);
        if(dataFile.exists()){
            return true;
        }
        return false;
    }

    /**
     * 返回玩家配置文件所有键
     * @param fileName
     * @return
     */
    public static Set<String> getDataFileKeys(String fileName,boolean isAll){
        File dataFile = new File(instance.getDataFolder(),fileName);
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        return dataConfig.getKeys(isAll);
    }


    /**
     *  判断配置文件中是否有指定的ItemStack存在
     * @param playerName  要判断的playerName
     * @param item  要判断的item
     * @return  返回-1则没有，其他数字则代表位置
     */
    public static int isDataFileContainsSpecificItem(ItemStack item, String playerName){
        String fileName = "/data/"+playerName+".yml";
        //i用于遍历
        int i = 0;
        //k用于返回
        int k = -1;
        while(FileUtil.getStringFromDataFile(playerName+"."+i+".item",fileName) != null){
            String itemString = FileUtil.getStringFromDataFile(playerName+"."+i+".item",fileName);
            if(itemString.equals(ItemStackSerializeUtil.itemStackSerialize(item))){
                k = i;
                return k;
            }
            i++;
        }
        return k;
    }

    /**
     * 获取配置文件中最后一个物品的序号，用于添加物品或修改物品数量
     * @param keys  对应玩家的配置文件中的所有键
     * @return  配置文件中最后一个物品的序号
     */
    public static int getDataFileMaxItemNum(Set<String> keys){
        List<Integer> allKeysNums = keys.stream()
                .filter(key -> key.split("\\.").length > 2)
                .map(i -> Integer.parseInt(i.split("\\.")[1]))
                .collect(Collectors.toList());
        return Collections.max(allKeysNums);
    }

    public static void initCustomFile(String fileName){
        instance.saveResource(fileName,false);
    }

    /**
     * 从指定文件中获取ConfigurationSection
     * @param fileName
     * @param key
     * @return
     */
    public static ConfigurationSection getCSFromDataFile(String fileName,String key){
        File file = new File(instance.getDataFolder(),fileName);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getConfigurationSection(key);
    }
}
