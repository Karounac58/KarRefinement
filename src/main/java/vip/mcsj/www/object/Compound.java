package vip.mcsj.www.object;

public class Compound {
    private int id;
    private String stoneKey;
    private String higherStoneKey;
    private double chance;

    public Compound(int id, String stoneKey, String higherStoneKey, double chance) {
        this.id = id;
        this.stoneKey = stoneKey;
        this.higherStoneKey = higherStoneKey;
        this.chance = chance;
    }

    public Compound() {
    }

    public int getId() {
        return id;
    }

    public String getStoneKey() {
        return stoneKey;
    }

    public void setStoneKey(String stoneKey) {
        this.stoneKey = stoneKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHigherStoneKey() {
        return higherStoneKey;
    }

    public void setHigherStoneKey(String higherStoneKey) {
        this.higherStoneKey = higherStoneKey;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "Compound{" +
                "id=" + id +
                ", stoneKey='" + stoneKey + '\'' +
                ", higherStoneKey='" + higherStoneKey + '\'' +
                ", chance=" + chance +
                '}';
    }
}
