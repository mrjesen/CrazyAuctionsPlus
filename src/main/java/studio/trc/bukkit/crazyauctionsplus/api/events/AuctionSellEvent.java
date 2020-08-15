package studio.trc.bukkit.crazyauctionsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.crazyauctionsplus.utils.MarketGoods;

/**
 * @author TRCRedstoner
 */
public class AuctionSellEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double price;
    private final MarketGoods mg;
    
    public AuctionSellEvent(Player player, MarketGoods mg, double price) {
        this.player = player;
        this.mg = mg;
        this.price = price;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public MarketGoods getMarketGoods() {
        return mg;
    }
    
    public double getPrice() {
        return price;
    }
    
}