package studio.trc.bukkit.crazyauctionsplus.database.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.database.Storage;
import studio.trc.bukkit.crazyauctionsplus.database.engine.SQLiteEngine;
import studio.trc.bukkit.crazyauctionsplus.util.ItemMail;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLiteStorage extends SQLiteEngine implements Storage {
	public static volatile Map<UUID, SQLiteStorage> cache = new HashMap<UUID, SQLiteStorage>();

	private static long lastUpdateTime = System.currentTimeMillis();

	private final UUID uuid;
	private final YamlConfiguration yamlData = new YamlConfiguration();
	private final List<ItemMail> mailBox = new ArrayList<ItemMail>();

	public SQLiteStorage(UUID uuid) {
		this.uuid = uuid;

		try {
			ResultSet rs = super.executeQuery(super.getConnection()
					.prepareStatement("SELECT * FROM " + getItemMailTable() + " WHERE UUID = '" + uuid + "'"));
			if (rs.next()) {
				String yamldata = rs.getString("YamlData");
				yamlData.loadFromString(yamldata);
			} else {
				register(uuid);
			}
		} catch (SQLException ex) {
			if (Main.language.get("SQLite-DataReadingError") != null)
				Main.getInstance().getServer().getConsoleSender().sendMessage(Main.language.getProperty("SQLite-DataReadingError").replace("{error}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null").replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
			try {
				if (super.getConnection().isClosed()) {
					super.repairConnection();
				}
			} catch (SQLException ex1) {
				PluginControl.printStackTrace(ex1);
			}
			PluginControl.printStackTrace(ex);
		} catch (InvalidConfigurationException | NullPointerException ex) {
			if (Main.language.get("PlayerDataFailedToLoad") != null) Main.getInstance().getServer().getConsoleSender().sendMessage(Main.language.getProperty("PlayerDataFailedToLoad").replace("{player}", Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getName() : "null").replace("{error}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null").replace("{prefix}", PluginControl.getPrefix()).replace("&", "§"));
		}

		loadData();
	}

	private void loadData() {
		if (yamlData.get("Name") == null
				|| !yamlData.getString("Name").equals(Bukkit.getOfflinePlayer(uuid).getName())) {
			yamlData.set("Name", Bukkit.getOfflinePlayer(uuid).getName());
			saveData();
		}

		if (yamlData.get("Items") != null) {
			for (String path : yamlData.getConfigurationSection("Items").getKeys(false)) {
				if (yamlData.get("Items." + path) != null) {
					ItemMail im;
					try {
						im = new ItemMail(
								yamlData.get("Items." + path + ".UID") != null
										? yamlData.getLong("Items." + path + ".UID") : Long.valueOf(path),
								uuid,
								yamlData.get("Items." + path + ".Item") != null
										? yamlData.getItemStack("Items." + path + ".Item")
										: new ItemStack(Material.AIR),
								yamlData.getLong("Items." + path + ".Full-Time"),
								yamlData.get("Items." + path + ".Added-Time") != null ? yamlData.getLong("Items." + path + ".Added-Time") : -1,
								yamlData.getBoolean("Items." + path + ".Never-Expire")
						);
					} catch (Exception ex) {
						PluginControl.printStackTrace(ex);
						continue;
					}
					mailBox.add(im);
				}
			}
		}
	}

	@Override
	public String getName() {
		return yamlData.getString("Name");
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	@Override
	public YamlConfiguration getYamlData() {
		return yamlData;
	}

	@Override
	public void saveData() {
		try {
			long i = 1;
			yamlData.set("Items", null);
			for (ItemMail im : mailBox) {
				if (im.getItem() == null || im.getItem().getType().equals(Material.AIR))
					continue;
				yamlData.set("Items." + i + ".Item", im.getItem());
				yamlData.set("Items." + i + ".Full-Time", im.getFullTime());
				yamlData.set("Items." + i + ".Never-Expire", im.isNeverExpire());
				yamlData.set("Items." + i + ".UID", i);
				i++;
			}
			String yaml = yamlData.saveToString();
			PreparedStatement statement = getConnection().prepareStatement(
					"UPDATE " + getItemMailTable() + " SET " + "YamlData = ? " + "WHERE UUID = '" + uuid + "'");
			statement.setString(1, yaml);
			executeUpdate(statement);
		} catch (SQLException ex) {
			PluginControl.printStackTrace(ex);
		}
	}

	@Override
	public List<ItemMail> getMailBox() {
		boolean save = false;
		for (int i = mailBox.size() - 1; i > -1; i--) {
			if (mailBox.get(i).getItem() == null || mailBox.get(i).getItem().getType().equals(Material.AIR)) {
				mailBox.remove(i);
				save = true;
			}
		}
		if (save) saveData();
		return mailBox;
	}

	@Override
	public ItemMail getMail(long uid) {
		for (ItemMail im : mailBox) {
			if (im.getUID() == uid) {
				return im;
			}
		}
		return null;
	}

	@Override
	public void addItem(ItemMail... is) {
		mailBox.addAll(Arrays.asList(is));
		saveData();
	}

	@Override
	public void removeItem(ItemMail... is) {
		mailBox.removeAll(Arrays.asList(is));
		saveData();
	}

	@Override
	public void clearMailBox() {
		mailBox.clear();
		yamlData.set("Items", null);
		saveData();
	}

	@Override
	public int getMailNumber() {
		return mailBox.size();
	}

	@Override
	public long makeUID() {
		long id = 0;
		while (true) { // 循环查找
			id++;
			boolean b = false;
			for (ItemMail im : mailBox) {
				if (im.getUID() == id) {
					b = true;
					break;
				}
			}
			if (b)
				continue;
			break;
		}
		return id;
	}

	private void register(UUID uuid) throws SQLException {
		String name = Bukkit.getOfflinePlayer(uuid) != null ? Bukkit.getOfflinePlayer(uuid).getName() : null;
		if (name == null) {
			name = "null";
		}
		PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO " + getItemMailTable() + "(UUID, Name, YamlData) " + "VALUES(?, ?, ?)");
		statement.setString(1, uuid.toString());
		statement.setString(2, name);
		statement.setString(3, "{}");
		executeUpdate(statement);
		yamlData.set("Name", name);
	}

	public static SQLiteStorage getPlayerData(Player player) {
		if (isItemMailReacquisition() && getUpdateDelay() == 0) {
			return new SQLiteStorage(player.getUniqueId());
		} else {
			SQLiteStorage data = cache.get(player.getUniqueId());
			if (data != null && getUpdateDelay() != 0) {
				if (!isItemMailReacquisition() || System.currentTimeMillis() - lastUpdateTime <= getUpdateDelay() * 1000) {
					return data;
				}
			}
			data = new SQLiteStorage(player.getUniqueId());
			cache.put(player.getUniqueId(), data);
			lastUpdateTime = System.currentTimeMillis();
			return data;
		}
	}

	public static SQLiteStorage getPlayerData(OfflinePlayer player) {
		if (isItemMailReacquisition() && getUpdateDelay() == 0) {
			return new SQLiteStorage(player.getUniqueId());
		} else {
			SQLiteStorage data = cache.get(player.getUniqueId());
			if (data != null && getUpdateDelay() != 0) {
				if (!isItemMailReacquisition() || System.currentTimeMillis() - lastUpdateTime <= getUpdateDelay() * 1000) {
					return data;
				}
			}
			data = new SQLiteStorage(player.getUniqueId());
			cache.put(player.getUniqueId(), data);
			lastUpdateTime = System.currentTimeMillis();
			return data;
		}
	}

	public static SQLiteStorage getPlayerData(UUID uuid) {
		if (isItemMailReacquisition() && getUpdateDelay() == 0) {
			return new SQLiteStorage(uuid);
		} else {
			SQLiteStorage data = cache.get(uuid);
			if (data != null && getUpdateDelay() != 0) {
				if (!isItemMailReacquisition() || System.currentTimeMillis() - lastUpdateTime <= getUpdateDelay() * 1000) {
					return data;
				}
			}
			data = new SQLiteStorage(uuid);
			cache.put(uuid, data);
			lastUpdateTime = System.currentTimeMillis();
			return data;
		}
	}
}