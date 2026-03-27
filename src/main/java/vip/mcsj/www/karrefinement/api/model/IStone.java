package vip.mcsj.www.karrefinement.api.model;

import org.bukkit.Material;

import java.util.List;

public interface IStone {
    String getIdentifier();
    String getName();
    List<String> getLore();
    List<Double> getProbability();
    Material getType();
    int getData();
    int[] getDownLevels();
    int getCustomModelData();
    String getNbt();

}
