package vip.mcsj.www.gui;

import org.bukkit.entity.Player;
import vip.mcsj.www.object.MCVersions;
import vip.mcsj.www.version.CustomMaterial;
import vip.mcsj.www.version.CustomParticle;
import vip.mcsj.www.version.CustomSounds;
import vip.mcsj.www.version.v1122.V1122;
import vip.mcsj.www.version.v1201.V1201;
import vip.mcsj.www.version.v1210.V1210;

import static vip.mcsj.www.utils.ReflectionUtils.judgeVersion;

public class GuiResource {
    private MCVersions pv;
    private CustomSounds cp;
    private CustomMaterial cm;
    public GuiResource(){
        init();
    }

    private void init(){
        if(pv == null){
            pv = judgeVersion();
        }
    }
    public CustomSounds getSound(){
        switch (pv){
            case v1122:
                return new V1122();
            case v1201:
                return new V1201();
            case v121:
                return new V1210();
            default:
                return null;
        }
    }

    public CustomMaterial getMaterial(){
        switch (pv){
            case v1122:
                return new V1122();
            case v1201:
                return new V1201();
            case v121:
                return new V1210();
            default:
                return null;
        }
    }
}
