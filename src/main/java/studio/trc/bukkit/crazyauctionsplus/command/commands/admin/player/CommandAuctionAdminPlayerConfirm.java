package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import static studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player.CommandAuctionAdminPlayer.itemMailConfirm;

public class CommandAuctionAdminPlayerConfirm extends VCommand {

    public CommandAuctionAdminPlayerConfirm() {
        this.addSubCommand("confirm");
        this.setPermission("Admin.SubCommands.Player.SubCommands.Confirm");
    }

    @Override
    protected CommandType perform(Main plugin) {

        if (itemMailConfirm.containsKey(sender)) {
            Bukkit.dispatchCommand(sender, itemMailConfirm.get(sender));
            return CommandType.SUCCESS;
        } else {
            Messages.sendMessage(sender, "Admin-Command.Player.Confirm.Invalid");
            return CommandType.SUCCESS;
        }
    }
}
