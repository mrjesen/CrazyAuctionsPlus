package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.command.CommandSender;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.util.HashMap;
import java.util.Map;


public class CommandAuctionAdminPlayer extends VCommand {
    final static Map<CommandSender, String> itemMailConfirm = new HashMap();

    public CommandAuctionAdminPlayer() {
        this.addSubCommand("player");
        this.setPermission("Admin.SubCommands.Player");
        this.addSubCommand(new CommandAuctionAdminPlayerConfirm());
        this.addSubCommand(new CommandAuctionAdminPlayerClear());
        this.addSubCommand(new CommandAuctionAdminPlayerDelete());
        this.addSubCommand(new CommandAuctionAdminPlayerDownload());
        this.addSubCommand(new CommandAuctionAdminPlayerList());
        this.addSubCommand(new CommandAuctionAdminPlayerUpload());
        this.addSubCommand(new CommandAuctionAdminPlayerView());
    }

    @Override
    protected CommandType perform(Main plugin) {
        Messages.sendMessage(sender, "Admin-Command.Player.Help");
        return CommandType.SUCCESS;
    }
}
