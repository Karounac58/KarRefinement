package vip.mcsj.www.effect;

import vip.mcsj.www.version.CustomParticle;
import vip.mcsj.www.version.v1122.V1122;
import vip.mcsj.www.version.v1201.V1201;
import vip.mcsj.www.version.v1210.V1210;
import vip.mcsj.www.object.MCVersions;

import static vip.mcsj.www.main.KarRefinement.pv;
import static vip.mcsj.www.utils.ReflectionUtils.*;

public class ParticleResource {
//    private MCVersions pv;
    private CustomParticle cP;

    public ParticleResource(){
        init();
    }

    private void init(){
        if(pv == null){
            pv = judgeVersion();
        }
    }
    public CustomParticle get(){
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