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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandAuctionAdminPlayerList extends VCommand {

    public CommandAuctionAdminPlayerList() {
        this.addSubCommand("list");
        this.setPermission("Admin.SubCommands.Player.SubCommands.List");
        this.addRequireArg("Player");
        this.addOptionalArg("page");
    }

    @Override
    protected CommandType perform(Main plugin) {
        Player player = argAsPlayer(0, this.player);
        UUID uuid;
        String name;
        if (player != null) {
            uuid = player.getUniqueId();
            name = player.getName();
        } else {
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%player%", argAsString(0));
            Messages.sendMessage(sender, "Admin-Command.Player.List.Please-Wait", placeholders);
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(argAsString(0));
            if (offlineplayer != null) {
                uuid = offlineplayer.getUniqueId();
                name = offlineplayer.getName();
            } else {
                Messages.sendMessage(sender, "Admin-Command.Player.List.Player-Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
        }
        int page = argAsInteger(1, -1);
        if (page != -1) {
            List<ItemMail> list = Storage.getPlayer(uuid).getMailBox();
            if (list.isEmpty()) {
                Messages.sendMessage(sender, "Admin-Command.Player.List.Empty");
                return CommandType.SUCCESS;
            }
            page = 1;
            try {
                page = argAsInteger(1);
            } catch (NumberFormatException ex) {
            }
            int nosp = 9;
            try {
                nosp = Integer.valueOf(Messages.getValue("Admin-Command.Player.List.Number-Of-Single-Page"));
            } catch (NumberFormatException ex) {
            }
            StringBuilder formatList = new StringBuilder();
            int maxpage = ((int) list.size() / nosp) + 1;
            if (maxpage < page) {
                page = maxpage;
            }
            for (int i = page * nosp - nosp; i < list.size() && i < page * nosp; i++) {
                String format = Messages.getValue("Admin-Command.Player.List.Format").replace("%uid%", String.valueOf(list.get(i).getUID()));
                try {
                    format = format.replace("%item%", list.get(i).getItem().getItemMeta().hasDisplayName() ? list.get(i).getItem().getItemMeta().getDisplayName() : (String) list.get(i).getItem().getClass().getMethod("getI18NDisplayName").invoke(list.get(i).getItem()));
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    format = format.replace("%item%", list.get(i).getItem().getItemMeta().hasDisplayName() ? list.get(i).getItem().getItemMeta().getDisplayName() : list.get(i).getItem().getType().toString().toLowerCase().replace("_", " "));
                }
                formatList.append(format);
            }
            Map<String, String> placeholders = new HashMap();
            placeholders.put("%player%", name);
            placeholders.put("%format%", formatList.toString());
            placeholders.put("%page%", String.valueOf(page));
            placeholders.put("%maxpage%", String.valueOf(maxpage));
            placeholders.put("%nextpage%", String.valueOf(page + 1));
            Map<String, Boolean> visible = new HashMap();
            visible.put("{hasnext}", maxpage > page);
            Messages.sendMessage(sender, "Admin-Command.Player.List.Messages", placeholders, visible);
        }


        return null;
    }
}
