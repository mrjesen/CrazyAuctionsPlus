package studio.trc.bukkit.crazyauctionsplus.database;

import java.util.List;

import studio.trc.bukkit.crazyauctionsplus.utils.ItemMail;

public interface ItemMailBox
{
    /**
     * Get the player's item mailbox
     * @return 
     */
    public List<ItemMail> getMailBox();
    
    /**
     * Add new item mail to player's mailbox
     * @param im 
     */
    public void addItem(ItemMail... im);
    
    /**
     * Remove the specified item email from the player's item mailbox
     * @param im
     */
    public void removeItem(ItemMail... im);
    
    /**
     * Empty player's item mailbox
     */
    public void clearMailBox();
    
    /**
     * Upload cached item mailbox data to the database.
     */
//    public void uploadMailBox();
    
    /**
     * Get the player's item email count
     * @return 
     */
    public int getMailNumber();
    
    /**
     * Make a new UID.
     * @return 
     */
    public long makeUID();
}
