package vip.mcsj.www.karrefinement.main.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoneInteractProbListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e){

        if(!StoneDataManager.enableStoneChnace){
            return;
        }

        if(e.getPlayer().getInventory().getItemInMainHand() == null || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR){
            return;
        }

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR){
            return;
        }

        if(StoneDataManager.isStoneLegal(e.getPlayer().getInventory().getItemInMainHand())){
            e.setCancelled(true);
            Stone stone = StoneDataManager.getStone(e.getPlayer().getInventory().getItemInMainHand());
            List<Double> stoneChance = stone.getProbability();
            List<String> stoneChanceString = new ArrayList<>();
            String formatter = Message.messages.get("stone_interact_chance_formatter");
            for (int i = 0; i < stoneChance.size(); i++) {
                stoneChanceString.add(formatter
                        .replace("{level}", String.valueOf(i+1))
                        .replace("{chance}", String.valueOf(stoneChance.get(i))));
            }

            List<String> showMsg = Message.showStoneChance;
            showMsg =  showMsg.stream().map(s -> s
                    .replace("{stone}",stone.getName())).collect(Collectors.toList());
            showMsg = ListUtils.replaceEquals(showMsg,"{chance}",stoneChanceString);

            for (String s : showMsg) {
                e.getPlayer().sendMessage(s);
            }
        }
    }
}
