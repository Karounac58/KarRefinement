package vip.mcsj.www.karrefinement.service.gui;

import org.bukkit.plugin.Plugin;
import vip.mcsj.www.karrefinement.api.gui.*;
import vip.mcsj.www.karrefinement.gui.KarCompoundPieceGui;
import vip.mcsj.www.karrefinement.gui.KarTransformStarGui;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * GUI 槽位注册中心默认实现
 */
public class DefaultGuiSlotRegistry implements GuiSlotRegistry {

    /** GuiType -> (slotId -> SlotEntry) */
    private final Map<GuiType, Map<String, SlotEntry>> registry = new ConcurrentHashMap<>();

    /** GuiType -> 核心保留槽位 */
    private final Map<GuiType, Set<Integer>> reservedSlots = new EnumMap<>(GuiType.class);

    public DefaultGuiSlotRegistry() {
        initReservedSlots();
    }

    private void initReservedSlots() {
        // 淬炼界面核心槽位
        reservedSlots.put(GuiType.REFINEMENT, new HashSet<>(Arrays.asList(
                8,   // 锻造按钮
                20,  // 石头信息
                24,  // 装备信息
                29,  // 淬炼石槽位
                33,  // 装备槽位
                49   // 确认按钮
        )));

        // 锻造界面核心槽位
        reservedSlots.put(GuiType.FORGE, new HashSet<>(Arrays.asList(
                4,   // 信息按钮
                19,  // 装备1槽位
                22,  // 确认按钮
                25   // 装备2槽位
        )));

        // 合成石界面核心槽位
        reservedSlots.put(GuiType.COMPOUND_STONE, new HashSet<>(Arrays.asList(
                16,  // 石头1槽位
                19,  // 结果槽位
                34   // 石头2槽位
        )));
        // 确认按钮位置 37-43
        for (int i = 37; i <= 43; i++) {
            reservedSlots.get(GuiType.COMPOUND_STONE).add(i);
        }

        // 转星界面核心槽位（从配置动态获取）
        reservedSlots.put(GuiType.TRANSFORM, new HashSet<>(Arrays.asList(
                KarTransformStarGui.originSlot,
                KarTransformStarGui.afterSlot
        )));

        // 碎片合成界面核心槽位（从配置动态获取）
        reservedSlots.put(GuiType.COMPOUND_PIECE, new HashSet<>(Arrays.asList(
                KarCompoundPieceGui.originSlot,
                KarCompoundPieceGui.afterSlot
        )));

        // 取物界面 — 全部由分页系统管理，无可用自定义槽位
        reservedSlots.put(GuiType.TAKE_ITEM, new HashSet<>());
    }

    /**
     * 重新初始化动态保留槽位（配置变化后调用）
     */
    public void refreshReservedSlots() {
        reservedSlots.get(GuiType.TRANSFORM).clear();
        reservedSlots.get(GuiType.TRANSFORM).add(KarTransformStarGui.originSlot);
        reservedSlots.get(GuiType.TRANSFORM).add(KarTransformStarGui.afterSlot);

        reservedSlots.get(GuiType.COMPOUND_PIECE).clear();
        reservedSlots.get(GuiType.COMPOUND_PIECE).add(KarCompoundPieceGui.originSlot);
        reservedSlots.get(GuiType.COMPOUND_PIECE).add(KarCompoundPieceGui.afterSlot);
    }

    @Override
    public void registerSlot(Plugin plugin, GuiType guiType, GuiSlot slot) {
        Set<Integer> reserved = getReservedSlots(guiType);
        if (reserved.contains(slot.getSlotIndex())) {
            throw new SlotConflictException(guiType, slot.getSlotIndex(), slot.getId());
        }

        Map<String, SlotEntry> guiSlots = registry.computeIfAbsent(guiType, k -> new ConcurrentHashMap<>());

        // 检查是否有更高优先级的槽位已经在此位置
        for (SlotEntry entry : guiSlots.values()) {
            if (entry.slot.getSlotIndex() == slot.getSlotIndex()) {
                if (entry.slot.getPriority() <= slot.getPriority()) {
                    // 新注册的优先级不够高，跳过
                    return;
                }
                // 新注册的优先级更高，移除旧的
                guiSlots.remove(entry.slot.getId());
                break;
            }
        }

        guiSlots.put(slot.getId(), new SlotEntry(plugin, slot));
    }

    @Override
    public void unregisterAll(Plugin plugin) {
        for (Map<String, SlotEntry> guiSlots : registry.values()) {
            guiSlots.entrySet().removeIf(entry -> entry.getValue().plugin.equals(plugin));
        }
    }

    @Override
    public void unregisterSlot(GuiType guiType, String slotId) {
        Map<String, SlotEntry> guiSlots = registry.get(guiType);
        if (guiSlots != null) {
            guiSlots.remove(slotId);
        }
    }

    @Override
    public List<GuiSlot> getSlots(GuiType guiType) {
        Map<String, SlotEntry> guiSlots = registry.get(guiType);
        if (guiSlots == null || guiSlots.isEmpty()) {
            return Collections.emptyList();
        }
        return guiSlots.values().stream()
                .map(entry -> entry.slot)
                .sorted(Comparator.comparingInt(GuiSlot::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> getReservedSlots(GuiType guiType) {
        return reservedSlots.getOrDefault(guiType, Collections.emptySet());
    }

    private static class SlotEntry {
        final Plugin plugin;
        final GuiSlot slot;

        SlotEntry(Plugin plugin, GuiSlot slot) {
            this.plugin = plugin;
            this.slot = slot;
        }
    }
}
