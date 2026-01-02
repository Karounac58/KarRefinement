package vip.mcsj.www.karrefinement.object;

import java.util.List;

public class Furnace {
    private String name;
    private String guiName;
    private int cmd;
    private int minLevel;
    private int maxLevel;
    private double success;
    private List<String> lore;

    public Furnace(String name, String guiName, int cmd, int minLevel, int maxLevel, double success, List<String> lore) {
        this.name = name;
        this.guiName = guiName;
        this.cmd = cmd;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.success = success;
        this.lore = lore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuiName() {
        return guiName;
    }

    public void setGuiName(String guiName) {
        this.guiName = guiName;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getSuccess() {
        return success;
    }

    public void setSuccess(double success) {
        this.success = success;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
