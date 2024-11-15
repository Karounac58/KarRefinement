package vip.mcsj.www.object;

import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    private int refinementLevel;
    private List<String> mainLore;
    private Map<String, List<String>> extractLores = new HashMap<>();

    private List<PotionEffect> potionEffects = new ArrayList<>(0);
    private int getRefinementLevel() {
        return refinementLevel;
    }

    public void setRefinementLevel(int refinementLevel) {
        this.refinementLevel = refinementLevel;
    }

    public List<String> getMainLore() {
        return mainLore;
    }

    public void setMainLore(List<String> mainLore) {
        this.mainLore = mainLore;
    }

    public Map<String, List<String>> getExtractLores() {
        return extractLores;
    }

    public void setExtractLores(Map<String, List<String>> extractLores) {
        this.extractLores = extractLores;
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }
}
