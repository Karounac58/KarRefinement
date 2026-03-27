package vip.mcsj.www.karrefinement.object;

import vip.mcsj.www.karrefinement.api.model.ILevel;
import vip.mcsj.www.karrefinement.api.model.ISuitEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level implements ILevel {
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


    @Override
    public int getRefinementLevel() {
        return refinementLevel;
    }

    public void setRefinementLevel(int refinementLevel) {
        this.refinementLevel = refinementLevel;
    }

    @Override
    public List<String> getMainLore() {
        return mainLore;
    }

    public void setMainLore(List<String> mainLore) {
        this.mainLore = mainLore;
    }

    @Override
    public Map<String, List<String>> getExtractLores() {
        return extractLores;
    }

    public void setExtractLores(Map<String, List<String>> extractLores) {
        this.extractLores = extractLores;
    }

    @Override
    public Map<String, List<String>> getAttribute() {
        return attribute;
    }

    public void setAttribute(HashMap<String, List<String>> attribute) {
        this.attribute = attribute;
    }
    @Override
    public ISuitEffect getSuitEffect() {
        return suitEffect;
    }

    public void setSuitEffect(SuitEffect suitEffect) {
        this.suitEffect = suitEffect;
    }
}
