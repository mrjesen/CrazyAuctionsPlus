package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.StorageMethod;
import studio.trc.bukkit.crazyauctionsplus.database.engine.MySQLEngine;
import studio.trc.bukkit.crazyauctionsplus.database.engine.SQLiteEngine;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player.CommandAuctionAdminPlayer.itemMailConfirm;

public class CommandAuctionAdminPlayerUpload extends VCommand {

    public CommandAuctionAdminPlayerUpload() {
        this.addSubCommand("upload");
        this.setPermission("Admin.SubCommands.Player.SubCommands.Upload");
        this.addRequireArg("Player");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (argAsString(0, "").equals("")) {
            Messages.sendMessage(sender, "Admin-Command.Player.Upload.Help");
            return CommandType.SUCCESS;
        }
        Player player = argAsPlayer(0, this.player);
        UUID uuid;
        String name;
        if (player != null) {
            uuid = player.getUniqueId();
            name = player.getName();
        } else {
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%player%", argAsString(0));
            Messages.sendMessage(sender, "Admin-Command.Player.Upload.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.Upload.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        if (PluginControl.getItemMailStorageMethod().equals(StorageMethod.YAML)) {
            Messages.sendMessage(sender, "Admin-Command.Player.Download.Only-Database-Mode");
            return CommandType.SUCCESS;
        }
        if (itemMailConfirm.containsKey(sender) && itemMailConfirm.get(sender).equalsIgnoreCase("ca admin player " + name + " upload")) {
            String fileName = FileManager.Files.CONFIG.getFile().getString("Settings.Upload.PlayerData").replace("%player%", name).replace("%uuid%", uuid.toString()).replace("%date%", new SimpleDateFormat("yyyy-MM-hh-HH-mm-ss").format(new Date()));
            File file = new File(fileName);
            if (!file.exists()) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%file%", fileName);
                Messages.sendMessage(sender, "Admin-Command.Player.Upload.File-Not-Exist", placeholders);
                itemMailConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            FileConfiguration config = new YamlConfiguration();
            try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
                config.load(reader);
            } catch (IOException | InvalidConfigurationException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                Messages.sendMessage(sender, "Admin-Command.Player.Upload.Failed", placeholders);
                itemMailConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            switch (PluginControl.getMarketStorageMethod()) {
                case MySQL: {
                    MySQLEngine engine = MySQLEngine.getInstance();
                    try {
                        PreparedStatement statement = engine.getConnection().prepareStatement("UPDATE " + MySQLEngine.getDatabaseName() + "." + MySQLEngine.getItemMailTable() + " SET "
                                + "YamlData = ? WHERE UUID = ?");
                        statement.setString(1, config.saveToString());
                        statement.setString(2, uuid.toString());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        Map<String, String> placeholders = new HashMap();
                        placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        Messages.sendMessage(sender, "Admin-Command.Player.Upload.Failed", placeholders);
                        itemMailConfirm.remove(sender);
                        return CommandType.SUCCESS;
                    }
                    break;
                }
                case SQLite: {
                    SQLiteEngine engine = SQLiteEngine.getInstance();
                    try {
                        PreparedStatement statement = engine.getConnection().prepareStatement("UPDATE " + SQLiteEngine.getItemMailTable() + " SET "
                                + "YamlMarket = ? WHERE UUID = ?");
                        statement.setString(1, config.saveToString());
                        statement.setString(2, uuid.toString());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        Map<String, String> placeholders = new HashMap();
                        placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        Messages.sendMessage(sender, "Admin-Command.Player.Upload.Failed", placeholders);
                        itemMailConfirm.remove(sender);
                        return CommandType.SUCCESS;
                    }
                    break;
                }
            }
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%file%", fileName);
            placeholders.put("%player%", name);
            Messages.sendMessage(sender, "Admin-Command.Player.Upload.Succeeded", placeholders);
            itemMailConfirm.remove(sender);
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Player.Confirm.Confirm");
            itemMailConfirm.put(sender, "ca admin player " + name + " upload");
            return CommandType.SUCCESS;
        }

    }
}
