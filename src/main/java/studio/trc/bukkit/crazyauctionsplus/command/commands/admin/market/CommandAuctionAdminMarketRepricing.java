package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;
import studio.trc.bukkit.crazyauctionsplus.util.enums.ShopType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandAuctionAdminMarketRepricing extends VCommand {

    public CommandAuctionAdminMarketRepricing() {
        this.addSubCommand("repricing");
        this.setPermission("Admin.SubCommands.Market.SubCommands.Repricing");
        this.addOptionalArg("UID");
        this.addOptionalArg("money");
    }

    @Override
    protected CommandType perform(Main plugin) {
        long uid = argAsLong(0,-23333333333L);
        double money = argAsDouble(0,-2.333);
        GlobalMarket market = GlobalMarket.getMarket();
        if (uid == -23333333333L && money == -2.333 ) {
            Messages.sendMessage(sender, "Admin-Command.Market.Repricing.Help");
            return CommandType.SUCCESS;
        } else {
            try {
                uid = argAsLong(0);
            } catch (NumberFormatException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%arg%", argAsString(0));
                Messages.sendMessage(sender, "Admin-Command.Market.Repricing.Not-A-Valid-Number", placeholders);
                return CommandType.SUCCESS;
            }
            try {
                money = argAsDouble(1);
            } catch (NumberFormatException ex) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%arg%", argAsString(1));
                Messages.sendMessage(sender, "Admin-Command.Market.Repricing.Not-A-Valid-Number", placeholders);
                return CommandType.SUCCESS;
            }
            MarketGoods goods = market.getMarketGoods(uid);
            if (goods == null) {
                Map<String, String> placeholders = new HashMap();
                placeholders.put("%uid%", String.valueOf(uid));
                Messages.sendMessage(sender, "Admin-Command.Market.Repricing.Not-Exist", placeholders);
                return CommandType.SUCCESS;
            }
            Map<String, String> placeholders = new HashMap();
            try {
                placeholders.put("%item%", goods.getItem().getItemMeta().hasDisplayName() ? goods.getItem().getItemMeta().getDisplayName() : (String) goods.getItem().getClass().getMethod("getI18NDisplayName").invoke(goods.getItem()));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                placeholders.put("%item%", goods.getItem().getItemMeta().hasDisplayName() ? goods.getItem().getItemMeta().getDisplayName() : (String) goods.getItem().getType().toString().toLowerCase().replace("_", " "));
            }
            placeholders.put("%uid%", String.valueOf(uid));
            placeholders.put("%money%", String.valueOf(money));
            if (goods.getShopType().equals(ShopType.BUY)) {
                goods.setReward(money);
            } else {
                goods.setPrice(money);
            }
            Messages.sendMessage(sender, "Admin-Command.Market.Repricing.Succeeded", placeholders);
            return CommandType.SUCCESS;
        }
    }
}
