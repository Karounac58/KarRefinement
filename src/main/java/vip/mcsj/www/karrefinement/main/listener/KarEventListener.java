package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.api.event.KarGuiClickEvent;
import vip.mcsj.www.karrefinement.api.event.KarGuiOpenEvent;
import vip.mcsj.www.karrefinement.api.event.ProtectPaperApplyEvent;
import vip.mcsj.www.karrefinement.api.gui.GuiContext;
import vip.mcsj.www.karrefinement.api.gui.GuiSlot;
import vip.mcsj.www.karrefinement.api.gui.GuiSlotRegistry;
import vip.mcsj.www.karrefinement.api.gui.GuiType;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.KarForgeGui;
import vip.mcsj.www.karrefinement.gui.KarForgeInvHolder;
import vip.mcsj.www.karrefinement.gui.KarRefinementGui;
import vip.mcsj.www.karrefinement.gui.holder.KarRefinementInvHolder;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.service.gui.ForgeGuiContext;
import vip.mcsj.www.karrefinement.service.gui.RefinementGuiContext;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import static vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KarEventListener implements Listener {
    //判断淬炼面板是否关闭
    public static ConcurrentHashMap<Player,Integer> judgeInvCloseOrNot = new ConcurrentHashMap<>();
    //判断是否在淬炼进行时
    public static ConcurrentHashMap<Player,Integer> judgeInvRefinementOrNot = new ConcurrentHashMap<>();

    //判断Forge锻造界面是否关闭
    public static ConcurrentHashMap<Player,Integer> judgeForgeInvCloseOrNot = new ConcurrentHashMap<>();
    //判断是否正在锻造
    public static ConcurrentHashMap<Player,Integer> judgeInvForgeOrNot = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Player,Integer> judgeTransformInvCloseOrNot = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Player,Integer> judgeInvTransformOrNot = new ConcurrentHashMap<>();

    //用于在锻造时关闭菜单后恢复菜单状态
    public static ConcurrentHashMap<Player,Inventory> forgeInvs = new ConcurrentHashMap<>();

    //用于在淬炼时关闭菜单后恢复菜单状态
    public static ConcurrentHashMap<Player,Inventory> invs = new ConcurrentHashMap<>();

    //锻造面板关闭后
    public static ConcurrentHashMap<Player,ItemStack[]> closeItems2 = new ConcurrentHashMap<>();

    //淬炼菜单关闭后
    public static ConcurrentHashMap<Player,ItemStack[]> closeItems = new ConcurrentHashMap();


    /**
     * 保护符事件
     * @param e
     */
    @EventHandler
    public void onInventoryClickItemEvent2(InventoryClickEvent e){
        ItemStack itemPaper;
        ItemStack itemEquipment;
        if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
            Player p1 = (Player) e.getWhoClicked();
            Integer refinementState = judgeInvRefinementOrNot.get(p1);
            if(refinementState != null && refinementState == 1){
                return;
            }
            //获取点击前在光标上的物品(保护符)
            itemPaper = e.getCursor();
            //获取点击前在所点击格子上的物品(待强化装备)
            itemEquipment = e.getCurrentItem();
            if(PaperDataManager.isPaperLegal(itemPaper)){
                if(EquipmentDataManager.isEquipmentLegal(itemEquipment)){
                    // 触发 ProtectPaperApplyEvent
                    ProtectPaperApplyEvent paperEvent = new ProtectPaperApplyEvent(p1, itemPaper, itemEquipment);
                    Bukkit.getPluginManager().callEvent(paperEvent);
                    if (paperEvent.isCancelled()) {
                        return;
                    }
                    if(PaperDataManager.protectorPaperUp(itemPaper,itemEquipment)) {
                        Player p = (Player)e.getWhoClicked();
                        p.sendMessage(Message.messages.get("paper_up"));
                        p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0),1,1);
                        KarUtils.removeItemRefinement(itemPaper);
                    }
                }
            }
        }
    }

    //宝石事件
    @EventHandler
    public void onInventoryClickItemEvent3(InventoryClickEvent e){
        ItemStack itemStone;
        ItemStack itemEquipment;
        if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
            Player p1 = (Player) e.getWhoClicked();
            Integer refinementState = judgeInvRefinementOrNot.get(p1);
            if(refinementState != null && refinementState == 1){
                return;
            }
            itemStone = e.getCursor();
            itemEquipment = e.getCurrentItem();
            //宝石是否可用
            if(SpecialStoneDataManager.isSpeStoneLegal(itemStone)){
                SpeStone speStone = SpecialStoneDataManager.getSpeStone(itemStone);
                //装备是否可用
                if(SpecialStoneDataManager.isEquipmentLegal(speStone,itemEquipment)){
                    if(SpecialStoneDataManager.specialStoneUp(itemStone,itemEquipment)){
                        Player p = (Player)e.getWhoClicked();
                        p.sendMessage(Message.messages.get("spestone_up"));
                        p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0),1,1);
                        KarUtils.removeItemRefinement(itemStone);
                    }
                }
            }
        }
    }
    //无限耐久精魂事件
    @EventHandler
    public void onInfiniteSoulUp(InventoryClickEvent e){
        ItemStack itemSoul;
        ItemStack itemEquipment;
        if(e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
            Player p1 = (Player) e.getWhoClicked();
            Integer refinementState = judgeInvRefinementOrNot.get(p1);
            if(refinementState != null && refinementState == 1){
                return;
            }
            itemSoul = e.getCursor();
            itemEquipment = e.getCurrentItem();
            //精魂是否可用
            if(InfiniteSoulManager.isInfiniteSoulLegal(itemSoul)){
                if(EquipmentDataManager.isEquipmentLegal(itemEquipment)){
                    if(InfiniteSoulManager.infiniteSoulUp(itemSoul,itemEquipment)) {
                        Player p = (Player)e.getWhoClicked();
                        p.sendMessage(Message.messages.get("soul_up"));
                        p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0),1,1);
                        KarUtils.removeItemRefinement(itemSoul);
                    }
                }
            }
        }
    }

    //无限耐久装备消耗耐久事件
    @EventHandler
    public void onInfiniteEquipmentConsumeDurability(PlayerItemDamageEvent e){
        ItemStack damageItem = e.getItem();
        NBTItem nbtItem = new NBTItem(damageItem);
        if(nbtItem.hasTag("infinite")){
            e.setCancelled(true);
        }
    }

    /**
     * gui淬炼监听事件
     * @param e
     */
    @EventHandler
    public void onKarInventoryClickEvent(@NotNull InventoryClickEvent e){

        Inventory inv = e.getInventory();
        if(inv == null){
            return;
        }

        if(e.getClickedInventory() == null){
            return;
        }
        if(!(e.getClickedInventory().getHolder() instanceof KarRefinementInvHolder)){
            //System.out.println("1");
            return;
        }
        //如果正在淬炼中，则不能移动物品
        Integer refinementRunning = judgeInvRefinementOrNot.get((Player)e.getWhoClicked());
        if(refinementRunning != null && refinementRunning == 1){
            e.getWhoClicked().sendMessage(Message.messages.get("refinement_running"));
            e.setCancelled(true);
            return;
        }
        if(!(e.getSlot() == 29 || e.getSlot() == 33)){
            // 检查是否为自定义槽位
            if (KarRefinementAPI.getInstance() != null) {
                GuiSlotRegistry registry = KarRefinementAPI.getGuiSlotRegistry();
                for (GuiSlot slot : registry.getSlots(GuiType.REFINEMENT)) {
                    if (slot.getSlotIndex() == e.getSlot()) {
                        e.setCancelled(true);
                        Player slotPlayer = (Player) e.getWhoClicked();
                        GuiContext context = new RefinementGuiContext(inv, slotPlayer, registry.getSlots(GuiType.REFINEMENT));
                        slot.onClick(e, context);
                        return;
                    }
                }
            }
            //System.out.println("2");
            e.setCancelled(true);
        }
        if(e.getSlot() == 8){
            Inventory forgeInv = Bukkit.createInventory(new KarForgeInvHolder(),45, PlaceholderAPI.setPlaceholders((Player) e.getWhoClicked(),KarForgeGui.title));
            KarForgeGui.initInv(forgeInv,(Player) e.getWhoClicked());
            e.getWhoClicked().openInventory(forgeInv);
        }
        if(e.getSlot() == 49 && e.getClick().equals(ClickType.LEFT)){
            ItemStack itemStone = inv.getItem(29);
            ItemStack itemEquipment = inv.getItem(33);
            Player p1 = (Player)e.getWhoClicked();

            if (KarRefinementGui.judgeInventoryClickMethod(itemStone,itemEquipment,p1)) {
                KarRefinementGui.playInvVideo(e.getInventory(),itemStone,itemEquipment,p1);
                closeItems.put(p1,new ItemStack[]{itemStone,itemEquipment});
            }
        }else if(e.getSlot() == 49 && e.getClick().equals(ClickType.RIGHT)){
            Player p1 = (Player)e.getWhoClicked();
            if(!KarRefinementGui.enableFastRefine){
                p1.sendMessage(Message.messages.get("refinement_fast_off"));
                return;
            }
            ItemStack itemStone = inv.getItem(29);
            ItemStack itemEquipment = inv.getItem(33);


            if (KarRefinementGui.judgeInventoryClickMethod(itemStone,itemEquipment,p1)) {
//                KarRefinementGui.KarRefinementMethod(itemStone,itemEquipment,p1,0.0);
                if(KarRefinementGui.KarRefinementMethod(p1,itemEquipment,StoneDataManager.getStone(itemStone),0)){
                    KarUtils.removeItemRefinement(itemStone);
                }
                closeItems.put(p1,new ItemStack[]{itemStone,itemEquipment});
            }
            KarRefinementGui.renderCustomSlots(inv, p1, GuiType.REFINEMENT);
        }
    }


    @EventHandler
    public void onPlayerDamageOtherEvent(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            if(attacker.getInventory().getItemInMainHand().getType() == Material.AIR){
                return;
            }
            ItemStack itemInUse = attacker.getInventory().getItemInMainHand();
            if(!canRefinementEquipment.get("Hand").contains(itemInUse.getType().name().toUpperCase())){
                return;
            }
            NBTItem nbtItem = new NBTItem(itemInUse);
            if(!nbtItem.hasTag("lfs")){
                return;
            }
            int i = nbtItem.getInteger("lfs");
            switch (i){
                case 1:
                    victim.setFireTicks(10*20);
                    break;
                case 2:
                    victim.setFireTicks(20*20);
                    break;
                case 3:
                    victim.setFireTicks(30*20);
                    break;
                case 4:
                    victim.setFireTicks(40*20);
                    break;
                case 5:
                    victim.setFireTicks(50*20);
                    break;
            }
        }
    }

    /**
     * 打开淬炼或锻造面板事件
     * @param e
     */
    @EventHandler
    public void onKarInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof KarRefinementInvHolder) {
            // 触发 KarGuiOpenEvent
            KarGuiOpenEvent openEvent = new KarGuiOpenEvent((Player) e.getPlayer(), GuiType.REFINEMENT, e.getInventory());
            Bukkit.getPluginManager().callEvent(openEvent);
            if (openEvent.isCancelled()) {
                e.setCancelled(true);
                return;
            }
            judgeInvCloseOrNot.put((Player) e.getPlayer(),1);
        }
        if (e.getInventory().getHolder() instanceof KarForgeInvHolder) {
            // 触发 KarGuiOpenEvent
            KarGuiOpenEvent openEvent = new KarGuiOpenEvent((Player) e.getPlayer(), GuiType.FORGE, e.getInventory());
            Bukkit.getPluginManager().callEvent(openEvent);
            if (openEvent.isCancelled()) {
                e.setCancelled(true);
                return;
            }
            judgeForgeInvCloseOrNot.put((Player) e.getPlayer(),1);
        }
    }


    /**
     * 淬炼面板关闭事件
     * @param e
     */
    @EventHandler
    public void onKarInventoryCloseEvent(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof KarRefinementInvHolder) {
            Integer refinementState = judgeInvRefinementOrNot.get((Player) e.getPlayer());
            if(refinementState != null && refinementState == 1){
                Bukkit.getScheduler().runTaskLater(KarRefinement.instance,()-> e.getPlayer().openInventory(invs.get(e.getPlayer())),1L);
                return;
            }
            ItemStack itemStone = e.getInventory().getItem(29);
//            e.getInventory().setItem(29,null);
            ItemStack itemEquipment = e.getInventory().getItem(33);
//            e.getInventory().setItem(33,null);
            Player p = (Player) e.getPlayer();
            if(itemStone != null) {
                p.getInventory().addItem(itemStone);
            }
            if(itemEquipment != null){
                p.getInventory().addItem(itemEquipment);
            }

            //标记关闭菜单，目的在于停止播放音乐和淬炼
            judgeInvCloseOrNot.put(p,0);
            judgeInvRefinementOrNot.put(p,0);
        }
    }

    /**
     * 玩家退出则移除
     *  - 是否淬炼map
     *  - 淬炼菜单是否关闭map
     * @param e
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        Integer refinementState = judgeInvRefinementOrNot.get(p);
        if(refinementState != null && refinementState == 1){
            ItemStack[] items = closeItems.get(p);
            if(items != null && items.length > 0){
                p.getInventory().addItem(items);
            }
        }
        judgeInvRefinementOrNot.remove(p);
        judgeInvCloseOrNot.remove(p);
        closeItems.remove(p);

        judgeInvForgeOrNot.remove(p);
        judgeForgeInvCloseOrNot.remove(p);
        closeItems2.remove(p);
        //消除淬炼特效
        if(EffectDataManager.getTask(p.getName()) != null){
            EffectDataManager.getTask(p.getName()).cancel();
            EffectDataManager.removeTaskFromMap(p.getName());
        }
    }

    /**
     * 玩家登录则加入
     *  - 是否淬炼map
     *  - 淬炼菜单是否关闭map
     * @param e
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        judgeInvRefinementOrNot.put(e.getPlayer(),0);
        judgeInvCloseOrNot.put(e.getPlayer(),0);
        closeItems.put(e.getPlayer(),new ItemStack[]{});

        judgeInvForgeOrNot.put(e.getPlayer(),0);
        judgeForgeInvCloseOrNot.put(e.getPlayer(),0);
        closeItems2.put(e.getPlayer(),new ItemStack[]{});
    }

    /**
     * 玩家死亡时，如果还在淬炼，则把淬炼物品扔到背包里，并重置淬炼状态
     * @param e
     */
    @EventHandler
    public void onPlayerDied(PlayerDeathEvent e){
        Player p = e.getEntity();
        judgeInvRefinementOrNot.put(p,0);
        judgeInvCloseOrNot.put(p,0);

        judgeForgeInvCloseOrNot.put(p,0);
        judgeInvForgeOrNot.put(p,0);
    }

    /**
     * GUI锻造面板事件
     * @param e
     */
    @EventHandler
    public void onPlayerClickedForgeGUI(InventoryClickEvent e){

        Inventory inv = e.getInventory();
        if(inv == null){
            return;
        }

        if(e.getClickedInventory() == null){
            return;
        }
        if(!(e.getClickedInventory().getHolder() instanceof KarForgeInvHolder)){
            //System.out.println("1");
            return;
        }
        //如果正在淬炼中，则不能移动物品
        Integer forgeRunning = judgeInvForgeOrNot.get((Player)e.getWhoClicked());
        if(forgeRunning != null && forgeRunning == 1){
            e.getWhoClicked().sendMessage(Message.messages.get("forge_running"));
            e.setCancelled(true);
            return;
        }
        if(!(e.getSlot() == 19 || e.getSlot() == 25)){
            // 检查是否为自定义槽位
            if (KarRefinementAPI.getInstance() != null) {
                GuiSlotRegistry registry = KarRefinementAPI.getGuiSlotRegistry();
                for (GuiSlot slot : registry.getSlots(GuiType.FORGE)) {
                    if (slot.getSlotIndex() == e.getSlot()) {
                        e.setCancelled(true);
                        Player slotPlayer = (Player) e.getWhoClicked();
                        GuiContext context = new ForgeGuiContext(inv, slotPlayer, registry.getSlots(GuiType.FORGE));
                        slot.onClick(e, context);
                        return;
                    }
                }
            }
            //System.out.println("2");
            e.setCancelled(true);
        }
        if(e.getSlot() == 4 && e.getClick().equals(ClickType.LEFT)){
            ItemStack itemEquipment1 = inv.getItem(19);
            ItemStack itemEquipment2 = inv.getItem(25);
            if(itemEquipment1 == null || itemEquipment2 == null){
                e.getWhoClicked().sendMessage(Message.messages.get("forge_needitem"));
                return;
            }
            EquipmentDataManager manager1 = new EquipmentDataManager(itemEquipment1);
            EquipmentDataManager manager2 = new EquipmentDataManager(itemEquipment2);

            if(manager1.carifyEquipmentLevel() != manager2.carifyEquipmentLevel()){
                e.getWhoClicked().sendMessage(Message.messages.get("forge_conflictlevel"));
                return;
            }

            Double success = forgeSuccessList.get(manager1.carifyEquipmentLevel() + 1);
            if(success == null){
                e.getWhoClicked().sendMessage(Message.messages.get("forge_disablelevel"));
                return;
            }
            ItemStack item = inv.getItem(4);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Arrays.asList(Message.messages.get("forge_success").replace("{success}",success+"")));
            item.setItemMeta(itemMeta);
        }
        if(e.getSlot() == 22 && e.getClick().equals(ClickType.LEFT)){
            ItemStack itemEquipment1 = inv.getItem(19);
            ItemStack itemEquipment2 = inv.getItem(25);
            Player p1 = (Player)e.getWhoClicked();

            if (KarForgeGui.judgeInventoryClickMethod(itemEquipment1,itemEquipment2,p1)) {
                KarForgeGui.playInvVideo(e.getInventory(),itemEquipment1,itemEquipment2,p1);
                closeItems2.put(p1,new ItemStack[]{itemEquipment1,itemEquipment2});
            }
        }
    }

    /**
     * 锻造面板关闭事件
     * @param e
     */
    @EventHandler
    public void onKarForgeGUICloseEvent(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof KarForgeInvHolder) {
            Integer forgeState = judgeInvForgeOrNot.get((Player) e.getPlayer());
            if(forgeState != null && forgeState == 1){
                Bukkit.getScheduler().runTaskLater(KarRefinement.instance,()-> e.getPlayer().openInventory(forgeInvs.get(e.getPlayer())),1L);
                return;
            }
            ItemStack itemEquipment1 = e.getInventory().getItem(19);
//            e.getInventory().setItem(29,null);
            ItemStack itemEquipment2 = e.getInventory().getItem(25);
//            e.getInventory().setItem(33,null);
            Player p = (Player) e.getPlayer();
            if(itemEquipment1 != null) {
                p.getInventory().addItem(itemEquipment1);
            }
            if(itemEquipment2 != null){
                p.getInventory().addItem(itemEquipment2);
            }

            //标记关闭菜单，目的在于停止播放音乐和淬炼
            judgeForgeInvCloseOrNot.put(p,0);
            judgeInvForgeOrNot.put(p,0);
        }
    }

    /**
     * @param e
     */
    @EventHandler
    public void onPlayerChangeHeldItem(PlayerItemHeldEvent e){
        if(EffectDataManager.getTask(e.getPlayer().getName()) != null) {
            String name = e.getPlayer().getName();
            EffectDataManager.getTask(name).cancel();
            EffectDataManager.removeTaskFromMap(name);
        }
    }

    @EventHandler
    public void onPlayerQuitStats(PlayerQuitEvent event) {
        // 玩家退出时清理缓存，确保数据已保存
        PlayerStatsDataManager.removeFromCache(event.getPlayer().getUniqueId());
    }
    

}
