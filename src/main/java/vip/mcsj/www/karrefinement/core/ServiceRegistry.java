package vip.mcsj.www.karrefinement.core;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 轻量级服务注册中心
 * 管理所有服务的注册、获取和生命周期
 */
public class ServiceRegistry {

    private final Map<Class<?>, Object> services = new LinkedHashMap<>();
    private final Logger logger;

    public ServiceRegistry(Logger logger) {
        this.logger = logger;
    }

    /**
     * 注册一个服务实例
     * @param type 服务类型（接口或实现类的Class）
     * @param instance 服务实例
     */
    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    /**
     * 获取一个已注册的服务
     * @param type 服务类型
     * @return 服务实例，若未注册则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) services.get(type);
    }

    /**
     * 按注册顺序初始化所有实现了 Service 接口的服务
     */
    public void initializeAll() {
        for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            Object instance = entry.getValue();
            if (instance instanceof Service) {
                try {
                    ((Service) instance).initialize();
//                    logger.info("服务 [" + entry.getKey().getSimpleName() + "] 初始化完成");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "服务 [" + entry.getKey().getSimpleName() + "] 初始化失败!", e);
                }
            }
        }
    }

    /**
     * 按注册逆序关闭所有实现了 Service 接口的服务
     */
    public void shutdownAll() {
        List<Map.Entry<Class<?>, Object>> entries = new ArrayList<>(services.entrySet());
        Collections.reverse(entries);
        for (Map.Entry<Class<?>, Object> entry : entries) {
            Object instance = entry.getValue();
            if (instance instanceof Service) {
                try {
                    ((Service) instance).shutdown();
//                    logger.info("服务 [" + entry.getKey().getSimpleName() + "] 已关闭");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "服务 [" + entry.getKey().getSimpleName() + "] 关闭失败!", e);
                }
            }
        }
    }

    /**
     * 重载所有服务（按注册顺序重新初始化）
     */
    public void reloadAll() {
        for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            Object instance = entry.getValue();
            if (instance instanceof Service) {
                try {
                    ((Service) instance).reload();
//                    logger.info("服务 [" + entry.getKey().getSimpleName() + "] 重载完成");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "服务 [" + entry.getKey().getSimpleName() + "] 重载失败!", e);
                }
            }
        }
    }

    /**
     * 获取所有已注册服务的数量
     */
    public int size() {
        return services.size();
    }
}
