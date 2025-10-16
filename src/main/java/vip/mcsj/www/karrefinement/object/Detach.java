package vip.mcsj.www.karrefinement.object;

import java.util.List;

public class Detach {
    private String identifier;
    private Double chance;
    private List<Integer> numbers;
    private int compound;
    private DetachItem item;

    public Detach(String identifier, Double chance, List<Integer> numbers, int compound, DetachItem item) {
        this.identifier = identifier;
        this.chance = chance;
        this.numbers = numbers;
        this.compound = compound;
        this.item = item;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Double getChance() {
        return chance;
    }

    public void setChance(Double chance) {
        this.chance = chance;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public int getCompound() {
        return compound;
    }

    public void setCompound(int compound) {
        this.compound = compound;
    }

    public DetachItem getItem() {
        return item;
    }

    public void setItem(DetachItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Detach{" +
                "identifier='" + identifier + '\'' +
                ", chance=" + chance +
                ", numbers=" + numbers +
                ", compound=" + compound +
                ", item=" + item +
                '}';
    }
}
