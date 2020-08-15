package studio.trc.bukkit.crazyauctionsplus.command.commands.admin.market;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.utils.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.Messages;
import studio.trc.bukkit.crazyauctionsplus.utils.enums.ShopType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAuctionAdminMarketList extends VCommand {

    public CommandAuctionAdminMarketList() {
        this.addSubCommand("list");
        this.setPermission("Admin.SubCommands.Market.SubCommands.List");
        this.setConsoleCanUse(false);
        this.addOptionalArg("page");
    }

    @Override
    protected CommandType perform(Main plugin) {
        GlobalMarket market = GlobalMarket.getMarket();
        List<MarketGoods> list = market.getItems();
        if (list.isEmpty()) {
            Messages.sendMessage(sender, "Admin-Command.Market.List.Empty");
            return CommandType.SUCCESS;
        }
        int page = 1;
        try {
            page = argAsInteger(0,1);
        } catch (NumberFormatException ex) {}
        int nosp = 9;
        try {
            nosp = Integer.valueOf(Messages.getValue("Admin-Command.Market.List.Number-Of-Single-Page"));
        } catch (NumberFormatException ex) {}
        StringBuilder formatList = new StringBuilder();
        for (int i = page * nosp - nosp;i < list.size() && i < page * nosp;i++) {
            String format = Messages.getValue("Admin-Command.Market.List.Format").replace("%uid%", String.valueOf(list.get(i).getUID())).replace("%money%", String.valueOf(list.get(i).getShopType().equals(ShopType.BUY) ? list.get(i).getReward() : list.get(i).getPrice()));
            try {
                format = format.replace("%item%", list.get(i).getItem().getItemMeta().hasDisplayName() ? list.get(i).getItem().getItemMeta().getDisplayName() : (String) list.get(i).getItem().getClass().getMethod("getI18NDisplayName").invoke(list.get(i).getItem()));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                format = format.replace("%item%", list.get(i).getItem().getItemMeta().hasDisplayName() ? list.get(i).getItem().getItemMeta().getDisplayName() : list.get(i).getItem().getType().toString().toLowerCase().replace("_", " "));
            }
            formatList.append(format);
        }
        int maxpage = ((int) list.size() / nosp) + 1;
        Map<String, String> placeholders = new HashMap();
        placeholders.put("%format%", formatList.toString());
        placeholders.put("%page%", String.valueOf(page));
        placeholders.put("%maxpage%", String.valueOf(maxpage));
        placeholders.put("%nextpage%", String.valueOf(page + 1));
        Map<String, Boolean> visible = new HashMap();
        visible.put("{hasnext}", maxpage > page);
        Messages.sendMessage(sender, "Admin-Command.Market.List.Messages", placeholders, visible);
        return CommandType.SUCCESS;
    }
}
