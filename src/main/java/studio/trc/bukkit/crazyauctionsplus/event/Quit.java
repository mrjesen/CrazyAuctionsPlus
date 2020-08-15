package studio.trc.bukkit.crazyauctionsplus.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import studio.trc.bukkit.crazyauctionsplus.database.Storage;

import java.util.UUID;

public class Quit
        implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Storage data = Storage.getPlayer(uuid);
        data.saveData();
    }
}