package vip.mcsj.www.karrefinement.gui;

import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;
import vip.mcsj.www.karrefinement.version.v1122.V1122;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.v1201.V1201;
import vip.mcsj.www.karrefinement.version.v1210.V1210;

public class GuiResource {
//    private MCVersions pv;
    private CustomSounds cp;
    private CustomMaterial cm;
    public GuiResource(){
        init();
    }

    private void init(){
        if(KarRefinement.pv == null){
            KarRefinement.pv = ReflectionUtils.judgeVersion();
        }
    }
    public CustomSounds getSound(){
        switch (KarRefinement.pv){
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
        switch (KarRefinement.pv){
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

    public CustomPath getPath(){
        switch (KarRefinement.pv){
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
