package studio.trc.bukkit.crazyauctionsplus.events;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import studio.trc.bukkit.crazyauctionsplus.database.Storage;

public class Quit 
    implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Storage data = Storage.getPlayer(uuid);
        data.saveData();
    }
}
