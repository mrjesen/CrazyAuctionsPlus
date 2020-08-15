package studio.trc.bukkit.crazyauctionsplus.command.commands.admin;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.command.commands.admin.collections.CommandAuctionAdminItemCollection;
import studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market.CommandAuctionAdminMarket;
import studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player.CommandAuctionAdminPlayer;
import studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload.CommandAuctionAdminReload;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdmin extends VCommand {

	public CommandAuctionAdmin() {
		this.setPermission("Admin");
		this.addSubCommand("admin");

		this.addSubCommand(new CommandAuctionAdminBackup());
		this.addSubCommand(new CommandAuctionAdminRollBack());
		this.addSubCommand(new CommandAuctionAdminInfo());
		this.addSubCommand(new CommandAuctionAdminSynchronize());
		this.addSubCommand(new CommandAuctionAdminItemCollection());
		this.addSubCommand(new CommandAuctionAdminMarket());
		this.addSubCommand(new CommandAuctionAdminReload());
		this.addSubCommand(new CommandAuctionAdminPlayer());
		this.addSubCommand(new CommandAuctionAdminPrintStackTrace());
	}

	@Override
	protected CommandType perform(Main plugin) {
		
		 for (String message : Messages.getValueList("Admin-Menu")) {
             sender.sendMessage(message);
         }
		
		return CommandType.SUCCESS;
	}

}
