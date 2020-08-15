package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminReloadConfig extends VCommand {

	public CommandAuctionAdminReloadConfig() {
		this.setPermission("Reload.SubCommands.Config");
		this.addSubCommand("config");
	}

	@Override
	protected CommandType perform(Main plugin) {
		
        PluginControl.reload(ReloadType.CONFIG);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
		Messages.sendMessage(sender, "Reload-Config");
		
		return CommandType.SUCCESS;
	}

}
