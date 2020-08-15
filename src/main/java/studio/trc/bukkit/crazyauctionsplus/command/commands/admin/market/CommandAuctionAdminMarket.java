package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import org.bukkit.command.CommandSender;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.util.HashMap;
import java.util.Map;

public class CommandAuctionAdminMarket extends VCommand {

    final static Map<CommandSender, String> marketConfirm = new HashMap();

    public CommandAuctionAdminMarket() {
        this.addSubCommand("market");
        this.setPermission("Admin.SubCommands.Market");
        this.addSubCommand(new CommandAuctionAdminMarketConfirm());
        this.addSubCommand(new CommandAuctionAdminMarketList());
        this.addSubCommand(new CommandAuctionAdminMarketRepricing());
        this.addSubCommand(new CommandAuctionAdminMarketDelete());
        this.addSubCommand(new CommandAuctionAdminMarketDownload());
        this.addSubCommand(new CommandAuctionAdminMarketUpload());
    }

    @Override
    protected CommandType perform(Main plugin) {
        Messages.sendMessage(sender, "Admin-Command.Market.Help");
        return CommandType.SUCCESS;
    }
}
