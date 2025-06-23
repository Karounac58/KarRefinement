package vip.mcsj.www.object;

import org.bukkit.Material;

import java.util.List;

public class SpeStone {
    private String identifier;

    private String name;

    private List<String> lore;

    private int level;

    private String nbtKey;

    private List<String> canUseItems;

    private Material type;

    private int data;

    private int customModelData;

    private List<String> equipmentLore;
    public SpeStone() {
    }

    public SpeStone(String identifier, String name, List<String> lore, int level, String nbtKey, List<String> canUseItems, Material type,int data,int customModelData, List<String> equipmentLore) {
        this.identifier = identifier;
        this.name = name;
        this.lore = lore;
        this.level = level;
        this.nbtKey = nbtKey;
        this.canUseItems = canUseItems;
        this.type = type;
        this.data = data;
        this.customModelData = customModelData;
        this.equipmentLore = equipmentLore;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getCanUseItems() {
        return canUseItems;
    }

    public void setCanUseItems(List<String> canUseItems) {
        this.canUseItems = canUseItems;
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

    public String getNbtKey() {
        return nbtKey;
    }

    public void setNbtKey(String nbtKey) {
        this.nbtKey = nbtKey;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public List<String> getEquipmentLore() {
        return equipmentLore;
    }

    public void setEquipmentLore(List<String> equipmentLore) {
        this.equipmentLore = equipmentLore;
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

    @Override
    public String toString() {
        return "SpeStone{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", level=" + level +
                ", nbtKey='" + nbtKey + '\'' +
                ", canUseItems=" + canUseItems +
                ", type=" + type +
                ", data=" + data +
                ", customModelData=" + customModelData +
                ", equipmentLore=" + equipmentLore +
                '}';
    }
}
