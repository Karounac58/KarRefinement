package vip.mcsj.www.karrefinement.main.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.datamanager.AdhesiveDataManager;
import vip.mcsj.www.karrefinement.datamanager.SpecialStoneDataManager;
import vip.mcsj.www.karrefinement.object.Adhesive;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.ItemStackUtils;
import vip.mcsj.www.karrefinement.utils.KarUtils;

import java.util.concurrent.ThreadLocalRandom;

public class AdhesiveListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&  e.getAction() != Action.RIGHT_CLICK_AIR){
            return;
        }
        ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(itemInMainHand);
        if(!nbtItem.hasKey("adhesive")){
            return;
        }
        Adhesive adhesive = AdhesiveDataManager.getAdehesive(itemInMainHand);
        if(adhesive == null){
            return;
        }
        SpecialStoneDataManager ssm = new SpecialStoneDataManager(adhesive.getOriginGem());
        ItemStack originGem = ssm.createSpeStone();
        int i = ItemStackUtils.judgePlayerItemStackAmount(e.getPlayer().getInventory(), originGem);
        if(i < adhesive.getRequiredAmount()){
            e.getPlayer().sendMessage("§c宝石数量不足！");
            return;
        }
        int num = ThreadLocalRandom.current().nextInt(0, 100);
        //成功
        if((99- adhesive.getChance()) < num){
            ItemStackUtils.reducePlayerItemStack(e.getPlayer().getInventory(), originGem,adhesive.getRequiredAmount());
            SpecialStoneDataManager ssm2 = new SpecialStoneDataManager(adhesive.getAfterGem());
            ItemStack afterGem = ssm2.createSpeStone();
            KarUtils.removeItemRefinement(itemInMainHand);
            e.getPlayer().getInventory().addItem(afterGem);
            e.getPlayer().sendMessage("§a成功合成"+afterGem.getItemMeta().getDisplayName());
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            //失败
        }else{
            int num1 = ThreadLocalRandom.current().nextInt(adhesive.getFailedAmount().get(0),adhesive.getFailedAmount().get(1));
            int leaves = adhesive.getRequiredAmount() -  num1 ;
            KarUtils.removeItemRefinement(itemInMainHand);
            ItemStackUtils.reducePlayerItemStack(e.getPlayer().getInventory(), originGem,leaves);
            e.getPlayer().sendMessage("§c合成失败,返还"+num1+"个宝石");
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);

        }

    }

}
