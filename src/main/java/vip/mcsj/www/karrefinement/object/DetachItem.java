package vip.mcsj.www.karrefinement.object;

import org.bukkit.Material;

import java.util.List;

public class DetachItem {
    private String name;
    private Material type;
    private int data;
    private int cmd;
    private int level;
    private List<String> lore;

    public DetachItem(String name, Material type, int data, int cmd, int level, List<String> lore) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.cmd = cmd;
        this.level = level;
        this.lore = lore;
    }

    public DetachItem(String name, Material type, int data, int cmd, List<String> lore) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.cmd = cmd;
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

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public String toString() {
        return "DetachItem{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", data=" + data +
                ", cmd=" + cmd +
                ", level=" + level +
                ", lore=" + lore +
                '}';
    }
}
