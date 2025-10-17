package vip.mcsj.www.karrefinement.object;

import org.bukkit.Material;

import java.util.List;

public class Adhesive {
    private String name;
    private Material type;
    private int data;
    private int customModelData;
    private List<String> lore;
    private String originGem;
    private String afterGem;
    private int requiredAmount;
    private List<Integer> failedAmount;
    private int chance;

    public Adhesive(String name, Material type, int data, int customModelData, List<String> lore, String originGem, String afterGem, int requiredAmount, List<Integer> failedAmount, int chance) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.customModelData = customModelData;
        this.lore = lore;
        this.originGem = originGem;
        this.afterGem = afterGem;
        this.requiredAmount = requiredAmount;
        this.failedAmount = failedAmount;
        this.chance = chance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public String getOriginGem() {
        return originGem;
    }

    public void setOriginGem(String originGem) {
        this.originGem = originGem;
    }

    public String getAfterGem() {
        return afterGem;
    }

    public void setAfterGem(String afterGem) {
        this.afterGem = afterGem;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public List<Integer> getFailedAmount() {
        return failedAmount;
    }

    public void setFailedAmount(List<Integer> failedAmount) {
        this.failedAmount = failedAmount;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "Adhesive{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", data=" + data +
                ", customModelData=" + customModelData +
                ", lore=" + lore +
                ", originGem='" + originGem + '\'' +
                ", afterGem='" + afterGem + '\'' +
                ", requiredAmount=" + requiredAmount +
                ", failedAmount=" + failedAmount +
                ", chance=" + chance +
                '}';
    }
}
