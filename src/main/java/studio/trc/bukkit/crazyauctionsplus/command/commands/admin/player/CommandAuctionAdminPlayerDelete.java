package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.Storage;
import studio.trc.bukkit.crazyauctionsplus.util.ItemMail;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandAuctionAdminPlayerDelete extends VCommand {

    public CommandAuctionAdminPlayerDelete() {
        this.addSubCommand("delete");
        this.setPermission("Admin.SubCommands.Player.SubCommands.Delete");
        this.addRequireArg("Player");
        this.addOptionalArg("UID");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (argAsString(0, "").equals("")) {
            Messages.sendMessage(sender, "Admin-Command.Player.Delete.Help");
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
            Messages.sendMessage(sender, "Admin-Command.Player.Delete.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.Delete.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        Storage playerdata = Storage.getPlayer(uuid);
        long uid;
        try {
            uid = Long.valueOf(argAsString(1));
        } catch (NumberFormatException ex) {
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%arg%", argAsString(1));
            Messages.sendMessage(sender, "Admin-Command.Player.Delete.Not-A-Valid-Number", placeholders);
            return CommandType.SUCCESS;
        }
        ItemMail mail = playerdata.getMail(uid);
        if (mail == null) {
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%uid%", String.valueOf(uid));
            Messages.sendMessage(sender, "Admin-Command.Player.Delete.Not-Exist", placeholders);
            return CommandType.SUCCESS;
        }
        Map<String, String> placeholders = new HashMap();
        try {
            placeholders.put("%item%", mail.getItem().getItemMeta().hasDisplayName() ? mail.getItem().getItemMeta().getDisplayName() : (String) mail.getItem().getClass().getMethod("getI18NDisplayName").invoke(mail.getItem()));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            placeholders.put("%item%", mail.getItem().getItemMeta().hasDisplayName() ? mail.getItem().getItemMeta().getDisplayName() : (String) mail.getItem().getType().toString().toLowerCase().replace("_", " "));
        }
        placeholders.put("%uid%", String.valueOf(uid));
        placeholders.put("%player%", name);
        playerdata.removeItem(mail);
        Messages.sendMessage(sender, "Admin-Command.Player.Delete.Succeeded", placeholders);

        return CommandType.SUCCESS;
    }
}
