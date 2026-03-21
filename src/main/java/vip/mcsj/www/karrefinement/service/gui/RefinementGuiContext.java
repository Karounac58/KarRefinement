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
 * 淬炼 GUI 上下文实现
 */
public class RefinementGuiContext implements GuiContext {

    private final Inventory inventory;
    private final Player player;
    private final List<GuiSlot> customSlots;

    public RefinementGuiContext(Inventory inventory, Player player, List<GuiSlot> customSlots) {
        this.inventory = inventory;
        this.player = player;
        this.customSlots = customSlots;
    }

    @Override
    public GuiType getGuiType() {
        return GuiType.REFINEMENT;
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
        ItemStack item = inventory.getItem(29);
        return Optional.ofNullable(item);
    }

    @Override
    public Optional<ItemStack> getEquipment() {
        ItemStack item = inventory.getItem(33);
        return Optional.ofNullable(item);
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
        // 只允许设置自定义槽位
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
