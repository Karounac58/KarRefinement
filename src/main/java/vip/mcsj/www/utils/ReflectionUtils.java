package vip.mcsj.www.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    public static int[] getSpigotVersion(){
        return Arrays.stream(Bukkit.getBukkitVersion().split("-")[0].split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    public static Method getSpigotMethod(String className,String methodName){
        try {
            Class<?> class1 = Class.forName(className);
            return class1.getMethod(methodName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
