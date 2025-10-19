package vip.mcsj.www.karrefinement.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class FileUtil {

    public static YamlConfiguration getCustomFileYaml(String fileName){
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
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
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
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
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        dataConfig.set(key,value);
        dataConfig.save(dataFile);
    }

    /**
     * 配置文件初始化方法
     * @param fileName  要初始化的配置文件名
     */
    public static void FileInitialize(String fileName){
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
        if(dataFile.exists()){
        }else{
            try{
                FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
                KarRefinement.instance.saveResource(fileName,false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setDataFileNull() throws Exception{
        File dataFile = new File(KarRefinement.instance.getDataFolder(),"data.yml");
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
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
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
        File dataFile = new File(KarRefinement.instance.getDataFolder(),fileName);
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
        KarRefinement.instance.saveResource(fileName,false);
    }

    public static void initCustomFile(String inPath,String fileName){
        saveResource(inPath,fileName,false);
    }
    /**
     * 从指定文件中获取ConfigurationSection
     * @param fileName
     * @param key
     * @return
     */
    public static ConfigurationSection getCSFromDataFile(String fileName,String key){
        File file = new File(KarRefinement.instance.getDataFolder(),fileName);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getConfigurationSection(key);
    }

    public static byte[] readBytes(File file) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];

            int n;
            while((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            fis.close();
            return bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, (String)null, ex);

            return new byte[0];
        }
    }

    public static String read(File file) {
        StringBuffer buf = null;
        BufferedReader breader = null;

        try {
            breader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            buf = new StringBuffer();

            while(breader.ready()) {
                buf.append((char)breader.read());
            }

            breader.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
        }

        return buf.toString();
    }

    public static void write(File file, String str) {
        try {
            OutputStream out = new FileOutputStream(file);

            try {
                out.write(str.getBytes());
            } catch (Throwable var6) {
                try {
                    out.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            out.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
        }

    }

    public static void copyFolder(File resourceFile, File targetFile) throws Exception {
        File[] resourceFiles = resourceFile.listFiles();

        for(File file : resourceFiles) {
            File file1 = new File(targetFile.getAbsolutePath() + File.separator + resourceFile.getName());
            if (file.isFile()) {
                if (!file1.exists()) {
                    file1.mkdirs();
                }

                File targetFile1 = new File(file1.getAbsolutePath() + File.separator + file.getName());
                copyFile(file, targetFile1);
            }

            if (file.isDirectory()) {
                copyFolder(file, file1);
            }
        }

    }

    public static void copyFile(File resource, File target) throws Exception {
        FileInputStream inputStream = new FileInputStream(resource);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(target);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            try {
                byte[] bytes = new byte[2048];
                int len = 0;

                while((len = inputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, len);
                }

                bufferedOutputStream.flush();
            } catch (Throwable var9) {
                try {
                    bufferedOutputStream.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            bufferedOutputStream.close();
        } catch (Throwable var10) {
            try {
                inputStream.close();
            } catch (Throwable var7) {
                var10.addSuppressed(var7);
            }

            throw var10;
        }

        inputStream.close();
        outputStream.close();
    }

    public static void saveResource(String inPath,String outPath, boolean replace) {
        if (inPath != null && !inPath.equals("")) {
            inPath = inPath.replace('\\', '/');
            InputStream in = KarRefinement.instance.getResource(inPath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + inPath + "' cannot be found in datafolder!");
            } else {
                File outFile = new File(KarRefinement.instance.getDataFolder(), outPath);
                int lastIndex = outPath.lastIndexOf(47);
                File outDir = new File(KarRefinement.instance.getDataFolder(), outPath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        KarRefinement.instance.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException ex) {
                    KarRefinement.instance.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }
}
