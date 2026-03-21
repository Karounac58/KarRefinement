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
 * 锻造 GUI 上下文实现
 */
public class ForgeGuiContext implements GuiContext {

    private final Inventory inventory;
    private final Player player;
    private final List<GuiSlot> customSlots;

    public ForgeGuiContext(Inventory inventory, Player player, List<GuiSlot> customSlots) {
        this.inventory = inventory;
        this.player = player;
        this.customSlots = customSlots;
    }

    @Override
    public GuiType getGuiType() {
        return GuiType.FORGE;
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
        ItemStack item = inventory.getItem(19);
        return Optional.ofNullable(item);
    }

    @Override
    public Optional<ItemStack> getForgeItem2() {
        ItemStack item = inventory.getItem(25);
        return Optional.ofNullable(item);
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
