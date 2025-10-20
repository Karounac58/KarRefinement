package vip.mcsj.www.karrefinement.object;

import org.bukkit.Material;

import java.util.List;

public class RPotion {
    String name;
    Material type;
    int data;
    int customModelData;
    double success;
    int duration;
    List<String> lore;

    public RPotion(String name, Material type, int data, int customModelData, double success, int duration, List<String> lore) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.customModelData = customModelData;
        this.success = success;
        this.duration = duration;
        this.lore = lore;
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

    public double getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
