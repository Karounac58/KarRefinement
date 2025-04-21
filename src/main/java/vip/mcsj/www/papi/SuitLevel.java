package vip.mcsj.www.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import vip.mcsj.www.datamanager.EffectDataManager;
import vip.mcsj.www.main.KarRefinement;

public class SuitLevel extends PlaceholderExpansion {

    private final KarRefinement plugin; //

    public SuitLevel(KarRefinement plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "KarRefinement";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("suitlevel")) {
            if(!player.isOnline()) return null;
            int i = EffectDataManager.judgeRightLevelEquipment(player.getPlayer());
            switch(i){
                case 0:
                    return "0";
                case 1:
                    return "6";
                case 2:
                    return "9";
                case 3:
                    return "12";
                case 4:
                    return "15";
                case 5:
                    return "18";
            }
        }
        return null; //
    }
}