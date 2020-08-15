package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminReloadDatabase extends VCommand {

	public CommandAuctionAdminReloadDatabase() {
		this.setPermission("Reload.SubCommands.Database");
		this.addSubCommand("database");
	}

	@Override
	protected CommandType perform(Main plugin) {
		
		PluginControl.reload(ReloadType.DATABASE);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
		Messages.sendMessage(sender, "Reload-Database");
		
		return CommandType.SUCCESS;
	}

}
