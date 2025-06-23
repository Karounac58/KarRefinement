package vip.mcsj.www.object;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class Stone {
    //命令用的标识符
    private String identifier;

    private String name;

    private List<String> lore;
    //概率
    private List<Double> probability;

    private Material type;

    private int data;

    private int[] downLevels;

    private int customModelData;

    /**
     *
     * @param identifier    用于命令获取的标识符
     * @param name          石头名字
     * @param lore          石头Lore
     * @param probability   石头对每级淬炼成功的概率
     * @param type          石头所用的物品类型
     * @param dropLevels    石头掉级数组
     * @param customModelData   顾名思义
     */
    public Stone(String identifier, String name, List<String> lore, List<Double> probability, Material type, int data, int[] dropLevels, int customModelData) {
        this.identifier = identifier;
        this.name = name;
        this.lore = lore;
        this.probability = probability;
        this.type = type;
        this.data = data;
        this.downLevels = dropLevels;
        this.customModelData = customModelData;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<Double> getProbability() {
        return probability;
    }

    public void setProbability(List<Double> probability) {
        this.probability = probability;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int[] getDownLevels() {
        return downLevels;
    }

    public void setDownLevels(int[] downLevels) {
        this.downLevels = downLevels;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Stone{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", probability=" + probability +
                ", type=" + type +
                ", data=" + data +
                ", downLevels=" + Arrays.toString(downLevels) +
                ", customModelData=" + customModelData +
                '}';
    }
}
