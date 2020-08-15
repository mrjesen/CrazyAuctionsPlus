package studio.trc.bukkit.crazyauctionsplus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import studio.trc.bukkit.crazyauctionsplus.database.engine.MySQLEngine;
import studio.trc.bukkit.crazyauctionsplus.database.engine.SQLiteEngine;
import studio.trc.bukkit.crazyauctionsplus.utils.PluginControl;

public interface DatabaseEngine
{
    /**
     * Get a connection to the database.
     * @return 
     */
    public Connection getConnection();
    
    /**
     * Reload connection parameters.
     */
    public void reloadConnectionParameters();
    
    /**
     * Connection to the database.
     */
    public void connectToTheDatabase();
    
    /**
     * Repair the connection to the database.
     * This method does not work when the connection is valid.
     */
    public void repairConnection();
    
    /**
     * Send SQL statement for update.
     * @param statement 
     */
    public void executeUpdate(PreparedStatement statement);
    
    /**
     * Send SQL statement for update.
     * @param sql
     * @deprecated
     */
    @Deprecated
    public void executeUpdate(String sql);
    
    /**
     * Send SQL statements to get data.
     * @param statement
     * @return 
     */
    public ResultSet executeQuery(PreparedStatement statement);
    
    /**
     * Send SQL statements to get data.
     * @param sql
     * @return
     * @deprecated
     */
    @Deprecated
    public ResultSet executeQuery(String sql);
    
    public static DatabaseEngine getDatabase() {
        if (PluginControl.useMySQLStorage()) {
            return MySQLEngine.getInstance();
        } else if (PluginControl.useSQLiteStorage()) {
            return SQLiteEngine.getInstance();
        } else {
            return null;
        }
    }
}
