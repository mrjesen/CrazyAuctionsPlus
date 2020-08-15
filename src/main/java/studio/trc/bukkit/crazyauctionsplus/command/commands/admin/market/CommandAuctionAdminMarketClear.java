package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import org.bukkit.Bukkit;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.Messages;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market.CommandAuctionAdminMarket.marketConfirm;

public class CommandAuctionAdminMarketClear extends VCommand {

    public CommandAuctionAdminMarketClear() {
        this.addSubCommand("clear");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Clear");
    }

    @Override
    protected CommandType perform(Main plugin) {
        GlobalMarket market = GlobalMarket.getMarket();
        if (marketConfirm.containsKey(sender) && marketConfirm.get(sender).equalsIgnoreCase("ca admin market clear")) {
            market.clearGlobalMarket();
            marketConfirm.remove(sender);
            Messages.sendMessage(sender, "Admin-Command.Market.Clear");
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Market.Confirm.Confirm");
            marketConfirm.put(sender, "ca admin market clear");
            return CommandType.SUCCESS;
        }
    }
}
