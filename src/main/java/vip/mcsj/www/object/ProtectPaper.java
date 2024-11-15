package vip.mcsj.www.object;

import org.bukkit.Material;

import java.util.List;

public class ProtectPaper {
    //命令用的标识符
    private String identifier;

    private String name;

    private List<String> lore;

    private Material type;
    private int customModelData;

    private int level;



    public ProtectPaper(String identifier, String name, List<String> lore, Material type, int customModelData, int level) {
        this.identifier = identifier;
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.customModelData = customModelData;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ProtectPaper() {
    }

    public ProtectPaper(String identifier, String name, List<String> lore, Material type, int customModelData) {
        this.identifier = identifier;
        this.name = name;
        this.lore = lore;
        this.type = type;
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

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }
}
