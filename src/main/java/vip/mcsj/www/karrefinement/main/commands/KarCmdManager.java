package vip.mcsj.www.karrefinement.main.commands;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.*;

import java.util.List;

//命令接收者
public class KarCmdManager {

    public boolean adminUp(Player p){
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
        if (EquipmentDataManager.isEquipmentLegal(itemInMainHand)) {
            EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand,p);
            manager.injuryUpStar();
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return true;
        }else{
            return false;
        }
    }

    public void reload(){
        StoneDataManager.init();
        EquipmentDataManager.init();
        EquipmentDataManager.initForgeData();
        LevelDataManager.init();
        PaperDataManager.init();
        SpecialStoneDataManager.init();
        InfiniteSoulManager.init();
        DUPaperDataManager.init();
        KarCompoundStoneGui.initCompoundData();
    }

    public void getNBT(Player p,String[] args){
        ItemStack invItem = p.getInventory().getItemInMainHand();
        int a = new NBTItem(invItem).getInteger(args[1]);
        p.sendMessage(String.valueOf(a));
    }

    public void clearLore(Player p){
        ItemStack invItem1 = p.getInventory().getItemInMainHand();
        ItemMeta itemMeta = invItem1.getItemMeta();
        List<String> lores = itemMeta.getLore();
        lores.clear();
        itemMeta.setLore(lores);
        invItem1.setItemMeta(itemMeta);
    }

    public boolean giveStone(Player p,String[] args){
        StoneDataManager stoneManager = new StoneDataManager(args[2]);
        ItemStack stone = stoneManager.createStone();
        if (stone == null) {
            p.sendMessage(ChatColor.RED + "没有这个淬炼石");
            return false;
        }
        int i = Integer.parseInt(args[3]);
        for (int j = 0; j < i; j++) {
            p.getInventory().addItem(stoneManager.createStone());
        }
        return true;
    }

    public void givePaper(Player p,String[] args){
        PaperDataManager paperDataManager = new PaperDataManager(args[2]);
        p.getInventory().addItem(paperDataManager.createProtectedPaper());
    }


    public void giveDUPaper(Player p,String[] args){
        p.getInventory().addItem(DUPaperDataManager.createDUPaper(args[2]));
    }

    public void giveSpeStone(Player p,String[] args){
        SpecialStoneDataManager speStoneManager = new SpecialStoneDataManager(args[2]);
        p.getInventory().addItem(speStoneManager.createSpeStone());
    }

    public void giveSoul(Player p,String[] args){
        InfiniteSoulManager soulManager = new InfiniteSoulManager(args[2]);
        p.getInventory().addItem(soulManager.createInfiniteSoul());
    }

    public void setNBT(Player p,String[] args){
        ItemStack invItem = p.getInventory().getItemInMainHand();
        NBT.modify(invItem, nbt -> {
            nbt.setInteger(args[1], Integer.parseInt(args[2]));
        });
    }

    public void openGui(Player p){
        Inventory inv = Bukkit.createInventory(new KarRefinementInvHolder(), 54, "§c淬炼界面");
        KarRefinementGui.setInvInitial(inv,p);
        p.openInventory(inv);
    }

    public void openForgeGui(Player p){
        Inventory inv1 = Bukkit.createInventory(new KarForgeInvHolder(),45,"§c§l锻造界面");
        KarForgeGui.initInv(inv1,p);
        p.openInventory(inv1);
    }

    public void openCompoundGui(Player p){
        Inventory inv2 = Bukkit.createInventory(new KarCompoundStoneInvHolder(),54,"§c§l宝石合成界面");
        KarCompoundStoneGui.initial(inv2,p);
        KarCompoundStoneGui.openGuiForPlayer(inv2,p);
    }
}
