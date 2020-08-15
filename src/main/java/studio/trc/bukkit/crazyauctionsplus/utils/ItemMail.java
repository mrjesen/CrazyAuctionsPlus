package studio.trc.bukkit.crazyauctionsplus.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.trc.bukkit.crazyauctionsplus.utils.FileManager.Files;

/**
 * Used to process player item mail.
 */
public class ItemMail
{
    private final UUID uuid;
    private final ItemStack is;
    private final long fullTime;
    private final boolean neverExpire;
    private final long uid;
    
    public ItemMail(long uid, UUID uuid, ItemStack is, long fullTime, boolean neverExpire) {
        this.uid = uid;
        this.uuid = uuid;
        this.is = is;
        this.fullTime = fullTime;
        this.neverExpire = neverExpire;
    }
    
    public ItemMail(long uid, Player owner, ItemStack is, long fullTime, boolean neverExpire) {
        this.uid = uid;
        this.uuid = owner.getUniqueId();
        this.is = is;
        this.fullTime = fullTime;
        this.neverExpire = neverExpire;
    }
    
    public ItemMail(long uid, OfflinePlayer owner, ItemStack is, long fullTime, boolean neverExpire) {
        this.uid = uid;
        this.uuid = owner.getUniqueId();
        this.is = is;
        this.fullTime = fullTime;
        this.neverExpire = neverExpire;
    }
    
    /**
     * Get ItemStack instance.
     * @return 
     */
    public ItemStack getItem() {
        return is;
    }
    
    /**
     * Get full time.
     * @return 
     */
    public long getFullTime() {
        return fullTime;
    }
    
    /**
     * Get creation time.
     * @return 
     */
    public long getAddedTime() {
        return fullTime - (PluginControl.convertToMill(Files.CONFIG.getFile().getString("Settings.Full-Expire-Time")) - System.currentTimeMillis());
    }
    
    /**
     * Does the item mail expire?
     * @return 
     */
    public boolean isNeverExpire() {
        return neverExpire;
    }
    
    /**
     * Get Player's instance
     * @return 
     */
    public Player getOwner() {
        return Bukkit.getPlayer(uuid);
    }
    
    /**
     * Get UUID.
     * @return 
     */
    public UUID getOwnerUUID() {
        return uuid;
    }
    
    /**
     * Get Item Mail's UID
     * @return 
     */
    public long getUID() {
        return uid;
    }
    
    /**
     * Give Item and delete this item mail.
     */
    public void giveItem() {
        if (Bukkit.getPlayer(uuid) != null) {
            Player p = Bukkit.getPlayer(uuid);
            p.getInventory().addItem(is.clone());
            is.setType(Material.AIR);
        }
    }
}
