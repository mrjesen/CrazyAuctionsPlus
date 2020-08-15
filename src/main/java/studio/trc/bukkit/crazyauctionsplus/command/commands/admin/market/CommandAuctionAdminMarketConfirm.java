package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import org.bukkit.Bukkit;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.Messages;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market.CommandAuctionAdminMarket.marketConfirm;

public class CommandAuctionAdminMarketConfirm extends VCommand {

    public CommandAuctionAdminMarketConfirm() {
        this.addSubCommand("confirm");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Confirm");
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (marketConfirm.containsKey(sender)) {
            Bukkit.dispatchCommand(sender, marketConfirm.get(sender));
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Market.Confirm.Invalid");
            return CommandType.SUCCESS;
        }
    }
}
