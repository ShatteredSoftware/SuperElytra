package eisenwave.elytra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import io.sentry.Sentry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class SuperElytraListener implements Listener {

    private final static String
            PERMISSION_LAUNCH = "superelytra.launch",
            PERMISSION_GLIDE = "superelytra.glide",
            PERMISSION_BOOST = "superelytra.boost";

    private final transient SuperElytraPlugin plugin;

    private final HashSet<String> handledExceptions = new HashSet<>();

    public SuperElytraListener(SuperElytraPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean shouldCancel(Player player) {
        // Worlds are in blacklist mode
        if (plugin.config().worldBlacklist
                && plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return true;
        }
        // Worlds are in whitelist mode
        //noinspection RedundantIfStatement -- I like the parallel here.
        if (!plugin.config().worldBlacklist
                && !plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerManager.getInstance().removePlayer(event.getPlayer());
    }

    public void onTick() {
        for (SuperElytraPlayer sePlayer : PlayerManager.getInstance()) {
            try {
                Player player = sePlayer.getPlayer();
                if (!player.isGliding()) {
                    sePlayer.setBoostTicks(0);
                }
                if (!player.hasPermission("superelytra.launch")) {
                    return;
                }
                if (shouldCancel(player)) continue;
                if (!player.isOnGround() || !sePlayer.isChargingLaunch()) continue;

                int time = sePlayer.getChargeUpTicks();
                sePlayer.setChargeUpTicks(++time);

                Location loc = player.getLocation();
                World world = player.getWorld();

                world.spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0.2F, 0.2F, 0.2F, 0.0F); // radius 30
                if (time % 3 == 0) {
                    if (plugin.config().chargeSound != null)
                        player.playSound(player.getLocation(), plugin.config().chargeSound.bukkitSound(), 0.1F, 0.1F);
                    if (time >= plugin.config().chargeupTicks) {
                        world.spawnParticle(Particle.FLAME, loc, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                        if (plugin.config().readySound != null)
                            player.playSound(player.getLocation(), plugin.config().readySound.bukkitSound(), 0.1F, 0.1F);
                    }
                }
            } catch (Exception ex) {
                if (this.handledExceptions.contains(ex.getMessage())) {
                    return;
                }
                Sentry.captureException(ex);
                this.handledExceptions.add(ex.getMessage());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        try {
            Player player = event.getPlayer();
            if (shouldCancel(player)) return;
            if (!player.isGliding()) {
                return;
            }

            SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);

            if (player.hasPermission(PERMISSION_GLIDE)
                    && superElytraPlayer.isEnabled()
                    && superElytraPlayer.preferences.boost
                    && player.getLocation().getPitch() > plugin.config().maxGlideAngle
            ) {
                Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
                player.setVelocity(player.getVelocity().add(unitVector.multiply(plugin.config().speed)));
            }

            if (player.hasPermission(PERMISSION_BOOST)
                    && superElytraPlayer.isBoosting()
                    && superElytraPlayer.preferences.firework) {
                Vector boostMomentum = player.getVelocity().normalize().multiply(plugin.config().boostModifier);
                player.setVelocity(player.getLocation().getDirection().add(boostMomentum));
                superElytraPlayer.decrementBoostTicks();
            }
        } catch (Exception ex) {
            if (this.handledExceptions.contains(ex.getMessage())) {
                return;
            }
            Sentry.captureException(ex);
            this.handledExceptions.add(ex.getMessage());
        }
    }

    @EventHandler()
    public void onBoost(PlayerInteractEvent event) {
        try {
            SuperElytraPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer());
            ItemStack item = event.getItem();

            if (!player.isEnabled()
                    || !player.preferences.firework
                    || event.getAction() != Action.RIGHT_CLICK_AIR
                    || item == null
                    || item.getType() != Material.FIREWORK_ROCKET
            ) {
                return;
            }

            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            player.setBoostTicks((meta.getPower() + 1) * plugin.config().boostDuration);

        } catch (Exception ex) {
            if (this.handledExceptions.contains(ex.getMessage())) {
                return;
            }
            Sentry.captureException(ex);
            this.handledExceptions.add(ex.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        try {
            Player player = event.getPlayer();
            if (!player.hasPermission(PERMISSION_LAUNCH)) return;
            if (shouldCancel(player)) return;

            ItemStack chestPlate = player.getEquipment().getChestplate();
            if (chestPlate == null || chestPlate.getType() != Material.ELYTRA)
                return;
            SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);
            if (!superElytraPlayer.isEnabled() || !superElytraPlayer.preferences.launch) {
                return;
            }

            // start charging up
            if (event.isSneaking()) {
                PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(0);
            }

            // release charge
            else {
                if (PlayerManager.getInstance().getPlayer(player).getChargeUpTicks() >= plugin.config().chargeupTicks) {
                    if (!plugin.getLaunchCooldownManager().canUse(player.getUniqueId())) {
                        HashMap<String, String> vars = new HashMap<>();
                        long time = plugin.getLaunchCooldownManager().timeUntilUse(player.getUniqueId());
                        long seconds = (time / 1000) % 60;
                        long minutes = (time / (1000 * 60)) % 60;
                        long hours = (time / (60 * 60 * 1000)) % 24;
                        vars.put("seconds", String.valueOf(seconds));
                        if (seconds == 1) {
                            vars.put("seconds_plural", plugin.getMessenger().getMessage("second", new HashMap<>()));
                        } else {
                            vars.put("seconds_plural", plugin.getMessenger().getMessage("seconds", new HashMap<>()));
                        }
                        if (minutes == 0) {
                            vars.put("minutes_plural", "");
                            vars.put("minutes", "");
                        } else if (minutes == 1) {
                            vars.put("minutes_plural", plugin.getMessenger().getMessage("minute", new HashMap<>()));
                            vars.put("minutes", String.valueOf(minutes));
                        } else {
                            vars.put("minutes_plural", plugin.getMessenger().getMessage("minutes", new HashMap<>()));
                            vars.put("minutes", String.valueOf(minutes));
                        }
                        if (hours == 0) {
                            vars.put("hours_plural", "");
                            vars.put("hours", "");
                        } else if (hours == 1) {
                            vars.put("hours_plural", plugin.getMessenger().getMessage("hour", new HashMap<>()));
                            vars.put("hours", String.valueOf(hours));
                        } else {
                            vars.put("hours_plural", plugin.getMessenger().getMessage("hours", new HashMap<>()));
                            vars.put("hours", String.valueOf(hours));
                        }
                        vars.put("username", player.getName());
                        plugin.getMessenger().sendErrorMessage(player, "cooldown", vars, true);
                        PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(-1);
                        return;
                    }
                    Location loc = player.getLocation();
                    Vector dir = loc.getDirection().add(new Vector(0, plugin.config().launch, 0));

                    player.setVelocity(player.getVelocity().add(dir));
                    loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
                    if (plugin.config().launchSound != null)
                        player.playSound(loc, plugin.config().launchSound.bukkitSound(), 0.1F, 2.0F);
                    plugin.getLaunchCooldownManager().use(player.getUniqueId());
                }
                PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(-1);
            }
        } catch (Exception ex) {
            if (this.handledExceptions.contains(ex.getMessage())) {
                return;
            }
            Sentry.captureException(ex);
            this.handledExceptions.add(ex.getMessage());
        }
    }

}
