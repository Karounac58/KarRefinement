package vip.mcsj.www.karrefinement.service.refinement;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.api.model.IRefinementStrategy;
import vip.mcsj.www.karrefinement.api.model.IStone;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StrategyRegistry {
    private final List<IRefinementStrategy> strategies = new ArrayList<>();

    public void register(IRefinementStrategy strategy) {
        strategies.removeIf(s -> s.getId().equals(strategy.getId()));
        strategies.add(strategy);
        KarRefinement.instance.getLogger().info(strategy.getId() + "淬炼策略已注册!");
        // 按优先级降序排序
        strategies.sort(Comparator.comparingInt(IRefinementStrategy::getPriority).reversed());
    }

    public Optional<IRefinementStrategy> findBestStrategy(OfflinePlayer player, ItemStack equipment, IStone stone) {
//        System.out.println(strategies.stream()
//                .filter(s -> {
//                    System.out.println(s.getId() + ":" + s.canHandle(player, equipment, stone));
//                    return s.canHandle(player, equipment, stone);
//                })
//                .findFirst().get().getId());
        return strategies.stream()
                .filter(s -> s.canHandle(player, equipment, stone))
                .findFirst();
    }
}