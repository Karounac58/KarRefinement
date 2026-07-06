package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.api.gui.GuiSlot;
import vip.mcsj.www.karrefinement.api.gui.GuiSlotRegistry;
import vip.mcsj.www.karrefinement.api.gui.GuiType;
import vip.mcsj.www.karrefinement.api.event.PostForgeEvent;
import vip.mcsj.www.karrefinement.api.event.PreForgeEvent;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.listener.KarEventListener;
import vip.mcsj.www.karrefinement.object.AnimationType;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.service.gui.ForgeGuiContext;
import vip.mcsj.www.karrefinement.service.SoundDataManager;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static vip.mcsj.www.karrefinement.gui.KarRefinementGui.createInvItem;

public class KarForgeGui {
    public static Map<String, InvItem> fItems = new HashMap<>();
    public static String title = "";
    public static List<Integer> amSlots;

    public static AnimationType playType;

    public static int mainSlot;

    public static int deputySlot;
    public static void init(){
        if(!fItems.isEmpty()){
            fItems.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("gui/forgegui.yml");
        title = file.getString("Title");
        ConfigurationSection fileCS = file.getConfigurationSection("Item");
        Set<String> keys = fileCS.getKeys(false);
        for (String key : keys) {
            String name = fileCS.getString(key + ".Name");
            Material material = Material.valueOf(fileCS.getString(key + ".Material"));
            List<Integer> slots = new ArrayList<>();
            if(!key.equals("VideoItem")){
                slots = fileCS.getIntegerList(key+".Slots");
            }
            int data = fileCS.getInt(key + ".Data");
            int customModelData = fileCS.getInt(key + ".CustomModelData");
            List<String> lore = fileCS.getStringList(key + ".Lore");
            if(slots.isEmpty()) {
                fItems.put(key, new InvItem(name, material, data, customModelData, lore));
            }else{
                fItems.put(key,new InvItem(name,material,slots,data,customModelData,lore));
            }
        }

        amSlots = file.getIntegerList("Animation.Slots");
        playType = AnimationType.valueOf(file.getString("Animation.PlayType"));
        mainSlot = file.getInt("Slot1");
        deputySlot = file.getInt("Slot2");
    }
    public static void initInv(Inventory inv,Player p) {
//        List<Integer> other = Arrays.asList(0,1,2,3,5,6,7,8,36,37,38,39,40,41,42,43,44);
//        List<Integer> greenPanes = Arrays.asList(9,10,11,18,20,27,28,29);
//        List<Integer> blackPanes = Arrays.asList(12,13,14,21,23,30,31,32);
//        List<Integer> bluePanes = Arrays.asList(15,16,17,24,26,33,34,35);
        InvItem redPaneInvItem = fItems.get("Barrier2");
        InvItem blackPaneInvItem = fItems.get("Barrier3");
        InvItem bluePaneInvItem = fItems.get("Barrier4");
        InvItem whitePaneInvItem = fItems.get("Barrier");
        InvItem confirmInvItem = fItems.get("ConfirmButton");
        InvItem infoInvItem = fItems.get("InfoButton");
        ItemStack redPaneItem = createInvItem(redPaneInvItem,p);
        ItemStack blackPaneItem = createInvItem(blackPaneInvItem,p);
        ItemStack bluePaneItem = createInvItem(bluePaneInvItem,p);
        ItemStack whitePaneItem = createInvItem(whitePaneInvItem,p);
        ItemStack redStone = createInvItem(confirmInvItem,p);
        ItemStack oak_sign = createInvItem(infoInvItem,p);
        for (Integer index : whitePaneInvItem.getSlots()) {
            inv.setItem(index,whitePaneItem);
        }
        for (Integer index : redPaneInvItem.getSlots()) {
            inv.setItem(index,redPaneItem);
        }
        for (Integer index : blackPaneInvItem.getSlots()) {
            inv.setItem(index,blackPaneItem);
        }
        for (Integer index : bluePaneInvItem.getSlots()) {
            inv.setItem(index,bluePaneItem);
        }
        for (Integer index : infoInvItem.getSlots()) {
            inv.setItem(index,oak_sign);
        }

        for (Integer index : confirmInvItem.getSlots()) {
            inv.setItem(index,redStone);
        }

        // === 渲染自定义槽位 ===
        if (KarRefinementAPI.getInstance() != null) {
            GuiSlotRegistry registry = KarRefinementAPI.getGuiSlotRegistry();
            java.util.List<GuiSlot> slots = registry.getSlots(GuiType.FORGE);
            if (!slots.isEmpty()) {
                ForgeGuiContext context = new ForgeGuiContext(inv, p, slots);
                for (GuiSlot slot : slots) {
                    ItemStack display = slot.buildDisplayItem(p, context);
                    if (display != null) {
                        inv.setItem(slot.getSlotIndex(), display);
                    }
                }
            }
        }
    }


    public static ItemStack removeItemStackName(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static boolean judgeInventoryClickMethod(ItemStack equipmentItem1, ItemStack equipmentItem2, Player p) {
        if (equipmentItem1 == null || equipmentItem2 == null) {
            return false;
        }


        if (!(EquipmentDataManager.isEquipmentLegal(equipmentItem1) || EquipmentDataManager.isEquipmentLegal(equipmentItem2))) {
            p.sendMessage(Message.messages.get("forge_disbaleitem"));
            return false;
        }

        EquipmentDataManager itemManager1 = new EquipmentDataManager(equipmentItem1);
        EquipmentDataManager itemManager2 = new EquipmentDataManager(equipmentItem2);
        if(EquipmentDataManager.forgeSuccessList.get(itemManager1.carifyEquipmentLevel()+1) == null){
            p.sendMessage(Message.messages.get("forge_disablelevel"));
            return false;
        }
        if(itemManager1.carifyEquipmentLevel() != itemManager2.carifyEquipmentLevel()){
            p.sendMessage(Message.messages.get("forge_conflictlevel"));
            return false;
        }
        return true;
    }

    public static void KarForgeMethod(ItemStack itemEquipment1, ItemStack itemEquipment2, Player p) {
        if(judgeInventoryClickMethod(itemEquipment1,itemEquipment2,p)){
            // 触发 PreForgeEvent
            PreForgeEvent preEvent = new PreForgeEvent(p, itemEquipment1, itemEquipment2);
            Bukkit.getPluginManager().callEvent(preEvent);
            if (preEvent.isCancelled()) {
                return;
            }

            EquipmentDataManager manager1 = new EquipmentDataManager(itemEquipment1,p);
            double success = EquipmentDataManager.forgeSuccessList.get(manager1.carifyEquipmentLevel()+1);
            BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
            boolean forgeSuccess;
            if(decimal.doubleValue() < success){
                forgeSuccess = true;
                manager1.injuryUpStar();
                p.sendMessage(Message.messages.get("forge_upstar"));

                Sound successSound = SoundDataManager.getSound("KarForgeGui", "Success");
                if(successSound != null){
                    p.playSound(p.getLocation(), successSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                KarUtils.removeItemRefinement(itemEquipment2);
            }else{
                forgeSuccess = false;
                p.sendMessage(Message.messages.get("forge_failed"));

                Sound failSound = SoundDataManager.getSound("KarForgeGui", "Fail");
                if(failSound != null){
                    p.playSound(p.getLocation(), failSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                }
                KarUtils.removeItemRefinement(itemEquipment2);
            }

            // 触发 PostForgeEvent
            PostForgeEvent postEvent = new PostForgeEvent(p, itemEquipment1, itemEquipment2, forgeSuccess);
            Bukkit.getPluginManager().callEvent(postEvent);
        }
    }

    /**
     * @param inv           //用于播放动画
     * @param itemEquipment1     //用于让锻造方法获取参数
     * @param itemEquipment2     //用于让锻造方法获取参数
     * @param p
     */
    public static void playInvVideo(Inventory inv, ItemStack itemEquipment1, ItemStack itemEquipment2, Player p) {
        List<Integer> indexs = new ArrayList<>(amSlots);
        int size = indexs.size();
        ItemStack videoItem = createInvItem(fItems.get("VideoItem"),p);
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                if(index == 0) {
                    KarEventListener.judgeInvForgeOrNot.put(p, 1);
                }
                Integer closeState = KarEventListener.judgeForgeInvCloseOrNot.get(p);
                if (closeState == null || closeState == 0) {
                    this.cancel();
                    return;
                }
                if(index >= size) {
                    KarForgeMethod(itemEquipment1, itemEquipment2, p);
                    initInv(inv, p);
                    KarEventListener.judgeInvForgeOrNot.put(p, 0);
                    this.cancel();
                    return;
                }

                int h = 0;
                if(playType == AnimationType.Turn) {
                    h = indexs.get(index);
                }else if(playType == AnimationType.Random){
                    int randIdx = ThreadLocalRandom.current().nextInt(indexs.size());
                    h = indexs.remove(randIdx);
                }

                inv.setItem(h,videoItem);
                p.updateInventory();
                KarEventListener.forgeInvs.put(p, inv);
                
                Sound runSound = SoundDataManager.getSound("KarForgeGui", "Run");
                if(runSound != null){
                    p.playSound(p.getLocation(), runSound, 1, 1);
                }else{
                    p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                }
                index++;
            }
        }.runTaskTimer(KarRefinement.instance, 0L, 20L);

    }

}
