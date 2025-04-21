package vip.mcsj.www.utils;

import org.bukkit.Bukkit;
import vip.mcsj.www.object.MCVersions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    private static int[] getSpigotVersion(){
        return Arrays.stream(Bukkit.getBukkitVersion().split("-")[0].split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    public static MCVersions judgeVersion(){
        int[] spigotVersion = getSpigotVersion();
        if(spigotVersion[1] > 20){
            return MCVersions.v121;
        }else if(spigotVersion[1] < 13){
            return MCVersions.v1122;
        }else{
            return MCVersions.v1201;
        }
    }

    public static Method getSpigotMethod(String className,String methodName,Class<?>...classes){
        try {
            Class<?> class1 = Class.forName(className);
            return class1.getMethod(methodName,classes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Field getSpigotField(String className,String fieldName){
        try {
            Class<?> class1 = Class.forName(className);
            return class1.getField(fieldName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
