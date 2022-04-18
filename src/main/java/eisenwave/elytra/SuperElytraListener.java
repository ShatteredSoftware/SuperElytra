package eisenwave.elytra;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

public class SuperElytraListener implements Listener {

    private static final String
            PERMISSION_LAUNCH = "superelytra.launch",
            PERMISSION_GLIDE = "superelytra.glide",
            PERMISSION_BOOST = "superelytra.boost";

    private final transient SuperElytraPlugin plugin;

    private final HashSet<String> handledExceptions = new HashSet<>();

    public SuperElytraListener(final SuperElytraPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean shouldCancel(final Player player) {
        // Worlds are in blacklist mode
        if (this.plugin.config().worldBlacklist
                && this.plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return true;
        }
        // Worlds are in whitelist mode
        //noinspection RedundantIfStatement -- I like the parallel here.
        if (!this.plugin.config().worldBlacklist
                && !this.plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        PlayerManager.getInstance().removePlayer(event.getPlayer());
    }

    public void onTick() {
        for (final SuperElytraPlayer sePlayer : PlayerManager.getInstance()) {
            try {
                final Player player = sePlayer.getPlayer();
                if (!player.isGliding()) {
                    sePlayer.setBoostTicks(0);
                }
                if (!player.hasPermission("superelytra.launch")) {
                    return;
                }
                if (this.shouldCancel(player)) continue;
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
                if (handledExceptions.contains(ex.getMessage())) {
                    return;
                }
                this.plugin.getErrorLogger().captureException(ex);
                handledExceptions.add(ex.getMessage());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        try {
            final Player player = event.getPlayer();
            if (this.shouldCancel(player)) return;
            if (!player.isGliding()) {
                return;
            }

            final SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);

            if (player.hasPermission(SuperElytraListener.PERMISSION_GLIDE)
                    && superElytraPlayer.isEnabled()
                    && superElytraPlayer.preferences.boost
                    && player.getLocation().getPitch() > this.plugin.config().maxGlideAngle
            ) {
                final Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
                player.setVelocity(player.getVelocity().add(unitVector.multiply(this.plugin.config().speed)));
            }

            if (player.hasPermission(SuperElytraListener.PERMISSION_BOOST)
                    && superElytraPlayer.isBoosting()
                    && superElytraPlayer.preferences.firework) {
                final Vector boostMomentum = player.getVelocity().normalize().multiply(this.plugin.config().boostModifier);
                player.setVelocity(player.getLocation().getDirection().add(boostMomentum));
                superElytraPlayer.decrementBoostTicks();
            }
        } catch (final Exception ex) {
            if (handledExceptions.contains(ex.getMessage())) {
                return;
            }
            this.plugin.getErrorLogger().captureException(ex);
            handledExceptions.add(ex.getMessage());
        }
    }

    @EventHandler
    public void onBoost(final PlayerInteractEvent event) {
        try {
            final SuperElytraPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer());
            final ItemStack item = event.getItem();

            if (!player.isEnabled()
                    || !player.preferences.firework
                    || event.getAction() != Action.RIGHT_CLICK_AIR
                    || item == null
                    || item.getType() != Material.FIREWORK_ROCKET
            ) {
                return;
            }

            final FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            player.setBoostTicks((meta.getPower() + 1) * this.plugin.config().boostDuration);

        } catch (final Exception ex) {
            if (handledExceptions.contains(ex.getMessage())) {
                return;
            }
            this.plugin.getErrorLogger().captureException(ex);
            handledExceptions.add(ex.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(final PlayerToggleSneakEvent event) {
        try {
            final Player player = event.getPlayer();
            if (!player.hasPermission(SuperElytraListener.PERMISSION_LAUNCH)) return;
            if (this.shouldCancel(player)) return;

            final ItemStack chestPlate = player.getEquipment().getChestplate();
            if (chestPlate == null || chestPlate.getType() != Material.ELYTRA)
                return;
            final SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);
            if (!superElytraPlayer.isEnabled() || !superElytraPlayer.preferences.launch) {
                return;
            }

            // start charging up
            if (event.isSneaking()) {
                PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(0);
            }

            // release charge
            else {
                if (PlayerManager.getInstance().getPlayer(player).getChargeUpTicks() >= this.plugin.config().chargeupTicks) {
                    if (!this.plugin.getLaunchCooldownManager().canUse(player.getUniqueId())) {
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
                        PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(-1);
                        return;
                    }
                    final Location loc = player.getLocation();
                    final Vector dir = loc.getDirection().add(new Vector(0, this.plugin.config().launch, 0));

                    player.setVelocity(player.getVelocity().add(dir));
                    loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
                    if (this.plugin.config().launchSound != null)
                        player.playSound(loc, this.plugin.config().launchSound.bukkitSound(), 0.1F, 2.0F);
                    this.plugin.getLaunchCooldownManager().use(player.getUniqueId());
                }
                PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(-1);
            }
        } catch (final Exception ex) {
            if (handledExceptions.contains(ex.getMessage())) {
                return;
            }
            this.plugin.getErrorLogger().captureException(ex);
            handledExceptions.add(ex.getMessage());
        }
    }

}
