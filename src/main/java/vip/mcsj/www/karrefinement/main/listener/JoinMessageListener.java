package vip.mcsj.www.karrefinement.main.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import vip.mcsj.www.karrefinement.datamanager.EquipmentDataManager;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.object.JoinMessage;
import vip.mcsj.www.karrefinement.object.Level;

import java.util.List;

public class JoinMessageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        int minLevel = EquipmentDataManager.getMinLevelFromEquipments(player);
        JoinMessage joinMessage = EquipmentDataManager.joinMessage.get(String.valueOf(minLevel));
        if(joinMessage == null){
            return;
        }
        List<String> messages = PlaceholderAPI.setPlaceholders(player,joinMessage.getMessage());
        if(player.hasPermission(joinMessage.getPermission())){
            for (String s : messages) {
                Bukkit.broadcastMessage(s.replace("{player}", player.getName()));
            }
        }
    }
}
