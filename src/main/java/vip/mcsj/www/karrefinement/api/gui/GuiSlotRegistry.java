package vip.mcsj.www.karrefinement.api.gui;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

/**
 * GUI 槽位注册中心
 * 外部插件通过此接口注册自定义槽位
 */
public interface GuiSlotRegistry {

    /**
     * 注册一个自定义槽位到指定 GUI
     *
     * @param plugin  注册此槽位的插件（用于生命周期管理）
     * @param guiType 目标 GUI 类型
     * @param slot    槽位实现
     * @throws SlotConflictException 如果槽位索引与核心功能槽位冲突
     */
    void registerSlot(Plugin plugin, GuiType guiType, GuiSlot slot);

    /**
     * 注销某个插件注册的所有槽位
     */
    void unregisterAll(Plugin plugin);

    /**
     * 注销指定 GUI 中的指定槽位
     */
    void unregisterSlot(GuiType guiType, String slotId);

    /**
     * 获取指定 GUI 中所有已注册的自定义槽位
     */
    List<GuiSlot> getSlots(GuiType guiType);

    /**
     * 获取指定 GUI 中不可被自定义槽位占据的核心槽位索引
     */
    Set<Integer> getReservedSlots(GuiType guiType);
}
