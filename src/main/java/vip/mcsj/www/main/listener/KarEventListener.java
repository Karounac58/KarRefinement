package vip.mcsj.www.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import vip.mcsj.www.datamanager.*;
import vip.mcsj.www.gui.KarForgeGui;
import vip.mcsj.www.gui.KarForgeInvHolder;
import vip.mcsj.www.gui.KarRefinementGui;
import vip.mcsj.www.gui.KarRefinementInvHolder;
import vip.mcsj.www.main.KarRefinement;
import vip.mcsj.www.object.SpeStone;
import vip.mcsj.www.utils.KarUtils;
import static vip.mcsj.www.datamanager.EquipmentDataManager.*;

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
    //用于在锻造时关闭菜单后恢复菜单状态
    public static ConcurrentHashMap<Player,Inventory> forgeInvs = new ConcurrentHashMap<>();

    //用于在淬炼时关闭菜单后恢复菜单状态
    public static ConcurrentHashMap<Player,Inventory> invs = new ConcurrentHashMap<>();

    //锻造面板关闭后
    public static ConcurrentHashMap<Player,ItemStack[]> closeItems2 = new ConcurrentHashMap<>();

    //淬炼菜单关闭后
    public static ConcurrentHashMap<Player,ItemStack[]> closeItems = new ConcurrentHashMap<>();


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
            if(judgeInvRefinementOrNot.get(p1) == 1){
                return;
            }
            //获取点击前在光标上的物品(保护符)
            itemPaper = e.getCursor();
            //获取点击前在所点击格子上的物品(待强化装备)
            itemEquipment = e.getCurrentItem();
            if(PaperDataManager.isPaperLegal(itemPaper)){
                if(EquipmentDataManager.isEquipmentLegal(itemEquipment)){
                    //System.out.println("11111");
                    if(PaperDataManager.protectorPaperUp(itemPaper,itemEquipment)) {
                        //System.out.println("2222");
                        Player p = (Player)e.getWhoClicked();
                        p.sendMessage("§a保护符已经成功融入这件装备");
                        p.playSound(p.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_BLAST,1,1);
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
            if(judgeInvRefinementOrNot.get(p1) == 1){
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
                        p.sendMessage("§a宝石已经成功镶嵌进这件装备！");
                        p.playSound(p.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_BLAST,1,1);
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
            if(judgeInvRefinementOrNot.get(p1) == 1){
                return;
            }
            itemSoul = e.getCursor();
            itemEquipment = e.getCurrentItem();
            //精魂是否可用
            if(InfiniteSoulManager.isInfiniteSoulLegal(itemSoul)){
                if(EquipmentDataManager.isEquipmentLegal(itemEquipment)){
                    if(InfiniteSoulManager.infiniteSoulUp(itemSoul,itemEquipment)) {
                        Player p = (Player)e.getWhoClicked();
                        p.sendMessage("§a精魂已经成功融入这件装备");
                        p.playSound(p.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_BLAST,1,1);
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
        if(Objects.requireNonNullElse(judgeInvRefinementOrNot.get((Player)e.getWhoClicked()),0) == 1){
            e.getWhoClicked().sendMessage("§c请等待淬炼结束！");
            e.setCancelled(true);
            return;
        }
        if(!(e.getSlot() == 29 || e.getSlot() == 33)){
            //System.out.println("2");
            e.setCancelled(true);
        }
        if(e.getSlot() == 8){
            Inventory forgeInv = Bukkit.createInventory(new KarForgeInvHolder(),45,"§c§l锻造界面");
            KarForgeGui.initInv(forgeInv);
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
            ItemStack itemStone = inv.getItem(29);
            ItemStack itemEquipment = inv.getItem(33);

            Player p1 = (Player)e.getWhoClicked();
            if (KarRefinementGui.judgeInventoryClickMethod(itemStone,itemEquipment,p1)) {
                KarRefinementGui.KarRefinementMethod(itemStone,itemEquipment,p1);
                closeItems.put(p1,new ItemStack[]{itemStone,itemEquipment});
            }
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
            System.out.println("落凤石：淬炼物品");
            NBTItem nbtItem = new NBTItem(itemInUse);
            if(!nbtItem.hasTag("lfs")){
                return;
            }
            attacker.sendMessage("落凤石：nbt");
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
            judgeInvCloseOrNot.put((Player) e.getPlayer(),1);
        }
        if (e.getInventory().getHolder() instanceof KarForgeInvHolder) {
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
            if(judgeInvRefinementOrNot.get((Player) e.getPlayer()) == 1){
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
        if(judgeInvRefinementOrNot.get(p) == 1){
            p.getInventory().addItem(closeItems.get(p));
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
        if(Objects.requireNonNullElse(judgeInvForgeOrNot.get((Player)e.getWhoClicked()),0) == 1){
            e.getWhoClicked().sendMessage("§c请等待锻造结束！");
            e.setCancelled(true);
            return;
        }
        if(!(e.getSlot() == 19 || e.getSlot() == 25)){
            //System.out.println("2");
            e.setCancelled(true);
        }
        if(e.getSlot() == 4 && e.getClick().equals(ClickType.LEFT)){
            ItemStack itemEquipment1 = inv.getItem(19);
            ItemStack itemEquipment2 = inv.getItem(25);
            if(itemEquipment1 == null || itemEquipment2 == null){
                e.getWhoClicked().sendMessage("§c§l请放入要锻造的装备！");
                return;
            }
            EquipmentDataManager manager1 = new EquipmentDataManager(itemEquipment1);
            EquipmentDataManager manager2 = new EquipmentDataManager(itemEquipment2);

            if(manager1.carifyEquipmentLevel() != manager2.carifyEquipmentLevel()){
                e.getWhoClicked().sendMessage("§c§l请放入同级装备！");
                return;
            }

            Double success = forgeSuccessList.get(manager1.carifyEquipmentLevel() + 1);
            if(success == null){
                e.getWhoClicked().sendMessage("§c§l此淬炼等级不能锻造！");
                return;
            }
            ItemStack item = inv.getItem(4);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Arrays.asList("§a§l成功率：§6§l"+success+"%"));
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
            if(judgeInvForgeOrNot.get((Player) e.getPlayer()) == 1){
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
}
