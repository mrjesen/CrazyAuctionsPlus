package studio.trc.bukkit.crazyauctionsplus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import studio.trc.bukkit.crazyauctionsplus.command.CommandManager;
import studio.trc.bukkit.crazyauctionsplus.currency.Vault;
import studio.trc.bukkit.crazyauctionsplus.database.GlobalMarket;
import studio.trc.bukkit.crazyauctionsplus.database.engine.MySQLEngine;
import studio.trc.bukkit.crazyauctionsplus.database.engine.SQLiteEngine;
import studio.trc.bukkit.crazyauctionsplus.database.storage.MySQLStorage;
import studio.trc.bukkit.crazyauctionsplus.database.storage.SQLiteStorage;
import studio.trc.bukkit.crazyauctionsplus.event.*;
import studio.trc.bukkit.crazyauctionsplus.util.CrazyAuctions;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager;
import studio.trc.bukkit.crazyauctionsplus.util.MarketGoods;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.ReloadType;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl.RollBackMethod;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

	public static FileManager fileManager = FileManager.getInstance();
	public static CrazyAuctions crazyAuctions = CrazyAuctions.getInstance();
	private final CommandManager commandManager = new CommandManager(this);

	public static Main main;
	public static Properties language = new Properties();

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		long time = System.currentTimeMillis();
		main = this;
		String lang = Locale.getDefault().toString();
		if (lang.equalsIgnoreCase("zh_cn")) {
			try {
				language.load(getClass().getResourceAsStream("/Languages/Chinese.properties"));
			} catch (IOException ex) {
			}
		} else {
			try {
				language.load(getClass().getResourceAsStream("/Languages/English.properties"));
			} catch (IOException ex) {
			}
		}
		if (language.get("LanguageLoaded") != null)
			getServer().getConsoleSender().sendMessage(language.getProperty("LanguageLoaded").replace("&", "§"));

		if (!getDescription().getName().equals("CrazyAuctionsPlus")) {
			if (language.get("PluginNameChange") != null)
				getServer().getConsoleSender().sendMessage(language.getProperty("PluginNameChange").replace("&", "§"));
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		PluginControl.reload(ReloadType.ALL);
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			if (language.get("DependentPluginExist") != null)
				getServer().getConsoleSender().sendMessage(language.getProperty("DependentPluginExist")
						.replace("{plugin}", "Vault").replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
		} else {
			if (language.get("DependentPluginNotExist") != null)
				getServer().getConsoleSender().sendMessage(language.getProperty("DependentPluginNotExist")
						.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
			return;
		}
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new Join(), this);
		pm.registerEvents(new Quit(), this);
		pm.registerEvents(new GUIAction(), this);
		pm.registerEvents(new EasyCommand(), this);
		pm.registerEvents(new ShopSign(), this);
		pm.registerEvents(new AuctionEvents(), this);

		commandManager.registerCommands();

		startCheck();
		Vault.setupEconomy();
		if (!PluginControl.useMySQLStorage() && !PluginControl.useSQLiteStorage()) {
			PluginControl.updateCacheData();
		}
		if (language.get("PluginEnabledSuccessfully") != null)
			getServer().getConsoleSender()
					.sendMessage(language.getProperty("PluginEnabledSuccessfully")
							.replace("{time}", String.valueOf(System.currentTimeMillis() - time))
							.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
	}

	/*
	 * Send message to console
	 */
	public void log(String message) {
		getServer().getConsoleSender().sendMessage(message);
	}

	@Override
	public void onDisable() {
		int file = 0;
		Bukkit.getScheduler().cancelTask(file);
		Bukkit.getOnlinePlayers().forEach((p) -> {
			p.closeInventory();
		});
		GlobalMarket.getMarket().saveData();
		if (PluginControl.useMySQLStorage()) {
			try {
				if (MySQLEngine.getInstance().getConnection() != null
						&& !MySQLEngine.getInstance().getConnection().isClosed()) {
					MySQLStorage.cache.values().forEach((storage) -> {
						storage.saveData();
					});
					if (language.get("MySQL-DataSave") != null)
						getServer().getConsoleSender().sendMessage(language.getProperty("MySQL-DataSave")
								.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
				}
			} catch (Exception ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (PluginControl.useSQLiteStorage()) {
			try {
				if (SQLiteEngine.getInstance().getConnection() != null
						&& !SQLiteEngine.getInstance().getConnection().isClosed()) {
					SQLiteStorage.cache.values().forEach((storage) -> {
						storage.saveData();
					});
					if (language.get("SQLite-DataSave") != null)
						getServer().getConsoleSender().sendMessage(language.getProperty("SQLite-DataSave")
								.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
				}
			} catch (Exception ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		asyncRun = false;
		if (PluginControl.automaticBackup()) {
			try {
				if (language.get("AutomaticBackupStarting") != null)
					getServer().getConsoleSender().sendMessage(language.getProperty("AutomaticBackupStarting")
							.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
				RollBackMethod.backup();
				if (language.get("AutomaticBackupDone") != null)
					getServer().getConsoleSender().sendMessage(language.getProperty("AutomaticBackupDone")
							.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
			} catch (Exception ex) {
				if (language.get("AutomaticBackupFailed") != null)
					getServer().getConsoleSender().sendMessage(language.getProperty("AutomaticBackupFailed")
							.replace("{error}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null")
							.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
			}
		}
	}

	private boolean asyncRun = true;
	private Thread RepricingTimeoutCheckThread;
	private Thread DataUpdateThread;

	private void startCheck() {
		DataUpdateThread = new Thread(() -> {
			boolean fault = false;
			while (asyncRun && PluginControl.isGlobalMarketAutomaticUpdate()) {
				try {
					Thread.sleep((long) (PluginControl.getGlobalMarketAutomaticUpdateDelay() * 1000));
					PluginControl.updateCacheData();
					if (fault) {
						if (language.get("CacheUpdateReturnsToNormal") != null)
							getServer().getConsoleSender()
									.sendMessage(language.getProperty("CacheUpdateReturnsToNormal")
											.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
						fault = false;
					}
				} catch (Exception ex) {
					if (language.get("CacheUpdateError") != null)
						getServer().getConsoleSender().sendMessage(language.getProperty("CacheUpdateError")
								.replace("{error}",
										ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null")
								.replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
					fault = true;
					PluginControl.printStackTrace(ex);
				}
			}
		});
		DataUpdateThread.start();
		RepricingTimeoutCheckThread = new Thread(() -> {
			while (asyncRun) {
				GUIAction.repricing.keySet().stream().filter((value) -> (System.currentTimeMillis() >= Long.valueOf(GUIAction.repricing.get(value)[1].toString()))).forEachOrdered((value) -> {
							try {
								MarketGoods mg  = (MarketGoods) GUIAction.repricing.get(value)[0];
								Player p = Bukkit.getPlayer(value);
								if (p != null) {
									Map<String, String> placeholders = new HashMap();
									try {
										placeholders.put("%item%", mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : (String) mg.getItem().getClass().getMethod("getI18NDisplayName").invoke(mg.getItem()));
									} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
										placeholders.put("%item%", mg.getItem().getItemMeta().hasDisplayName() ? mg.getItem().getItemMeta().getDisplayName() : mg.getItem().getType().toString().toLowerCase().replace("_", " "));
									}

									Messages.sendMessage(p, "Repricing-Undo", placeholders);
							}
							GUIAction.repricing.remove(p.getUniqueId());
						} catch (ClassCastException ex) {}
					});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		RepricingTimeoutCheckThread.start();
	}

	/**
	 * @return the commandManager
	 */
	public CommandManager getCommandManager() {
		return commandManager;
	}

}