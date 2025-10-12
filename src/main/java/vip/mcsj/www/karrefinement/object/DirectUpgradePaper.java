package vip.mcsj.www.karrefinement.object;

import org.bukkit.Material;

import java.util.List;

public class DirectUpgradePaper {

//    private String identifier;

    private int level;

    private String name;

    private List<String> lore;

    private Material material;

    private int data;

    private int customModelData;

    public DirectUpgradePaper(int level, String name, List<String> lore, Material material,int data, int customModelData) {
        this.level = level;
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.data = data;
        this.customModelData = customModelData;
    }

    public int getData() {
        return data;
    }
//    public String getIdentifier() {
//        return identifier;
//    }
//
//    public void setIdentifier(String identifier) {
//        this.identifier = identifier;
//    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }
}
