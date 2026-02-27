package vip.mcsj.www.karrefinement.core;

/**
 * 服务生命周期接口
 * 所有可注册到 ServiceRegistry 的服务都应实现此接口
 */
public interface Service {

    /**
     * 初始化服务（加载配置、建立连接等）
     * 在 ServiceRegistry.initializeAll() 中按注册顺序调用
     */
    void initialize();

    /**
     * 关闭服务（释放资源、关闭连接等）
     * 在 ServiceRegistry.shutdownAll() 中按注册逆序调用
     */
    default void shutdown() {
        // 默认空实现，子类按需重写
    }

    /**
     * 重载服务配置
     * 默认行为是先 shutdown 再 initialize
     */
    default void reload() {
        shutdown();
        initialize();
    }
}
