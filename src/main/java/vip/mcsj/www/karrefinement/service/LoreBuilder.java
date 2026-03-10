package vip.mcsj.www.karrefinement.service;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.object.Level;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.RandomLoreUtils;

import java.util.*;

/**
 * Lore 构建/移除逻辑
 * 从 EquipmentDataManager 提取，合并 addItemRefinementInfo (2个重载)
 * 和 removeNowItemRefinementInfo (4个重载) 中 80%+ 的重复代码
 */
public class LoreBuilder {

    private final ItemStack equipmentItem;

    public LoreBuilder(ItemStack equipmentItem) {
        this.equipmentItem = equipmentItem;
    }

    // ==================== Add Refinement Info ====================

    /**
     * 给装备添加淬炼信息 Lore（基本版本 — 从物品读取已有信息）
     */
    public void addRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementLevel) {
        addRefinementInfo(mainLore, extractLore, refinementLevel, null, 0, null, 0);
    }

    /**
     * 给装备添加淬炼信息 Lore（完整版本 — 移星等场景提供额外数据）
     *
     * @param mainLore        主淬炼 Lore
     * @param extractLore     附加 Lore（按装备类型）
     * @param refinementLevel 目标淬炼等级
     * @param existingInfoMap 已有的装备信息 Map（null 则从物品读取）
     * @param paperLevel      保护符等级（0 表示不写入 NBT）
     * @param speStones       宝石列表（null 表示不写入 NBT，使用已有信息）
     * @param soulLevel       精魂等级（0 表示不写入 NBT）
     */
    public boolean addRefinementInfo(List<String> mainLore, List<String> extractLore, int refinementLevel,
                                  Map<String, List<String>> existingInfoMap,
                                  int paperLevel, List<SpeStone> speStones, int soulLevel) {

        ItemMeta im = equipmentItem.getItemMeta();



        if (EquipmentDataManager.enableDisplayNameInfo) {
            // 更新显示名称
            String displayName = im.hasDisplayName() ? im.getDisplayName() : null;
            if (displayName == null || displayName.trim().isEmpty()) {
                // 如果物品没有显示名称，尝试从chinesenames获取
                displayName = EquipmentDataManager.chinesenames.get(equipmentItem.getType().name());
                // 如果chinesenames中也没有对应名称，则使用物品类型名
                if (displayName == null || displayName.trim().isEmpty()) {
                    displayName = equipmentItem.getType().name().toLowerCase().replace('_', ' ');
                    displayName = Character.toUpperCase(displayName.charAt(0)) + displayName.substring(1);
                }
            }
            displayName += EquipmentDataManager.displayNameSuffix.replace("{level}", refinementLevel + "");
            im.setDisplayName(displayName);
        }
        


        List<String> lores = new ArrayList<>();
        List<String> metaLore = im.getLore();
        if (metaLore == null) {
            metaLore = new ArrayList<>();
        }

        // 处理随机 Lore
        boolean isRandomLore = false;
        if (extractLore != null && RandomLoreUtils.isRandomLore(extractLore)) {
            extractLore = RandomLoreUtils.replaceWithRandom(extractLore);
            isRandomLore = true;
        }

        // 获取装备已有的信息 Lore
        Map<String, List<String>> equipmentInfoLore;
        if (existingInfoMap != null) {
            equipmentInfoLore = existingInfoMap;
        } else {
            equipmentInfoLore = getEquipmentInfoLore(this.equipmentItem);
        }

        // 移除旧的信息 Lore
        for (String key : equipmentInfoLore.keySet()) {
            List<String> strList = equipmentInfoLore.get(key);
            metaLore.removeAll(strList);
        }

        // 按 sortOrder 重新排列
        boolean useSpeStoneParam = (speStones != null);
        for (String s : EquipmentDataManager.sortOrder) {
            if (s.equals("{lore}")) {
                lores.addAll(metaLore);
            }
            if (s.equals("{refinement}")) {
                lores.add(EquipmentDataManager.mainLore);
                lores.addAll(mainLore);
                lores.addAll(extractLore);
            }
            if (s.equals("{spestone}")) {
                if (useSpeStoneParam) {
                    // 移星模式：使用传入的 speStones 列表
                    if (!speStones.isEmpty()) {
                        lores.add(EquipmentDataManager.speStoneLore);
                        for (SpeStone speStone : speStones) {
                            lores.addAll(speStone.getEquipmentLore());
                        }
                    }
                } else {
                    if (equipmentInfoLore.containsKey("spestone")) {
                        lores.addAll(equipmentInfoLore.get("spestone"));
                    }
                }
            }
            if (s.equals("{paper}")) {
                if (equipmentInfoLore.containsKey("paper")) {
                    lores.addAll(equipmentInfoLore.get("paper"));
                }
            }
            if (s.equals("{soul}")) {
                if (equipmentInfoLore.containsKey("soul")) {
                    lores.addAll(equipmentInfoLore.get("soul"));
                }
            }
        }

        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);

        // 写入淬炼等级 NBT
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setInteger("refinement", refinementLevel);
        });

        // 写入随机 Lore NBT
        if (isRandomLore) {
            RandomLoreUtils.addRandomLoreWithNBT(this.equipmentItem, extractLore);
        }

        // 写入保护符 NBT（移星模式）
        if (paperLevel != 0) {
            NBT.modify(this.equipmentItem, nbt -> {
                nbt.setInteger("protector", paperLevel);
            });
        }

        // 写入宝石 NBT（移星模式）
        if (useSpeStoneParam) {
            for (SpeStone speStone : speStones) {
                NBT.modify(this.equipmentItem, nbt -> {
                    nbt.setInteger(speStone.getNbtKey(), speStone.getLevel());
                });
            }
        }

        // 写入精魂 NBT（移星模式）
        if (soulLevel != 0) {
            NBT.modify(this.equipmentItem, nbt -> {
                nbt.setInteger("infinite", soulLevel);
            });
        }

        // 精魂等级检查
        boolean isSoulDestroy = judgeSoul(refinementLevel);

        // 设置 Lore 版本号
        NBT.modify(this.equipmentItem, nbt -> {
            nbt.setString("loreversion", LoreUpdateManager.getCurrentLoreVersion());
        });

        return isSoulDestroy;
    }

    // ==================== Remove Refinement Info ====================

    /**
     * 移除装备淬炼信息（基本版本 — 总是移除所有附加 Lore）
     */
    public Map<String, List<String>> removeRefinementInfo(List<String> mainLore, List<String> extractLore,
                                                          int refinementLevel) {
        return removeRefinementInfoInternal(mainLore, extractLore, refinementLevel, true, false, 0);
    }

    /**
     * 移除装备淬炼信息（掉星版本 — 附加 Lore 仅在掉星后等级 > 0 时移除）
     *
     * @param downLevel 掉落的等级数
     */
    public Map<String, List<String>> removeRefinementInfoWithDownLevel(List<String> mainLore, List<String> extractLore,
                                                                      int refinementLevel, int downLevel) {
        boolean removeExtraLore = (refinementLevel - downLevel > 0);
        return removeRefinementInfoInternal(mainLore, extractLore, refinementLevel, removeExtraLore, false, 0);
    }

    /**
     * 移除装备淬炼信息（移星版本 — 移除所有附加 Lore 和 NBT）
     */
    public Map<String, List<String>> removeRefinementInfoForTransform(List<String> mainLore, List<String> extractLore,
                                                                     int refinementLevel) {
        return removeRefinementInfoInternal(mainLore, extractLore, refinementLevel, true, true, 0);
    }

    /**
     * 根据等级直接移除淬炼信息（公共便捷方法）
     */
    public Map<String, List<String>> removeRefinementInfo(int nowLevel) {
        Level level = LevelDataManager.levels.get(nowLevel - 1);
        String equipmentIdentifier = EquipmentDataManager.getEquipmentIdentifier(this.equipmentItem);
        List<String> mainLore = level.getMainLore();
        List<String> extractLore = level.getExtractLores().get(equipmentIdentifier);
        if (RandomLoreUtils.hasRandomLore(this.equipmentItem)) {
            extractLore = RandomLoreUtils.getRandomLore(this.equipmentItem);
        }
        return removeRefinementInfoForTransform(mainLore, extractLore, nowLevel);
    }

    // ==================== 核心实现 ====================

    /**
     * 统一的移除淬炼信息实现
     *
     * @param mainLore        主淬炼 Lore
     * @param extractLore     附加 Lore
     * @param refinementLevel 当前淬炼等级
     * @param removeExtraLore 是否移除宝石/保护符/精魂的 Lore
     * @param removeExtraNbt  是否移除宝石/保护符/精魂的 NBT 键
     * @param unused          保留参数（向后兼容）
     */
    private Map<String, List<String>> removeRefinementInfoInternal(List<String> mainLore, List<String> extractLore,
                                                                   int refinementLevel,
                                                                   boolean removeExtraLore,
                                                                   boolean removeExtraNbt,
                                                                   int unused) {
        ItemMeta im = equipmentItem.getItemMeta();

        // 移除显示名称后缀
        if (EquipmentDataManager.enableDisplayNameInfo) {
            String displayName = im.getDisplayName();
            if (displayName != null) {
                String suffix = EquipmentDataManager.displayNameSuffix.replace("{level}", refinementLevel + "");
                displayName = displayName.replace(suffix, "").trim();
                // 如果移除后缀后名称为空，尝试获取原始名称
                if (displayName.isEmpty()) {
                    displayName = EquipmentDataManager.chinesenames.get(equipmentItem.getType().name());
                    if (displayName == null || displayName.trim().isEmpty()) {
                        displayName = equipmentItem.getType().name().toLowerCase().replace('_', ' ');
                        displayName = Character.toUpperCase(displayName.charAt(0)) + displayName.substring(1);
                    }
                }
                im.setDisplayName(displayName);
            }
        }

        // 获取当前 Lore 列表
        List<String> lores = im.getLore();
        if (lores == null) {
            lores = new ArrayList<>();
        }

        // 移除 mainLore 头部标识
        int headerIndex = -1;
        for (int i = 0; i < lores.size(); i++) {
            if (lores.get(i).equals(EquipmentDataManager.mainLore)) {
                headerIndex = i;
                break;
            }
        }

        if(headerIndex != -1) {
            lores.remove(headerIndex);
        }
        // 移除 mainLore 内容
        lores.removeAll(mainLore);

        // 处理随机 Lore
        if (RandomLoreUtils.hasRandomLore(this.equipmentItem)) {
            extractLore = RandomLoreUtils.getRandomLore(this.equipmentItem);
        }
        lores.removeAll(extractLore);

        // 获取装备信息 Lore
        Map<String, List<String>> equipmentInfoLore = getEquipmentInfoLore(this.equipmentItem);

        // 条件性移除附加 Lore（宝石/保护符/精魂）
        if (removeExtraLore) {
            if (equipmentInfoLore.containsKey("spestone")) {
                List<String> speStoneLore = equipmentInfoLore.get("spestone");
                for (String s : speStoneLore) {
                    lores.remove(s);
                }
            }
            if (equipmentInfoLore.containsKey("paper")) {
                lores.removeAll(equipmentInfoLore.get("paper"));
            }
            if (equipmentInfoLore.containsKey("soul")) {
                lores.removeAll(equipmentInfoLore.get("soul"));
            }
        }

        im.setLore(lores);
        this.equipmentItem.setItemMeta(im);

        // 移除基本 NBT 键
        NBT.modify(equipmentItem, nbt -> {
            nbt.removeKey("refinement");
        });
        NBT.modify(equipmentItem, nbt -> {
            nbt.removeKey("randomlore");
        });

        // 移除附加 NBT 键（移星模式）
        if (removeExtraNbt) {
            List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
            for (SpeStone speStone : speStones) {
                NBT.modify(equipmentItem, nbt -> {
                    nbt.removeKey(speStone.getNbtKey());
                });
            }
            NBT.modify(equipmentItem, nbt -> {
                nbt.removeKey("protector");
            });
            NBT.modify(equipmentItem, nbt -> {
                nbt.removeKey("infinite");
            });
        }

        return equipmentInfoLore;
    }

    // ==================== 工具方法 ====================

    /**
     * 获取装备上所有已附加的信息 Lore（淬炼/宝石/保护符/精魂）
     */
    public static Map<String, List<String>> getEquipmentInfoLore(ItemStack equipmentItem) {
        Map<String, List<String>> map = new HashMap<>();
        NBTItem nbt = new NBTItem(equipmentItem);

        // 淬炼 Lore
        int refinementLevel = EquipmentDataManager.carifyEquipmentLevel(equipmentItem);
        if (refinementLevel != 0) {
            List<String> refineLore = new ArrayList<>();
            refineLore.add(EquipmentDataManager.mainLore);
            Level level = LevelDataManager.levels.get(refinementLevel - 1);
            refineLore.addAll(level.getMainLore());
            String equipmentIdentifier = EquipmentDataManager.getEquipmentIdentifier(equipmentItem);
            if (nbt.hasKey("randomlore")) {
                refineLore.addAll(RandomLoreUtils.getRandomLore(equipmentItem));
            } else {
                refineLore.addAll(level.getExtractLores().get(equipmentIdentifier));
            }
            map.put("refinement", refineLore);
        }

        // 宝石 Lore
        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);
        if (!speStones.isEmpty()) {
            List<String> speStoneLore = new ArrayList<>();
            speStoneLore.add(EquipmentDataManager.speStoneLore);
            for (SpeStone speStone : speStones) {
                speStoneLore.addAll(speStone.getEquipmentLore());
            }
            map.put("spestone", speStoneLore);
        }

        // 保护符 Lore
        String paperIdentifier = PaperDataManager.getPaperIdentifier(equipmentItem);
        if (paperIdentifier != null) {
            String paperLore = PaperDataManager.papers.get(paperIdentifier).getName();
            map.put("paper", Arrays.asList(paperLore));
        }

        // 精魂 Lore
        String soulName = InfiniteSoulManager.getSoulName(equipmentItem);
        if (soulName != null) {
            map.put("soul", Arrays.asList(soulName));
        }

        return map;
    }

    /**
     * 精魂破碎检查 — 淬炼等级低于精魂等级时移除精魂
     * @return 是否破碎
     */
    private boolean judgeSoul(int refinementLevel) {
        int soulLevel = new NBTItem(this.equipmentItem).getInteger("infinite");
        if (soulLevel == 0) {
            return false;
        }
        if (refinementLevel > soulLevel || refinementLevel == 0) {
            String soulIdentifier = InfiniteSoulManager.getSoulName(this.equipmentItem);
            ItemMeta itemMeta = this.equipmentItem.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore.remove(soulIdentifier);
            itemMeta.setLore(lore);
            this.equipmentItem.setItemMeta(itemMeta);
            NBT.modify(this.equipmentItem, nbt -> {
                nbt.removeKey("infinite");
            });
            return true;
        }else{
            return false;
        }
    }
}
