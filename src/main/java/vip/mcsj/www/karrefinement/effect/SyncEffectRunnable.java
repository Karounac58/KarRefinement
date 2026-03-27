package vip.mcsj.www.karrefinement.effect;


import cn.org.bukkit.craneattribute.CraneAttribute;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Level;

public class SyncEffectRunnable implements Runnable {

    public static ConcurrentHashMap<UUID, Level> tmpMap = new ConcurrentHashMap<>();

    @Override
    public void run() {
        List<LivingEntity> entities = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (LivingEntity le : entities) {
            try {
                sync(le);
            } catch (ClassNotFoundException ex) {
                KarRefinement.instance.getLogger().log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    public void sync(LivingEntity le) throws ClassNotFoundException {
        Level minLevel = KarRefinementAPI.getService(LevelDataManager.class).getMinLevel((Player) le);
        if (minLevel != null && minLevel.suitEffect != null) {
            for (String potionStr : minLevel.suitEffect.potionEffect) {
                String[] args = potionStr.split(" ");
                String potion = args[0];
                int level = Integer.parseInt(args[1]);
                PotionEffectType potionType = PotionEffectType.getByName(potion);
                Bukkit.getScheduler().callSyncMethod(KarRefinement.instance, () -> {
                    le.removePotionEffect(potionType);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potion), 120, level), true);
                    return null;
                });
            }
            if (KarRefinement.ap2Enable) {
                org.serverct.ersha.jd.AttributeAPI.addAttribute((Player) le, "KarRefinement", minLevel.suitEffect.attribute, false);
            }

            if(KarRefinement.ap3Enable) {
                AttributeData attrData = AttributeAPI.getAttrData(le);
                AttributeAPI.addSourceAttribute(attrData,"KarRefinement", minLevel.suitEffect.attribute);
            }
//            if (NewCustomCuiLianPro.sxv2Enable && le instanceof Player) {
//                SXAttributeData data = (SXAttributeData) ReflectUtil.doMethod(SXAttribute.getApi(), "getLoreData", new ParamGroup(null, LivingEntity.class), new ParamGroup(null, Class.forName("github.saukiya.sxattribute.data.condition.SXConditionType")), new ParamGroup(minLevel.suitEffect.attribute));
//                SXAttribute.getApi().setEntityAPIData(SyncEffectRunnable.class, le.getUniqueId(), data);
//            }
            if (KarRefinement.sxv3Enable ) {
                SXAttributeData data = SXAttribute.getApi().loadListData(minLevel.suitEffect.attribute);
                SXAttribute.getApi().setEntityAPIData(SyncEffectRunnable.class, le.getUniqueId(), data);
            }

            if(KarRefinement.caEnabled){
                cn.org.bukkit.craneattribute.api.attribute.data.AttributeData attrData = cn.org.bukkit.craneattribute.api.AttributeAPI.getAttrData(le);
                cn.org.bukkit.craneattribute.api.AttributeAPI.addAttributeSource(attrData,"KarRefinement", minLevel.suitEffect.attribute);
            }

            if (!tmpMap.containsKey(le.getUniqueId())) {
                le.sendMessage(Message.messages.get("effect_start").replace("{level}",minLevel.getMainLore().get(0)));
                tmpMap.put(le.getUniqueId(), minLevel);
            } else {
                Level level = tmpMap.get(le.getUniqueId());
                if (level != minLevel) {
                    le.sendMessage(Message.messages.get("effect_stop").replace("{level}",level.getMainLore().get(0)));
                    tmpMap.remove(le.getUniqueId());
                }
            }
        } else if (tmpMap.containsKey(le.getUniqueId())) {
            le.sendMessage(Message.messages.get("effect_stop").replace("{level}",tmpMap.get(le.getUniqueId()).getMainLore().get(0)));
            tmpMap.remove(le.getUniqueId());
            if (KarRefinement.ap2Enable) {
                org.serverct.ersha.jd.AttributeAPI.deleteAttribute((Player) le, "KarRefinement");
            }
            if(KarRefinement.ap3Enable) {
                AttributeData attrData = AttributeAPI.getAttrData(le);
                AttributeAPI.takeSourceAttribute(attrData,"KarRefinement");
            }
            if (KarRefinement.sxv3Enable) {
                SXAttribute.getApi().removeEntityAPIData(SyncEffectRunnable.class, le.getUniqueId());
            }
        }
    }
}
