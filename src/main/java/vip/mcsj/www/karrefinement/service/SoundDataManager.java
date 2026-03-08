package vip.mcsj.www.karrefinement.service;

import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.HashMap;
import java.util.Map;

public class SoundDataManager implements Service {
    public static Map<String, Sound> sounds = new HashMap<>();
    
    @Override
    public void initialize() {
        init();
    }

    public void init(){
        if(!sounds.isEmpty()){
            sounds.clear();
        }

        YamlConfiguration soundYaml = FileUtil.getCustomFileYaml("sound.yml");
        
        if(soundYaml.getConfigurationSection("Sound") == null){
            return;
        }
        
        loadSoundsFromSection(soundYaml, "Sound.KarRefinementGui", "KarRefinementGui");
        loadSoundsFromSection(soundYaml, "Sound.KarForgeGui", "KarForgeGui");
        loadSoundsFromSection(soundYaml, "Sound.KarCompoundPieceGui", "KarCompoundPieceGui");
        loadSoundsFromSection(soundYaml, "Sound.KarCompoundStoneGui", "KarCompoundStoneGui");
    }
    
    private void loadSoundsFromSection(YamlConfiguration yaml, String sectionPath, String prefix){
        if(yaml.getConfigurationSection(sectionPath) == null){
            return;
        }
        
        for(String key : yaml.getConfigurationSection(sectionPath).getKeys(false)){
            String soundName = yaml.getString(sectionPath + "." + key);
            if(soundName != null && !soundName.isEmpty()){
                try{
                    Sound sound = Sound.valueOf(soundName.toUpperCase());
                    sounds.put(prefix + "." + key, sound);
                }catch (IllegalArgumentException e){
                }
            }
        }
    }
    
    public static Sound getSound(String key){
        return sounds.get(key);
    }
    
    public static Sound getSound(String gui, String action){
        return sounds.get(gui + "." + action);
    }
}
