package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public class TickListener extends BaseListener<PlayerEvent> {

    public TickListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    public void onTick() {
        for (final SuperElytraPlayer sePlayer : playerManager) {
            try {
                final Player player = sePlayer.getPlayer();
                if (!player.isGliding()) {
                    sePlayer.setBoostTicks(0);
                }
                if (!player.hasPermission("superelytra.launch")) {
                    return;
                }
                if (!shouldHandle(player)) continue;
                if (!player.isOnGround() || !sePlayer.isChargingLaunch()) continue;

                int time = sePlayer.getChargeUpTicks();
                sePlayer.setChargeUpTicks(++time);

                final Location loc = player.getLocation();
                final World world = player.getWorld();

                world.spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0.2F, 0.2F, 0.2F, 0.0F); // radius 30
                if (time % 3 == 0) {
                    if (this.plugin.config().chargeSound != null)
                        player.playSound(player.getLocation(), this.plugin.config().chargeSound.bukkitSound(), 0.1F, 0.1F);
                    if (time >= this.plugin.config().chargeupTicks) {
                        world.spawnParticle(Particle.FLAME, loc, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                        if (this.plugin.config().readySound != null)
                            player.playSound(player.getLocation(), this.plugin.config().readySound.bukkitSound(), 0.1F, 0.1F);
                    }
                }
            } catch (final Exception ex) {
                this.errorLogger.handleError(ex);
            }
        }
    }
}
