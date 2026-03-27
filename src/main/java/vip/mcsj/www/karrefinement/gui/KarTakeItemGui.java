package vip.mcsj.www.karrefinement.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.main.listener.KarTakeItemGuiListener;
import vip.mcsj.www.karrefinement.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KarTakeItemGui {
    public static List<ItemStack> guiItems = new ArrayList<>();

    public static void initItems(){
        if(!guiItems.isEmpty()){
            guiItems.clear();
        }
        //放入淬炼石
        List<Stone> stones = new ArrayList<>(StoneDataManager.stones.values());
        for (Stone stone : stones) {
            guiItems.add(KarRefinementAPI.createStone(stone.getNbt()));
        }
        //放入保护符
        List<ProtectPaper> papers = new ArrayList<>(PaperDataManager.papers.values());
        for (ProtectPaper paper : papers) {
            guiItems.add(KarRefinementAPI.createPaper(paper.getIdentifier()));
        }
        //放入特殊石头
        List<SpeStone> speStones = new ArrayList<>(SpecialStoneDataManager.speStones.values());
        for (SpeStone speStone : speStones) {
            guiItems.add(KarRefinementAPI.createSpeStone(speStone.getIdentifier()));
        }
        //放入无限耐久精魂
        List<InfiniteSoul> souls = new ArrayList<>(InfiniteSoulManager.infiniteSouls.values());
        for (InfiniteSoul infiniteSoul : souls) {
            guiItems.add(KarRefinementAPI.createSoul(infiniteSoul.getIdentifier()));
        }
        //放入直升符
        Set<Map.Entry<String, DirectUpgradePaper>> entries = DUPaperDataManager.duPapers.entrySet();
        for (Map.Entry<String, DirectUpgradePaper> entry : entries) {
//            guiItems.add(DUPaperDataManager.create(entry.getKey()));
            guiItems.add(KarRefinementAPI.createDUPaper(entry.getKey()));
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
            guiItems.add(KarRefinementAPI.createAdhesive(s));
        }

        Set<String> strings = PotionDataManager.potions.keySet();
        for (String s : strings) {
            guiItems.add(KarRefinementAPI.createPotion(s));
        }
        if(FurnaceDataManager.furnaceEnabled) {
            Set<String> furnaces = FurnaceDataManager.furnaces.keySet();
            for (String furnace : furnaces) {
                guiItems.add(KarRefinementAPI.createFurnace(furnace));
            }
        }
    }

    public static void openKarTakeItemGui(Player p){
        CustomInventory cInv = new CustomInventory(guiItems);
        KarTakeItemGuiListener.setCustomInventory(p,cInv);
        p.openInventory(cInv.getInventory());
    }
}
