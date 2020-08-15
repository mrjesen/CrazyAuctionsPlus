package studio.trc.bukkit.crazyauctionsplus.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.api.events.AuctionListEvent;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.currency.CurrencyManager;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager;
import studio.trc.bukkit.crazyauctionsplus.util.ItemOwner;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;
import studio.trc.bukkit.crazyauctionsplus.util.enums.ShopType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandAuctionBuy extends VCommand {

	public CommandAuctionBuy() {
		this.setConsoleCanUse(false);
		this.addSubCommand("buy");
		this.setPermission("Buy");

		this.addRequireArg("reward");
		this.addOptionalArg("amount");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected CommandType perform(Main plugin) {

		if (PluginControl.isWorldDisabled(player)) {
			Messages.sendMessage(sender, "World-Disabled");
			return CommandType.DEFAULT;
		}

		if (!crazyAuctions.isBuyingEnabled()) {
			Messages.sendMessage(sender, "Buying-Disabled");
			return CommandType.DEFAULT;
		}

		double reward = argAsDouble(0);

		double tax = 0;
		if (!PluginControl.bypassTaxRate(player, ShopType.BUY)) {
			tax = PluginControl.getTaxRate(player, ShopType.BUY);
		}

		if (CurrencyManager.getMoney(player) < reward) {
			HashMap<String, String> placeholders = new HashMap<String, String>();
			placeholders.put("%Money_Needed%", String.valueOf((reward + tax) - CurrencyManager.getMoney(player)));
			placeholders.put("%money_needed%", String.valueOf((reward + tax) - CurrencyManager.getMoney(player)));
			Messages.sendMessage(sender, "Need-More-Money",placeholders);
			return CommandType.DEFAULT;
		}
		if (reward < FileManager.Files.CONFIG.getFile().getDouble("Settings.Minimum-Buy-Reward")) {
			Map<String, String> placeholders = new HashMap<String, String>();
			placeholders.put("%reward%",
					String.valueOf(FileManager.Files.CONFIG.getFile().getDouble("Settings.Minimum-Buy-Reward")));
			Messages.sendMessage(sender, "Buy-Reward-To-Low",placeholders);
			return CommandType.DEFAULT;
		}
		if (reward > FileManager.Files.CONFIG.getFile().getDouble("Settings.Max-Beginning-Buy-Reward")) {
			Map<String, String> placeholders = new HashMap<String, String>();
			placeholders.put("%reward%",
					String.valueOf(FileManager.Files.CONFIG.getFile().getDouble("Settings.Max-Beginning-Buy-Reward")));
			Messages.sendMessage(sender, "Buy-Reward-To-High",placeholders);
			return CommandType.DEFAULT;
		}
		if (!PluginControl.bypassLimit(player, ShopType.BUY)) {
			int limit = PluginControl.getLimit(player, ShopType.BUY);
			if (limit > -1) {
				if (crazyAuctions.getNumberOfPlayerItems(player, ShopType.BUY) >= limit) {
					Map<String, String> placeholders = new HashMap<String, String>();
					placeholders.put("%number%", String.valueOf(limit));
					Messages.sendMessage(sender, "Max-Buying-Items",placeholders);
					return CommandType.DEFAULT;
				}
			}
		}

		int amount = argAsInteger(1, 1);
		if (amount > 64) {
			Messages.sendMessage(sender, "Too-Many-Items");
			return CommandType.DEFAULT;
		}

		UUID owner = player.getUniqueId();
		GlobalMarket market = GlobalMarket.getMarket();
		ItemStack item;
		if (args.length >= 4) {
			try {
				item = new ItemStack(Material.valueOf(args[3].toUpperCase()), amount);
			} catch (IllegalArgumentException ex) {
				Map<String, String> placeholders = new HashMap<String, String>();
				placeholders.put("%Item%", args[3]);
				placeholders.put("%item%", args[3]);
				Messages.sendMessage(sender, "Unknown-Item", placeholders);
				return CommandType.SUCCESS;
			}
		} else if (PluginControl.getItemInHand(player).getType() != Material.AIR) {
			item = PluginControl.getItemInHand(player).clone();
		} else {
			Messages.sendMessage(sender, "CrazyAuctions-Buy");
			return CommandType.SUCCESS;
		}

		 item.setAmount(amount);
         MarketGoods goods = new MarketGoods(
             market.makeUID(),
             ShopType.BUY,
             new ItemOwner(owner, player.getName()),
             item,
             PluginControl.convertToMill(FileManager.Files.CONFIG.getFile().getString("Settings.Buy-Time")),
             PluginControl.convertToMill(FileManager.Files.CONFIG.getFile().getString("Settings.Full-Expire-Time")),
             reward
         );
         market.addGoods(goods);
         Bukkit.getPluginManager().callEvent(new AuctionListEvent(player, ShopType.BUY, item, reward, tax));
         Map<String, String> placeholders = new HashMap<String, String>();
         placeholders.put("%reward%", String.valueOf(reward));
         placeholders.put("%tax%", String.valueOf(tax));
         try {
             placeholders.put("%Item%", item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : (String) item.getClass().getMethod("getI18NDisplayName").invoke(item));
             placeholders.put("%item%", item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : (String) item.getClass().getMethod("getI18NDisplayName").invoke(item));
         } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
             placeholders.put("%Item%", item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase().replace("_", " "));
             placeholders.put("%item%", item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase().replace("_", " "));
         }
		 Messages.sendMessage(sender, "Added-Item-For-Acquisition", placeholders);
         CurrencyManager.removeMoney(player, reward + tax);
		
		return CommandType.SUCCESS;
	}

}
