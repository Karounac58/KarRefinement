package vip.mcsj.www.karrefinement.api.gui;

/**
 * 槽位冲突异常
 * 当自定义槽位尝试占据核心功能槽位时抛出
 */
public class SlotConflictException extends RuntimeException {

    private final GuiType guiType;
    private final int slotIndex;
    private final String slotId;

    public SlotConflictException(GuiType guiType, int slotIndex, String slotId) {
        super("Slot " + slotIndex + " in " + guiType.name() + " is reserved and cannot be used by custom slot '" + slotId + "'");
        this.guiType = guiType;
        this.slotIndex = slotIndex;
        this.slotId = slotId;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public String getSlotId() {
        return slotId;
    }
}
