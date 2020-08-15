package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.database.StorageMethod;
import studio.trc.bukkit.crazyauctionsplus.utils.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.Messages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market.CommandAuctionAdminMarket.marketConfirm;

public class CommandAuctionAdminMarketDownload extends VCommand {

    public CommandAuctionAdminMarketDownload() {
        this.addSubCommand("download");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Download");
    }

    @Override
    protected CommandType perform(Main plugin) {
        GlobalMarket market = GlobalMarket.getMarket();
        if (PluginControl.getMarketStorageMethod().equals(StorageMethod.YAML)) {
            Messages.sendMessage(sender, "Admin-Command.Market.Download.Only-Database-Mode");
            return CommandType.SUCCESS;
        }
        if (marketConfirm.containsKey(sender) && marketConfirm.get(sender).equalsIgnoreCase("ca admin market download")) {
            String fileName = Messages.getValue("Admin-Command.Market.Download.File-Name").replace("%date%", new SimpleDateFormat("yyyy-MM-hh-HH-mm-ss").format(new Date())) + ".yml";
            File dir = new File("plugins/CrazyAuctionsPlus/Download/");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File yamlFile = new File(dir, fileName);
            if (!yamlFile.exists()) {
                try {
                    yamlFile.createNewFile();
                } catch (IOException ex) {}
            }
            try (OutputStream out = new FileOutputStream(yamlFile)) {
                out.write(market.getYamlData().saveToString().getBytes());
            } catch (IOException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%error%", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                Messages.sendMessage(sender, "Admin-Command.Market.Download.Failed", placeholders);
                marketConfirm.remove(sender);
                return CommandType.SUCCESS;
            }
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%path%", "plugins/CrazyAuctionsPlus/Download/" + fileName);
            Messages.sendMessage(sender, "Admin-Command.Market.Download.Succeeded", placeholders);
            marketConfirm.remove(sender);
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Market.Confirm.Confirm");
            marketConfirm.put(sender, "ca admin market download");
            return CommandType.SUCCESS;
        }
    }
}
