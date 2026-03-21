package vip.mcsj.www.karrefinement.service.placeholder;

import org.bukkit.plugin.Plugin;
import vip.mcsj.www.karrefinement.api.placeholder.PlaceholderProvider;
import vip.mcsj.www.karrefinement.api.placeholder.PlaceholderRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PAPI 变量注册中心默认实现
 */
public class DefaultPlaceholderRegistry implements PlaceholderRegistry {

    private final List<PlaceholderProvider> providers = new CopyOnWriteArrayList<>();

    @Override
    public void register(PlaceholderProvider provider) {
        providers.add(provider);
    }

    @Override
    public void unregisterAll(Plugin plugin) {
        providers.removeIf(p -> p.getPlugin().equals(plugin));
    }

    @Override
    public List<PlaceholderProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }
}
