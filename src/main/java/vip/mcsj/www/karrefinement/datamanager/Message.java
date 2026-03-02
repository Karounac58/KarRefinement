package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import vip.mcsj.www.karrefinement.core.Service;

import java.util.*;

public class Message implements Service {
    public static Map<String,String> messages = new HashMap<>();

    public static List<String> showStoneChance = new ArrayList<>();

    @Override
    public void initialize() {
        init();
    }

    public static void init(){
        if(!messages.isEmpty()) {
            messages.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("message.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            if(key.equals("stone_interact_show_chance")){
                showStoneChance = customFileYaml.getStringList(key);
                continue;
            }
            messages.put(key, ChatColor.translateAlternateColorCodes('&',customFileYaml.getString(key)));
        }
    }
}
