package vip.mcsj.www.karrefinement.datamanager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;

public class NashornManager {

    public static ScriptEngine createScriptEngine() {
        String javaVersion = System.getProperty("java.version");

        if (javaVersion.startsWith("1.8")) {
            // JDK8 - 使用内置Nashorn
            return createJDK8Nashorn();
        } else {
            // JDK11+ - 尝试加载外部Nashorn
            return createExternalNashorn();
        }
    }

    private static ScriptEngine createJDK8Nashorn() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            return manager.getEngineByName("nashorn");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JDK8 Nashorn engine", e);
        }
    }

    private static ScriptEngine createExternalNashorn() {
        try {
            // 使用反射加载外部Nashorn，避免编译时依赖
            Class<?> nashornashornClass = Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
            Object factory = nashornashornClass.newInstance();
            Method method = nashornashornClass.getMethod("getScriptEngine");
            return (ScriptEngine) method.invoke(factory);
        } catch (Exception e) {
            // 回退到GraalVM JavaScript
            return createGraalJSEngine();
        }
    }

    private static ScriptEngine createGraalJSEngine() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("graal.js");
            if (engine != null) {
                return engine;
            }

            // 最后的回退方案
            return manager.getEngineByName("javascript");
        } catch (Exception e) {
            throw new RuntimeException("No available JavaScript engine found", e);
        }
    }

    public static boolean isJDK8() {
        return System.getProperty("java.version").startsWith("1.8");
    }
}