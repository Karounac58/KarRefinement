package vip.mcsj.www.karrefinement.object;

import org.bukkit.Material;

import java.util.List;

public class InvItem {
    private String name;
    private Material material;
    private int slot;
    private List<Integer> slots;
    private int data;
    private int customModelData;
    private List<String> lore;

    public InvItem(String name, Material material, List<Integer> slots, int data, int customModelData, List<String> lore) {
        this.name = name;
        this.material = material;
        this.slots = slots;
        this.data = data;
        this.customModelData = customModelData;
        this.lore = lore;
    }

    public InvItem(String name, Material material, int slot, int data, int customModelData, List<String> lore) {
        this.name = name;
        this.material = material;
        this.slot = slot;
        this.data = data;
        this.customModelData = customModelData;
        this.lore = lore;
    }

    public InvItem(String name, Material material, int data, int customModelData, List<String> lore) {
        this.name = name;
        this.material = material;
        this.data = data;
        this.customModelData = customModelData;
        this.lore = lore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
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
}
