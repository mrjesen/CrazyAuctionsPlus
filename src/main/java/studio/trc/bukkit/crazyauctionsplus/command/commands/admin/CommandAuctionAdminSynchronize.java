package studio.trc.bukkit.crazyauctionsplus.command.commands.admin;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminSynchronize extends VCommand {

	public CommandAuctionAdminSynchronize() {
		this.setPermission("Admin.SubCommands.Synchronize");
		this.addSubCommand("synchronize");
	}

	@Override
	protected CommandType perform(Main plugin) {
		
		if (FileManager.isSyncing()) {
			Messages.sendMessage(sender, "Admin-Command.Synchronize.Syncing");
            return CommandType.DEFAULT;
        }
		Messages.sendMessage(sender, "Admin-Command.Synchronize.Starting");
        FileManager.synchronize(sender);
		
		return CommandType.SUCCESS;
	}

}
