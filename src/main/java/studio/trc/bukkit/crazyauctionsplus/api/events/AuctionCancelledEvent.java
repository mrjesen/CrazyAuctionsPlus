package studio.trc.bukkit.crazyauctionsplus.api.events;

import studio.trc.bukkit.crazyauctionsplus.utils.enums.ShopType;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.CancelledReason;
import studio.trc.bukkit.crazyauctionsplus.utils.MarketGoods;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author BadBones69
 *
 * This event is fired when a player's item is cancelled.
 *
 */
public class AuctionCancelledEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer offlinePlayer;
    private Player onlinePlayer;
    private final boolean isOnline;
    private final MarketGoods mg;
    private final CancelledReason reason;
    private final ShopType type;
    
    public AuctionCancelledEvent(OfflinePlayer offlinePlayer, MarketGoods mg, CancelledReason reason, ShopType type) {
        this.offlinePlayer = offlinePlayer;
        this.mg = mg;
        this.isOnline = false;
        this.reason = reason;
        this.type = type;
    }
    
    public AuctionCancelledEvent(Player onlinePlayer, MarketGoods mg, CancelledReason reason, ShopType type) {
        this.onlinePlayer = onlinePlayer;
        this.mg = mg;
        this.isOnline = true;
        this.reason = reason;
        this.type = type;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
    
    public Player getOnlinePlayer() {
        return onlinePlayer;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public MarketGoods getMarketGoods() {
        return mg;
    }
    
    public ShopType getShopType() {
        return type;
    }
    
    public CancelledReason getReason() {
        return reason;
    }
    
}