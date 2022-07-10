package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class MoveListener extends BaseListener<PlayerMoveEvent> implements Listener {

    public MoveListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        checkPredicates(event,
                 this::shouldHandleEvent,
                (playerEvent) -> playerManager.getPlayer(playerEvent.getPlayer()) != null
            ).then(() -> {
            final Player player = event.getPlayer();
            final SuperElytraPlayer superElytraPlayer = playerManager.getPlayer(player);

            if (player.hasPermission(Permission.GLIDE.permission)
                    && player.isGliding()
                    && superElytraPlayer.isEnabled()
                    && superElytraPlayer.preferences.boost
                    && player.getLocation().getPitch() > this.plugin.config().maxGlideAngle
            ) {
                final Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
                player.setVelocity(player.getVelocity().add(unitVector.multiply(this.plugin.config().speed)));
            }

            if (player.hasPermission(Permission.BOOST.permission)
                    && superElytraPlayer.isBoosting()) {
                final Vector boostMomentum = player.getVelocity().normalize().multiply(this.plugin.config().boostModifier);
                player.setVelocity(player.getLocation().getDirection().add(boostMomentum));
                superElytraPlayer.decrementBoostTicks();
            }
        }).run();
    }
}
