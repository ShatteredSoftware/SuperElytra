package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener extends BaseListener<PlayerQuitEvent> implements Listener {
    public QuitListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        this.checkPredicates(event).then(() -> playerManager.removePlayer(event.getPlayer())).run();
    }
}
