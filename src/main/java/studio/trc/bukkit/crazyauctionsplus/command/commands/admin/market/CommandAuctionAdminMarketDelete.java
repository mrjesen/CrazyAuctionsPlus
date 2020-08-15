package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandAuctionAdminMarketDelete extends VCommand {

    public CommandAuctionAdminMarketDelete() {
        this.addSubCommand("delete");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Repricing");
        this.addOptionalArg("UID");
    }

    @Override
    protected CommandType perform(Main plugin) {
        long uid = argAsLong(0,-23333333333L);
        GlobalMarket market = GlobalMarket.getMarket();
        if (uid == -23333333333L) {
            Messages.sendMessage(sender, "Admin-Command.Market.Delete.Help");
            return CommandType.SUCCESS;
        } else {
            try {
                uid = argAsLong(0);
            } catch (NumberFormatException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%arg%", argAsString(0));
                Messages.sendMessage(sender, "Admin-Command.Market.Delete.Not-A-Valid-Number", placeholders);
                return CommandType.SUCCESS;
            }
            MarketGoods goods = market.getMarketGoods(uid);
            if (goods == null) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%uid%", String.valueOf(uid));
                Messages.sendMessage(sender, "Admin-Command.Market.Delete.Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
            Map<String, String> placeholders = new HashMap();
            try {
                placeholders.put("%item%", goods.getItem().getItemMeta().hasDisplayName() ? goods.getItem().getItemMeta().getDisplayName() : (String) goods.getItem().getClass().getMethod("getI18NDisplayName").invoke(goods.getItem()));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                placeholders.put("%item%", goods.getItem().getItemMeta().hasDisplayName() ? goods.getItem().getItemMeta().getDisplayName() : (String) goods.getItem().getType().toString().toLowerCase().replace("_", " "));
            }
            placeholders.put("%uid%", String.valueOf(uid));
            market.removeGoods(uid);
            Messages.sendMessage(sender, "Admin-Command.Market.Delete.Succeeded", placeholders);
            return CommandType.SUCCESS;
        }
    }
}
