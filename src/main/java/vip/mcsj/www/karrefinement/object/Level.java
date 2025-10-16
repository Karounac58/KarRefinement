package vip.mcsj.www.karrefinement.object;

import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    private int refinementLevel;
    private List<String> mainLore;
    private Map<String, List<String>> extractLores = new HashMap<>();
    public HashMap<String, List<String>> attribute;
    public SuitEffect suitEffect;

    public Level() {
    }

    public Level(int refinementLevel, List<String> mainLore, Map<String, List<String>> extractLores, HashMap<String, List<String>> attribute, SuitEffect suitEffect) {
        this.refinementLevel = refinementLevel;
        this.mainLore = mainLore;
        this.extractLores = extractLores;
        this.attribute = attribute;
        this.suitEffect = suitEffect;
    }


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

    public HashMap<String, List<String>> getAttribute() {
        return attribute;
    }

    public void setAttribute(HashMap<String, List<String>> attribute) {
        this.attribute = attribute;
    }

    public SuitEffect getSuitEffect() {
        return suitEffect;
    }

    public void setSuitEffect(SuitEffect suitEffect) {
        this.suitEffect = suitEffect;
    }
}
