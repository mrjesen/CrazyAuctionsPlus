package studio.trc.bukkit.crazyauctionsplus.util;

import studio.trc.bukkit.crazyauctionsplus.util.FileManager.Files;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager.ProtectedConfiguration;

public class MarketGroup
{
    private final String groupname;
    
    private static final ProtectedConfiguration config = Files.CONFIG.getFile();
    
    public MarketGroup(String groupname) {
        this.groupname = groupname;
    }
    
    public int getSellLimit() {
        return config.getInt("Settings.Permissions.Market.Permission-Groups." + groupname + ".Sell-Limit");
    }
    
    public int getBuyLimit() {
        return config.getInt("Settings.Permissions.Market.Permission-Groups." + groupname + ".Buy-Limit");
    }
    
    public int getBidLimit() {
        return config.getInt("Settings.Permissions.Market.Permission-Groups." + groupname + ".Bid-Limit");
    }
    
    public double getSellTaxRate() {
        return config.getDouble("Settings.Permissions.Market.Permission-Groups." + groupname + ".Sell-Tax-Rate");
    }
    
    public double getBuyTaxRate() {
        return config.getDouble("Settings.Permissions.Market.Permission-Groups." + groupname + ".Buy-Tax-Rate");
    }
    
    public double getBidTaxRate() {
        return config.getInt("Settings.Permissions.Market.Permission-Groups." + groupname + ".Bid-Tax-Rate");
    }
    
    public String getGroupName() {
        return groupname;
    }
    
    public boolean exist() {
        return config.get("Settings.Permissions.Market.Permission-Groups." + groupname) != null;
    }
}
