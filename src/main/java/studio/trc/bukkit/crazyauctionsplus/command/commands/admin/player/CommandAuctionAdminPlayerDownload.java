package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.Storage;
import studio.trc.bukkit.crazyauctionsplus.database.StorageMethod;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player.CommandAuctionAdminPlayer.itemMailConfirm;

public class CommandAuctionAdminPlayerDownload extends VCommand {

    public CommandAuctionAdminPlayerDownload() {
        this.addSubCommand("download");
        this.setPermission("Admin.SubCommands.Player.SubCommands.Download");
        this.addRequireArg("Player");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (argAsString(0, "").equals("")) {
            Messages.sendMessage(sender, "Admin-Command.Player.Download.Help");
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
            Messages.sendMessage(sender, "Admin-Command.Player.Download.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.Download.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        if (PluginControl.getItemMailStorageMethod().equals(StorageMethod.YAML)) {
            Messages.sendMessage(sender, "Admin-Command.Player.Download.Only-Database-Mode");
            return CommandType.SUCCESS;
        }
        if (itemMailConfirm.containsKey(sender) && itemMailConfirm.get(sender).equalsIgnoreCase("ca admin player " + name + " download")) {
            String fileName = FileManager.Files.CONFIG.getFile().getString("Settings.Download.PlayerData").replace("%player%", name).replace("%uuid%", uuid.toString()).replace("%date%", new SimpleDateFormat("yyyy-MM-hh-HH-mm-ss").format(new Date()));
            File file = new File(fileName);
            if (file.getParent() != null) {
                new File(file.getParent()).mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                }
            }
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(Storage.getPlayer(uuid).getYamlData().saveToString().getBytes());
            } catch (IOException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                Messages.sendMessage(sender, "Admin-Command.Player.Download.Failed", placeholders);
                itemMailConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%path%", fileName);
            placeholders.put("%player%", name);
            Messages.sendMessage(sender, "Admin-Command.Player.Download.Succeeded", placeholders);
            itemMailConfirm.remove(sender);
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Player.Confirm.Confirm");
            itemMailConfirm.put(sender, "ca admin player " + name + " download");
            return CommandType.SUCCESS;
        }
    }
}
