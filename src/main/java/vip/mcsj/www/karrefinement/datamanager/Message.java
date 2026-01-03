package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Message {
    public static Map<String,String> messages = new HashMap<>();

    public static void init(){
        if(!messages.isEmpty()) {
            messages.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("message.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&',customFileYaml.getString(key)));
        }
    }
}
