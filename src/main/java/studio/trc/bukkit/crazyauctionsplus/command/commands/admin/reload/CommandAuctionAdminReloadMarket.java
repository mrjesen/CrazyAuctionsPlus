package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.reload;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminReloadMarket extends VCommand {

	public CommandAuctionAdminReloadMarket() {
		this.setPermission("Reload.SubCommands.Market");
		this.addSubCommand("market");
	}

	@Override
	protected CommandType perform(Main plugin) {
		
        PluginControl.reload(ReloadType.MARKET);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
		Messages.sendMessage(sender, "Reload-Market");
		
		return CommandType.SUCCESS;
	}

}
