package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.effect.ScriptRunnable;
import vip.mcsj.www.karrefinement.gui.KarCompoundStoneGui;
import vip.mcsj.www.karrefinement.gui.KarTakeItemGui;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class ReloadCommand extends KarAbstractCommand {

    public ReloadCommand() {
        super("reload","karrefinement.reload","§e/krf reload —— §b重载配置文件",false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }

        reloadConfig();
        sender.sendMessage(Message.messages.get("reload_success"));
        return true;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public void reloadConfig(){
        // 清理统计数据缓存
        PlayerStatsDataManager.clearCache();
        KarRefinement.instance.reloadConfig();
        KarRefinement.instance.initGuiData();
        StoneDataManager.init();
        EquipmentDataManager.init();
        EquipmentDataManager.initForgeData();
        EquipmentDataManager.initTransformData();
        LevelDataManager.init();
        PaperDataManager.init();
        SpecialStoneDataManager.init();
        InfiniteSoulManager.init();
        DUPaperDataManager.init();
        KarCompoundStoneGui.initCompoundData();
        DetachDataManager.init();
        AdhesiveDataManager.init();
        PotionDataManager.init();
        Message.init();
        KarTakeItemGui.initItems();
        FurnaceDataManager.init();
        ScriptRunnable.enbaleScript = KarRefinement.instance.getConfig().getBoolean("settings.enablescript");
        
        // 更新lore版本号，触发所有已淬炼装备的lore更新
        LoreUpdateManager.init();
        LoreUpdateManager.clearAllCooldowns();
    }
}
