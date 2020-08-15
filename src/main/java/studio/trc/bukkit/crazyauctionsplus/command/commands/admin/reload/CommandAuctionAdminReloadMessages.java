package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminReloadMessages extends VCommand {

	public CommandAuctionAdminReloadMessages() {
		this.setPermission("Reload.SubCommands.Messages");
		this.addSubCommand("messages");
	}

	@Override
	protected CommandType perform(Main plugin) {

		PluginControl.reload(ReloadType.MESSAGES);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();
		}
		Messages.sendMessage(sender, "Reload-Messages");

		return CommandType.SUCCESS;
	}

}
