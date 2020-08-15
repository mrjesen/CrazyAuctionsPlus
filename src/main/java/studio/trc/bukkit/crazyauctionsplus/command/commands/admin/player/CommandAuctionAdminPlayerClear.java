package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.database.Storage;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player.CommandAuctionAdminPlayer.itemMailConfirm;

public class CommandAuctionAdminPlayerClear extends VCommand {

    public CommandAuctionAdminPlayerClear() {
        this.addSubCommand("clear");
        this.setPermission("Admin.SubCommands.Player.SubCommands.Clear");
        this.addOptionalArg("Player");
        this.addOptionalArg("market/mail");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (argAsString(0, "").equals("")) {
            Messages.sendMessage(sender, "Admin-Command.Player.Clear.Help");
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
            Messages.sendMessage(sender, "Admin-Command.Player.Clear.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.Clear.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        if (argAsString(1, "").equals("market") || argAsString(1, "").equals("mail")) {
            if (argAsString(1).equals("market")) {
                // market
                GlobalMarket market = GlobalMarket.getMarket();
                if (itemMailConfirm.containsKey(sender) && itemMailConfirm.get(sender).equalsIgnoreCase("ca admin player " + name + " clear market")) {
                    List<MarketGoods> marketGoods = market.getItems();
                    for (int i = marketGoods.size() - 1; i > -1; i--) {
                        if (marketGoods.get(i).getItemOwner().getUUID().equals(uuid)) {
                            marketGoods.remove(i);
                        }
                    }
                    market.saveData();
                    itemMailConfirm.remove(sender);
                    Map<String, String> placeholders = new HashMap();
                    placeholders.put("%player%", name);
                    Messages.sendMessage(sender, "Admin-Command.Player.Clear.Market", placeholders);
                    return CommandType.SUCCESS;
                } else {
                    Messages.sendMessage(sender, "Admin-Command.Player.Confirm.Confirm");
                    itemMailConfirm.put(sender, "ca admin player clear " + name + " market");
                    return CommandType.SUCCESS;
                }
            } else {
                //mail
                if (itemMailConfirm.containsKey(sender) && itemMailConfirm.get(sender).equalsIgnoreCase("ca admin player " + name + " clear mail")) {
                    Storage.getPlayer(uuid).clearMailBox();
                    itemMailConfirm.remove(sender);
                    Map<String, String> placeholders = new HashMap();
                    placeholders.put("%player%", name);
                    Messages.sendMessage(sender, "Admin-Command.Player.Clear.ItemMail", placeholders);
                    return CommandType.SUCCESS;
                } else {
                    Messages.sendMessage(sender, "Admin-Command.Player.Confirm.Confirm");
                    itemMailConfirm.put(sender, "ca admin player clear " + name + " mail");
                    return CommandType.SUCCESS;
                }
            }
        } else {
            Messages.sendMessage(sender, "Admin-Command.Player.Clear.Help");
            return CommandType.SUCCESS;
        }
    }
}
