package vip.mcsj.www.karrefinement.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.main.listener.KarTakeItemGuiListener;
import vip.mcsj.www.karrefinement.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KarTakeItemGui {
    public static List<ItemStack> guiItems = new ArrayList<>();

    public static void initItems(){
        if(!guiItems.isEmpty()){
            guiItems.clear();
        }
        //放入淬炼石
        List<Stone> stones = new ArrayList<>(StoneDataManager.stones.values());
        for (Stone stone : stones) {
            StoneDataManager sdm = new StoneDataManager(stone.getNbt());
            guiItems.add(sdm.createStone());
        }
        //放入保护符
        List<ProtectPaper> papers = new ArrayList<>(PaperDataManager.papers.values());
        for (ProtectPaper paper : papers) {
            PaperDataManager pdm = new PaperDataManager(paper.getIdentifier());
            guiItems.add(pdm.createProtectedPaper());
        }
        //放入特殊石头
        List<SpeStone> speStones = new ArrayList<>(SpecialStoneDataManager.speStones.values());
        for (SpeStone speStone : speStones) {
            SpecialStoneDataManager sdm = new SpecialStoneDataManager(speStone.getIdentifier());
            guiItems.add(sdm.createSpeStone());
        }
        //放入无限耐久精魂
        List<InfiniteSoul> souls = new ArrayList<>(InfiniteSoulManager.infiniteSouls.values());
        for (InfiniteSoul infiniteSoul : souls) {
            InfiniteSoulManager ism = new InfiniteSoulManager(infiniteSoul.getIdentifier());
            guiItems.add(ism.createInfiniteSoul());
        }
        //放入直升符
        Set<Map.Entry<String, DirectUpgradePaper>> entries = DUPaperDataManager.duPapers.entrySet();
        for (Map.Entry<String, DirectUpgradePaper> entry : entries) {
            guiItems.add(DUPaperDataManager.createDUPaper(entry.getKey()));
        }
        //放入拆卸刀和保护符碎片
        guiItems.add(DetachDataManager.createPaperDetachItem());
        Set<String> detaches1 = DetachDataManager.paperDetachs.keySet();
        for (String detach : detaches1) {
            DetachDataManager ddm = new DetachDataManager(detach);
            guiItems.add(ddm.createPaperPiece());
        }

        guiItems.add(DetachDataManager.createSpeStoneDetachItem());
        //放入拆卸刀和宝石碎片
        Set<String> detaches2 = DetachDataManager.speStoneDetachs.keySet();
        for (String s : detaches2) {
            DetachDataManager ddm1 = new DetachDataManager(s);
            guiItems.add(ddm1.createSpeStonePiece());
        }

        Set<String> adhesive = AdhesiveDataManager.adhesives.keySet();
        for (String s : adhesive) {
            guiItems.add(AdhesiveDataManager.createAdhesiveItem(s));
        }
    }

    public static void openKarTakeItemGui(Player p){
        CustomInventory cInv = new CustomInventory(guiItems);
        KarTakeItemGuiListener.setCustomInventory(p,cInv);
        p.openInventory(cInv.getInventory());
    }
}
