package vip.mcsj.www.karrefinement.api.model;

import java.util.List;
import java.util.Map;

public interface ILevel {
    int getRefinementLevel();

    List<String> getMainLore();

    Map<String,List<String>> getExtractLores();

    Map<String,List<String>> getAttribute();

    ISuitEffect getSuitEffect();
}
