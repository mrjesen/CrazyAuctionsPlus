package studio.trc.bukkit.crazyauctionsplus.events;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import studio.trc.bukkit.crazyauctionsplus.api.events.AuctionListEvent;
import studio.trc.bukkit.crazyauctionsplus.api.events.AuctionNewBidEvent;
import studio.trc.bukkit.crazyauctionsplus.api.events.AuctionWinBidEvent;
import studio.trc.bukkit.crazyauctionsplus.utils.AuctionProcess;
import studio.trc.bukkit.crazyauctionsplus.utils.FileManager.ProtectedConfiguration;
import studio.trc.bukkit.crazyauctionsplus.utils.FileManager.Files;
import studio.trc.bukkit.crazyauctionsplus.utils.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.utils.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.ShopType;

public class AuctionEvents
    extends AuctionProcess
    implements Listener
{
    @EventHandler
    public void starting(AuctionListEvent e) {
        if (!e.getShopType().equals(ShopType.BID)) return;
        Player p = e.getPlayer();
        ProtectedConfiguration config = Files.CONFIG.getFile();
        if (config.getBoolean("Settings.Auction-Process-Settings.Starting.Enabled")) {
            String item;
            try {
                item = e.getItem().getItemMeta().hasDisplayName() ? e.getItem().getItemMeta().getDisplayName() : (String) e.getItem().getClass().getMethod("getI18NDisplayName").invoke(e.getItem());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                item = e.getItem().getItemMeta().hasDisplayName() ? e.getItem().getItemMeta().getDisplayName() : e.getItem().getType().toString().toLowerCase().replace("_", " ");
            }
            for (String message : config.getStringList("Settings.Auction-Process-Settings.Starting.Messages")) {
                Bukkit.broadcastMessage(message.replace("%player%", p.getName()).replace("%money%", String.valueOf(e.getMoney())).replace("%item%", item).replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
            }
        }
    }
    
    @EventHandler
    public void bidding(AuctionNewBidEvent e) {
        Player p = e.getPlayer();
        ProtectedConfiguration config = Files.CONFIG.getFile();
        MarketGoods mg = e.getMarketGoods();
        if (config.getBoolean("Settings.Auction-Process-Settings.Bidding.Enabled")) {
            String item;
            try {
                item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : (String) mg.getItem().getClass().getMethod("getI18NDisplayName").invoke(mg.getItem());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : mg.getItem().getType().toString().toLowerCase().replace("_", " ");
            }
            for (String message : config.getStringList("Settings.Auction-Process-Settings.Bidding.Messages")) {
                Bukkit.broadcastMessage(message.replace("%bidder%", p.getName()).replace("%price%", String.valueOf(e.getPrice())).replace("%item%", item).replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
            }
        }
    }
    
    @EventHandler
    public void ending(AuctionWinBidEvent e) {
        Player p = e.getPlayer();
        ProtectedConfiguration config = Files.CONFIG.getFile();
        MarketGoods mg = e.getMarketGoods();
        if (config.getBoolean("Settings.Auction-Process-Settings.Ending.Enabled")) {
            String item;
            try {
                item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : (String) mg.getItem().getClass().getMethod("getI18NDisplayName").invoke(mg.getItem());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : mg.getItem().getType().toString().toLowerCase().replace("_", " ");
            }
            for (String message : config.getStringList("Settings.Auction-Process-Settings.Ending.Messages")) {
                Bukkit.broadcastMessage(message.replace("%bidder%", p.getName()).replace("%price%", String.valueOf(e.getPrice())).replace("%item%", item).replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
            }
        }
    }
    
    @EventHandler
    public void newBid(AuctionNewBidEvent e) {
        Player p = e.getPlayer();
        ProtectedConfiguration config = Files.CONFIG.getFile();
        if (config.getBoolean("Settings.Auction-Process-Settings.Bid-Overtime.Enabled")) {
            MarketGoods mg = e.getMarketGoods();
            for (String time : config.getConfigurationSection("Settings.Auction-Process-Settings.Bid-Overtime.Times").getKeys(false)) {
                try {
                    double timeTillExpire = Double.valueOf(time);
                    if (timeTillExpire * 1000 >= mg.getTimeTillExpire() - System.currentTimeMillis()) {
                        double overtime = config.getDouble("Settings.Auction-Process-Settings.Bid-Overtime.Times." + time + ".Overtime");
                        mg.setTimeTillExpire(mg.getTimeTillExpire() + (long) (overtime * 1000));
                        String item;
                        try {
                            item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : (String) mg.getItem().getClass().getMethod("getI18NDisplayName").invoke(mg.getItem());
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            item = mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : mg.getItem().getType().toString().toLowerCase().replace("_", " ");
                        }
                        if (config.get("Settings.Auction-Process-Settings.Bid-Overtime.Times." + time + ".Messages") != null) {
                            for (String message : config.getStringList("Settings.Auction-Process-Settings.Bid-Overtime.Times." + time + ".Messages")) {
                                Bukkit.broadcastMessage(message.replace("%bidder%", p.getName()).replace("%price%", String.valueOf(e.getPrice())).replace("%item%", item).replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
                            }
                        }
                        break;
                    }
                } catch (NumberFormatException ex) {}
            }
        }
    }
} 
