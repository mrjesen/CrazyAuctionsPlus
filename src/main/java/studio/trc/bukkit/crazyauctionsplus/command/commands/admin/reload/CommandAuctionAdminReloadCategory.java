package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminReloadCategory extends VCommand {

	public CommandAuctionAdminReloadCategory() {
		this.setPermission("Reload.SubCommands.Category");
		this.addSubCommand("category");
	}

	@Override
	protected CommandType perform(Main plugin) {

		PluginControl.reload(ReloadType.CATEGORY);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();
		}
		Messages.sendMessage(sender, "Reload-Category");

		return CommandType.SUCCESS;
	}

}
