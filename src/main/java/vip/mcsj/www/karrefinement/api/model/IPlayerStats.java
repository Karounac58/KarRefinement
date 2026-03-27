package vip.mcsj.www.karrefinement.api.model;

import java.util.UUID;

public interface IPlayerStats {
    UUID getPlayerUUID();

    int getTotalAttempts();

    int getHighestLevel();

    int getTotalSuccesses();

    int getTotalFailures();

    int getCurrentSuitLevel();


}
