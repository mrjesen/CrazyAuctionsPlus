package studio.trc.bukkit.crazyauctionsplus.command.commands;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionHelp extends VCommand {

	public CommandAuctionHelp() {
		this.setPermission("Help");
		this.addSubCommand("help");
	}

	@Override
	protected CommandType perform(Main plugin) {

		for (String message : Messages.getValueList("Help-Menu")) {
            sender.sendMessage(message);
        }

		return CommandType.SUCCESS;
	}

}
