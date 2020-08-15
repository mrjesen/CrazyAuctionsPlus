package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.collections;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.ItemCollection;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.util.HashMap;
import java.util.Map;

public class CommandAuctionAdminItemCollectionAdd extends VCommand {

	public CommandAuctionAdminItemCollectionAdd() {
		this.addSubCommand("add");
		this.setPermission("Admin.SubCommands.ItemCollection.SubCommands.Add");
		this.setConsoleCanUse(false);
		this.addRequireArg("name");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected CommandType perform(Main plugin) {

		if (player.getItemInHand() == null) {
			Messages.sendMessage(sender, "Admin-Command.ItemCollection.Add.Doesnt-Have-Item-In-Hand");
			return CommandType.DEFAULT;
		}
		if (ItemCollection.addItem(player.getItemInHand(), argAsString(0))) {
			Map<String, String> placeholders = new HashMap<String, String>();
			placeholders.put("%item%", argAsString(0));
			Messages.sendMessage(sender, "Admin-Command.ItemCollection.Add.Successfully", placeholders);
		} else {
			Messages.sendMessage(sender, "Admin-Command.ItemCollection.Add.Already-Exist");
		}

		return CommandType.SUCCESS;
	}

}
