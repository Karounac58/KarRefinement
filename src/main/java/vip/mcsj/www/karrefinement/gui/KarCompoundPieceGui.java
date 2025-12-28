package vip.mcsj.www.karrefinement.gui;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.DetachDataManager;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.datamanager.SpecialStoneDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Detach;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.*;

public class KarCompoundPieceGui {
    public static Map<String, InvItem> cpItems = new HashMap<>();
    public static String title = "";
    public static int size = 0;
    public static int originSlot = 0;
    public static int afterSlot = 0;

    public static void init(){
        if(!cpItems.isEmpty()){
            cpItems.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("gui/compoundpiecegui.yml");
        title = file.getString("Title");
        size = file.getInt("Size");
        originSlot = file.getInt("OriginSlot");
        afterSlot = file.getInt("AfterSlot");
        ConfigurationSection fileCS = file.getConfigurationSection("Item");
        Set<String> keys = fileCS.getKeys(false);
        for (String key : keys) {
            String name = fileCS.getString(key + ".Name");
            Material material = Material.valueOf(fileCS.getString(key + ".Material"));
            int data = fileCS.getInt(key + ".Data");
            List<Integer> slots = fileCS.getIntegerList(key + ".Slots");
            int customModelData = fileCS.getInt(key + ".CustomModelData");
            List<String> lore = fileCS.getStringList(key + ".Lore");
            cpItems.put(key,new InvItem(name, material, slots,data,customModelData, lore));
        }
    }

    public static void initInv(Inventory inv,Player p) {

        InvItem barrier = cpItems.get("Barrier");
        InvItem barrier2 = cpItems.get("Barrier2");
        InvItem confirmButton = cpItems.get("ConfirmButton");
        InvItem originInfo = cpItems.get("OriginInfo");
        InvItem afterInfo = cpItems.get("AfterInfo");

        ItemStack whitePaneItem = KarRefinementGui.createInvItem(barrier,p);
        ItemStack blackPaneItem = KarRefinementGui.createInvItem(barrier2,p);
        ItemStack redPaneItem = KarRefinementGui.createInvItem(confirmButton,p);
        ItemStack originSign =  KarRefinementGui.createInvItem(originInfo,p);
        ItemStack afterSign = KarRefinementGui.createInvItem(afterInfo,p);


        for (Integer slot : barrier.getSlots()) {
            inv.setItem(slot, whitePaneItem);
        }


        for (Integer slot : barrier2.getSlots()) {
            inv.setItem(slot, blackPaneItem);
        }

        for (Integer slot : confirmButton.getSlots()) {
            inv.setItem(slot, redPaneItem);
        }

        for (Integer slot : originInfo.getSlots()) {
            inv.setItem(slot, originSign);
        }

        for (Integer slot : afterInfo.getSlots()) {
            inv.setItem(slot, afterSign);
        }
    }

    public static boolean judgeInventoryClickMethod(ItemStack itemPieces, Player p){
        if(itemPieces == null || itemPieces.getItemMeta() == null) return false;

        if(!DetachDataManager.isPaperPieceLegal(itemPieces)) return false;

        int level = DetachDataManager.getPaperPieceLevel(itemPieces);

        String paperIdentifier = PaperDataManager.getPaperIdentifier(level);

        Detach detach = DetachDataManager.paperDetachs.get(paperIdentifier);

        if(detach == null) return false;

        int compound = detach.getCompound();

        if(itemPieces.getAmount() < compound) return false;

        return true;
    }


    public static ItemStack karCompoundPieceMethod(ItemStack itemPieces, Player p){
        int level = DetachDataManager.getPaperPieceLevel(itemPieces);

        String paperIdentifier = PaperDataManager.getPaperIdentifier(level);

        Detach detach = DetachDataManager.paperDetachs.get(paperIdentifier);

        int compound = detach.getCompound();
        KarUtils.removeItemRefinement(itemPieces, compound);
        PaperDataManager pdm = new PaperDataManager(paperIdentifier);
        return pdm.createProtectedPaper();
    }

    //宝石碎片判断
    public static boolean judgeInventoryClickMethod2(ItemStack itemPieces, Player p){
        if(itemPieces == null || itemPieces.getItemMeta() == null) return false;

        if(!DetachDataManager.isSpeStonePieceLegal(itemPieces)) return false;

        List<Object> speStonePieceInfo = DetachDataManager.getSpeStonePieceInfo(itemPieces);

        int level = (int)speStonePieceInfo.get(0);
        String speStoneIdentifier = (String)speStonePieceInfo.get(1);

        Detach detach = DetachDataManager.speStoneDetachs.get(speStoneIdentifier);

        if(detach == null) return false;

        int compound = detach.getCompound();

        if(itemPieces.getAmount() < compound) return false;

        return true;
    }

    //宝石碎片合成
    public static ItemStack karCompoundPieceMethod2(ItemStack itemPieces, Player p){
        List<Object> speStonePieceInfo = DetachDataManager.getSpeStonePieceInfo(itemPieces);

        int level = (int)speStonePieceInfo.get(0);

        String speStoneIdentifier = (String)speStonePieceInfo.get(1);

        Detach detach = DetachDataManager.speStoneDetachs.get(speStoneIdentifier);

        int compound = detach.getCompound();
        KarUtils.removeItemRefinement(itemPieces, compound);
        SpecialStoneDataManager ssdm = new SpecialStoneDataManager(speStoneIdentifier);
        return ssdm.createSpeStone();
    }

    public static class KarCompoundPieceGuiInvHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
