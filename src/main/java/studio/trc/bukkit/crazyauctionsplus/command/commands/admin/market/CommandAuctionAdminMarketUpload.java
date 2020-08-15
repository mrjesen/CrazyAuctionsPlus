package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.database.StorageMethod;
import studio.trc.bukkit.crazyauctionsplus.database.engine.MySQLEngine;
import studio.trc.bukkit.crazyauctionsplus.database.engine.SQLiteEngine;
import studio.trc.bukkit.crazyauctionsplus.utils.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.Messages;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market.CommandAuctionAdminMarket.marketConfirm;

public class CommandAuctionAdminMarketUpload extends VCommand {

    public CommandAuctionAdminMarketUpload() {
        this.addSubCommand("upload");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Upload");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (PluginControl.getMarketStorageMethod().equals(StorageMethod.YAML)) {
            Messages.sendMessage(sender, "Admin-Command.Market.Upload.Only-Database-Mode");
            return CommandType.SUCCESS;
        }
        if (marketConfirm.containsKey(sender) && marketConfirm.get(sender).equalsIgnoreCase("ca admin market upload")) {
            String fileName = Messages.getValue("Admin-Command.Market.Upload.File-Name") + ".yml";
            File file = new File("plugins/CrazyAuctionsPlus/", fileName);
            if (!file.exists()) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%file%", "plugins/CrazyAuctionsPlus/" + Messages.getValue("Admin-Command.Market.Upload.File-Name") + ".yml");
                Messages.sendMessage(sender, "Admin-Command.Market.Upload.File-Not-Exist", placeholders);
                marketConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            FileConfiguration config = new YamlConfiguration();
            try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
                config.load(reader);
            } catch (IOException | InvalidConfigurationException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                Messages.sendMessage(sender, "Admin-Command.Market.Upload.Failed", placeholders);
                marketConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            switch (PluginControl.getMarketStorageMethod()) {
                case MySQL: {
                    MySQLEngine engine = MySQLEngine.getInstance();
                    try {
                        PreparedStatement statement = engine.getConnection().prepareStatement("UPDATE " + MySQLEngine.getDatabaseName() + "." + MySQLEngine.getMarketTable() + " SET "
                                + "YamlMarket = ?");
                        statement.setString(1, config.saveToString());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        Map<String, String> placeholders = new HashMap();
                        placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        Messages.sendMessage(sender, "Admin-Command.Market.Upload.Failed", placeholders);
                        marketConfirm.remove(sender);
                        return CommandType.SUCCESS;
                    }
                    break;
                }
                case SQLite: {
                    SQLiteEngine engine = SQLiteEngine.getInstance();
                    try {
                        PreparedStatement statement = engine.getConnection().prepareStatement("UPDATE " + SQLiteEngine.getMarketTable() + " SET "
                                + "YamlMarket = ?");
                        statement.setString(1, config.saveToString());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        Map<String, String> placeholders = new HashMap();
                        placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        Messages.sendMessage(sender, "Admin-Command.Market.Upload.Failed", placeholders);
                        marketConfirm.remove(sender);
                        return CommandType.SUCCESS;
                    }
                    break;
                }
            }
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%file%", fileName);
            Messages.sendMessage(sender, "Admin-Command.Market.Upload.Succeeded", placeholders);
            marketConfirm.remove(sender);
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Market.Confirm.Confirm");
            marketConfirm.put(sender, "ca admin market upload");
            return CommandType.SUCCESS;
        }
    }
}
