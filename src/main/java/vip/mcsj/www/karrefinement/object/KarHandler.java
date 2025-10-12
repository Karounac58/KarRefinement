package vip.mcsj.www.karrefinement.object;

import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class KarHandler {
    private UUID pUUID;
    private HandlerList handlerList;

    public KarHandler(UUID pUUID, HandlerList handlerList) {
        this.pUUID = pUUID;
        this.handlerList = handlerList;
    }

    public UUID getpUUID() {
        return pUUID;
    }

    public void setpUUID(UUID pUUID) {
        this.pUUID = pUUID;
    }

    public HandlerList getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(HandlerList handlerList) {
        this.handlerList = handlerList;
    }
}
