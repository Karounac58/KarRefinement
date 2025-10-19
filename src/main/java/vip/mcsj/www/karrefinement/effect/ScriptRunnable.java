package vip.mcsj.www.karrefinement.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
//import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import vip.mcsj.www.karrefinement.datamanager.LevelDataManager;
import vip.mcsj.www.karrefinement.datamanager.NashornManager;
import vip.mcsj.www.karrefinement.effect.particle.*;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Level;

import javax.script.*;

public class ScriptRunnable implements Runnable {

    public static boolean enbaleScript = true;
    public static HashMap<UUID, HashMap<String, Bindings>> bindingsMap = new HashMap<>();
    public static HashMap<String, ScriptEngine> engineMap = new HashMap<>();

    @Override
    public void run() {
        List<LivingEntity> entities = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (LivingEntity le : entities) {
            sync(le);
        }
    }

    public void sync(LivingEntity le) {
        UUID uuid = le.getUniqueId();
        Level minLevel = LevelDataManager.getMinLevel((Player) le);
        if (minLevel != null && minLevel.suitEffect != null) {
            try {
                if (enbaleScript) {
                    for (String script : minLevel.suitEffect.script) {
                        if (!engineMap.containsKey(script)) {
                            engineMap.put(script, getDefaultScriptEngine(script));
                        }
                        ScriptEngine engine = engineMap.get(script);
                        bindingsMap.putIfAbsent(uuid, new HashMap<>());
                        if (bindingsMap.get(uuid).containsKey(script)) {
                            engine.setBindings(bindingsMap.get(uuid).get(script), ScriptContext.ENGINE_SCOPE);
                        }
                        Invocable invocable = (Invocable) engine;
                        invocable.invokeFunction("onEffectTick", le);
                        bindingsMap.get(uuid).put(script, engine.getBindings(ScriptContext.ENGINE_SCOPE));
                    }
                }
            } catch (NoSuchMethodException | ScriptException | ClassCastException ex) {
                KarRefinement.instance.getLogger().log(java.util.logging.Level.SEVERE, null, ex);
//                enbaleScript = false;
            }
        }
    }

    public ScriptEngine getDefaultScriptEngine(String script) {
        ScriptEngine engine = NashornManager.createScriptEngine();
        try {
            uploadScriptClass(engine, ParticleModel.class);
            uploadScriptClass(engine, ParticleModel2D.class);
            uploadScriptClass(engine, ParticleModel3D.class);
            uploadScriptClass(engine, ParticleModelLine.class);
            uploadScriptClass(engine, ParticleUtil.class);
            engine.eval(script);
        } catch (ScriptException ex) {
            KarRefinement.instance.getLogger().log(java.util.logging.Level.SEVERE, null, ex);
        }
        return engine;
    }

    public static void uploadScriptClass(ScriptEngine engine, Class clazz) throws ScriptException {
        String totalName = clazz.getSimpleName() + "StaticClass";
        String staticClassName = clazz.getSimpleName();
        engine.put(totalName, clazz);
        engine.eval("var " + staticClassName + " = " + totalName + ".static;");
    }
}
