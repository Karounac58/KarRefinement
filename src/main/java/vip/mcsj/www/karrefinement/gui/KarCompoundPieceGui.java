package vip.mcsj.www.karrefinement.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.DetachDataManager;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Detach;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.Arrays;
import java.util.List;

public class KarCompoundPieceGui {

    public static void init(Inventory inv) {
        List<Integer> whitePanes = Arrays.asList(0,2,3,4,5,6,8,9,17,18,19,20,21,23,24,25,26);
        List<Integer> blackPanes = Arrays.asList(11,12,13,14,15);
        int originSlot = 1;
        int afterSlot = 7;
        int beginButton = 22;

        ItemStack whitePaneItem = KarRefinement.cm.getItems().get(3);
        ItemStack blackPaneItem = KarRefinement.cm.getItems().get(2);
        ItemStack redPaneItem = KarRefinement.cm.getItems().get(0);
        ItemStack originSign =  KarRefinement.cm.getItems().get(6).clone();
        ItemStack afterSign = KarRefinement.cm.getItems().get(6).clone();


        ItemMeta redPaneItemMeta = redPaneItem.getItemMeta();
        redPaneItemMeta.setDisplayName("§a§l开始合成");
        redPaneItem.setItemMeta(redPaneItemMeta);
        ItemMeta originSignItemMeta = originSign.getItemMeta();
        originSignItemMeta.setDisplayName("§c§l☼§d§l合成材料§c§l☼");
        originSign.setItemMeta(originSignItemMeta);

        ItemMeta afterSignItemMeta = afterSign.getItemMeta();
        afterSignItemMeta.setDisplayName("§c§l☼§d§l合成结果§c§l☼");
        afterSign.setItemMeta(afterSignItemMeta);

        for (int i = 0; i < whitePanes.size(); i++) {
            inv.setItem(whitePanes.get(i), whitePaneItem);
        }

        for (int i = 0; i < blackPanes.size(); i++) {
            inv.setItem(blackPanes.get(i), blackPaneItem);
        }

        inv.setItem(originSlot, originSign);
        inv.setItem(afterSlot, afterSign);
        inv.setItem(beginButton, redPaneItem);
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
        if(judgeInventoryClickMethod(itemPieces, p)) {
            int level = DetachDataManager.getPaperPieceLevel(itemPieces);

            String paperIdentifier = PaperDataManager.getPaperIdentifier(level);

            Detach detach = DetachDataManager.paperDetachs.get(paperIdentifier);

            int compound = detach.getCompound();
            KarUtils.removeItemRefinement(itemPieces, compound);
            PaperDataManager pdm = new PaperDataManager(paperIdentifier);
            return pdm.createProtectedPaper();
        }
        return null;
    }

    public static class KarCompoundPieceGuiInvHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
