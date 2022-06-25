package eisenwave.elytra.listeners;


import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener extends BaseListener<PlayerJoinEvent> implements Listener {
    public JoinListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.checkPredicates(event).then(() -> playerManager.loadPlayer(event.getPlayer())).run();
    }
}
