package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.GUI;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandAuctionAdminPlayerView extends VCommand {

    public CommandAuctionAdminPlayerView() {
        this.addSubCommand("view");
        this.setPermission("Admin.SubCommands.Player.SubCommands.View");
        this.setConsoleCanUse(false);
        this.addRequireArg("Player");
        this.addOptionalArg("UID");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (argAsString(0, "").equals("")) {
            Messages.sendMessage(sender, "Admin-Command.Player.View.Help");
            return CommandType.SUCCESS;
        }
        Player player = argAsPlayer(0, this.player);
        UUID uuid;
        String name;
        if (player != null) {
            uuid = player.getUniqueId();
            name = player.getName();
        } else {
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%player%", argAsString(0));
            Messages.sendMessage(sender, "Admin-Command.Player.View.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.Delete.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        GUI.openPlayersMail((Player) sender, 1, uuid);
        Map<String, String> placeholders = new HashMap();
        placeholders.put("%player%", name);
        Messages.sendMessage(sender, "Admin-Command.Player.View.Succeeded", placeholders);
        return CommandType.SUCCESS;
    }
}
