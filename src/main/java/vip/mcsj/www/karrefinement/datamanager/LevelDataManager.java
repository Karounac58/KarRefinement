package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

public class LevelDataManager {
    public static List<Level> levels = new ArrayList<>();

    public static void init(){
        if(!levels.isEmpty()){
            levels.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("refinement.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        List<String> collect = keys.stream().map(Integer::parseInt).sorted().map(String::valueOf).collect(Collectors.toList());
        for (String s : collect) {
            Level level = new Level();
            level.setRefinementLevel(Integer.parseInt(s));
            List<String> lore = customFileYaml.getStringList(s+".Lore");
            level.setMainLore(lore);
            Set<String> keys1 = customFileYaml.getConfigurationSection(s + ".Attribute").getKeys(false);
            Map<String,List<String>> extractlores = new HashMap<>();
            for (String key : keys1) {
                List<String> handLore = customFileYaml.getStringList(s + ".Attribute."+key);
                extractlores.put(key,handLore);
            }
            List<String> potionEffectStrings = customFileYaml.getStringList(s+".SuitEffect.PotionEffect");
            if(potionEffectStrings.size() != 0) {
                List<PotionEffect> potionEffects = new ArrayList<>();
                for (String potionEffectString : potionEffectStrings) {
                    String[] potionAndLevel = potionEffectString.split(" ");
                    PotionEffect potion = new PotionEffect(PotionEffectType.getByName(potionAndLevel[0]), 120, Integer.parseInt(potionAndLevel[1]));
                    potionEffects.add(potion);
                }
                level.setPotionEffects(potionEffects);
            }
            level.setExtractLores(extractlores);
            levels.add(level);
        }
    }

    public static Level getMinLevel(Player p){
        int minLevel = EquipmentDataManager.getMinLevelFromEquipments(p);
        if(minLevel == 0){
            return null;
        }
        return levels.get(minLevel-1);
    }
}
