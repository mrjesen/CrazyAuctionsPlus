package studio.trc.bukkit.crazyauctionsplus.database;

import org.bukkit.configuration.file.YamlConfiguration;
import studio.trc.bukkit.crazyauctionsplus.database.market.MySQLMarket;
import studio.trc.bukkit.crazyauctionsplus.database.market.SQLiteMarket;
import studio.trc.bukkit.crazyauctionsplus.database.market.YamlMarket;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;

import java.util.List;

public interface GlobalMarket
{
    /**
     * Get data for all products in the market
     * @return 
     */
    public List<MarketGoods> getItems();
    
    /**
     * Get goods with uid.
     * @param uid
     * @return 
     */
    public MarketGoods getMarketGoods(long uid);
    
    /**
     * Adding new products to the market
     * @param goods 
     */
    public void addGoods(MarketGoods goods);
    
    /**
     * Remove specified items from the market
     * @param goods 
     */
    public void removeGoods(MarketGoods goods);
    
    /**
     * Remove item with specified UID
     * @param uid 
     */
    public void removeGoods(long uid);

    /**
     * Clear global market.
     */
    public void clearGlobalMarket();


    /**
     * Save market data
     */
    public void saveData();
    
    /**
     * Reload market data from the database
     */
    public void reloadData();
    
    /**
     * Make a new UID.
     * @return 
     */
    public long makeUID();
    
    /**
     * @return 
     */
    public YamlConfiguration getYamlData();
    
    public static GlobalMarket getMarket() {
        if (PluginControl.useSplitDatabase()) {
            switch (PluginControl.getMarketStorageMethod()) {
                case MySQL: {
                    if (PluginControl.useMySQLStorage()) {
                        return MySQLMarket.getInstance();
                    } else {
                        return YamlMarket.getInstance();
                    }
                }
                case SQLite: {
                    if (PluginControl.useSQLiteStorage()) {
                        return SQLiteMarket.getInstance();
                    } else {
                        return YamlMarket.getInstance();
                    }
                }
                case YAML: {
                    return YamlMarket.getInstance();
                }
            }
        } else if (PluginControl.useMySQLStorage()) {
            return MySQLMarket.getInstance();
        } else if (PluginControl.useSQLiteStorage()) {
            return SQLiteMarket.getInstance();
        } else {
            return YamlMarket.getInstance();
        }
        return null;
    }
}
