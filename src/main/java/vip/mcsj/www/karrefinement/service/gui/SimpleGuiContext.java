package vip.mcsj.www.karrefinement.service.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.gui.GuiContext;
import vip.mcsj.www.karrefinement.api.gui.GuiSlot;
import vip.mcsj.www.karrefinement.api.gui.GuiType;

import java.util.List;
import java.util.Optional;

/**
 * 通用 GUI 上下文实现
 * 用于合成石、转星、碎片合成、取物等无特殊便捷方法的 GUI
 */
public class SimpleGuiContext implements GuiContext {

    private final GuiType guiType;
    private final Inventory inventory;
    private final Player player;
    private final List<GuiSlot> customSlots;

    public SimpleGuiContext(GuiType guiType, Inventory inventory, Player player, List<GuiSlot> customSlots) {
        this.guiType = guiType;
        this.inventory = inventory;
        this.player = player;
        this.customSlots = customSlots;
    }

    @Override
    public GuiType getGuiType() {
        return guiType;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public Optional<ItemStack> getStone() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getEquipment() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getForgeItem1() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getForgeItem2() {
        return Optional.empty();
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        for (GuiSlot customSlot : customSlots) {
            if (customSlot.getSlotIndex() == slot) {
                inventory.setItem(slot, item);
                return;
            }
        }
    }

    @Override
    public void refreshCustomSlots() {
        for (GuiSlot slot : customSlots) {
            ItemStack display = slot.buildDisplayItem(player, this);
            if (display != null) {
                inventory.setItem(slot.getSlotIndex(), display);
            }
        }
    }
}
