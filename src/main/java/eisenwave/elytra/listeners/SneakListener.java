package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SneakListener extends BaseListener<PlayerToggleSneakEvent> implements Listener {

    public SneakListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(final PlayerToggleSneakEvent event) {
        checkPredicates(event,
                this::shouldHandleEvent,
                checkPreference((prefs) -> prefs.launch),
                (playerEvent) -> playerEvent.getPlayer().hasPermission(Permission.LAUNCH.permission),
                (playerEvent) -> {
                    Player player = playerEvent.getPlayer();
                    return player.getEquipment() != null
                            && player.getEquipment().getChestplate() != null
                            && player.getEquipment().getChestplate().getType() == Material.ELYTRA;
                }).then(() -> {
                    Player player = event.getPlayer();

                    // Grounded
                    if (player.getLocation().getY() - (double) player.getLocation().getBlockY() < 0.0001) {
                        if (event.isSneaking()) {
                            playerManager.getPlayer(player).setChargeUpTicks(0);
                        } else {
                            if (playerManager.getPlayer(player).getChargeUpTicks() >= playerManager.getPlayer(player)
                                    .getPrefs().personalChargeupTicks) {
                                if (!this.plugin.getLaunchCooldownManager().canUse(player.getUniqueId())) {
                                    sendCooldownMessage(player);
                                    return;
                                }
                                launch(player);
                            }
                            playerManager.getPlayer(player).setChargeUpTicks(-1);
                        }
                    } else if (player.isGliding() && player.hasPermission(Permission.BOOST_FLYING.permission)
                            && plugin.config().allowCrouchBoost) {
                        playerManager.getPlayer(player).setInfiniteBoosting(event.isSneaking());
                    }
                }).run();
    }

    private void launch(Player player) {
        final Location loc = player.getLocation();
        final Vector dir = loc.getDirection().add(new Vector(0, this.plugin.config().launch, 0));

        player.setVelocity(player.getVelocity().add(dir));
        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
        if (this.plugin.config().launchSound != null)
            player.playSound(loc, this.plugin.config().launchSound.bukkitSound(), 0.1F, 2.0F);
        this.plugin.getLaunchCooldownManager().use(player.getUniqueId());
    }

    private void sendCooldownMessage(Player player) {
        final HashMap<String, String> vars = new HashMap<>();
        final long time = this.plugin.getLaunchCooldownManager().timeUntilUse(player.getUniqueId());
        final long seconds = (time / 1000) % 60;
        final long minutes = (time / (1000 * 60)) % 60;
        final long hours = (time / (60 * 60 * 1000)) % 24;
        vars.put("seconds", String.valueOf(seconds));
        if (seconds == 1) {
            vars.put("seconds_plural", this.plugin.getMessenger().getMessage("second", new HashMap<>()));
        } else {
            vars.put("seconds_plural", this.plugin.getMessenger().getMessage("seconds", new HashMap<>()));
        }
        if (minutes == 0) {
            vars.put("minutes_plural", "");
            vars.put("minutes", "");
        } else if (minutes == 1) {
            vars.put("minutes_plural", this.plugin.getMessenger().getMessage("minute", new HashMap<>()));
            vars.put("minutes", String.valueOf(1));
        } else {
            vars.put("minutes_plural", this.plugin.getMessenger().getMessage("minutes", new HashMap<>()));
            vars.put("minutes", String.valueOf(minutes));
        }
        if (hours == 0) {
            vars.put("hours_plural", "");
            vars.put("hours", "");
        } else if (hours == 1) {
            vars.put("hours_plural", this.plugin.getMessenger().getMessage("hour", new HashMap<>()));
            vars.put("hours", String.valueOf(1));
        } else {
            vars.put("hours_plural", this.plugin.getMessenger().getMessage("hours", new HashMap<>()));
            vars.put("hours", String.valueOf(hours));
        }
        vars.put("username", player.getName());
        this.plugin.getMessenger().sendErrorMessage(player, "cooldown", vars, true);
        playerManager.getPlayer(player).setChargeUpTicks(-1);
    }
}
