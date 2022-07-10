package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.data.PlayerPreferences;
import eisenwave.elytra.data.SuperElytraConfig;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class BaseListener<T extends PlayerEvent> {
    public enum Permission {
        LAUNCH("superelytra.launch"),
        GLIDE("superelytra.glide"),

        BOOST_FLYING("superelytra.launch.flying"),

        BOOST("superelytra.boost");

        public final String permission;

        Permission(String permission) {
            this.permission = permission;
        }
    }

    protected final SuperElytraPlugin plugin;
    protected final ErrorLogger errorLogger;
    protected final PlayerManager playerManager;

    public BaseListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        this.plugin = plugin;
        this.errorLogger = errorLogger;
        this.playerManager = playerManager;
    }

    protected boolean shouldHandle(Player player) {
        // Worlds are in blacklist mode
        if (this.plugin.config().worldBlacklist
                && this.plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return false;
        }
        // Worlds are in whitelist mode
        //noinspection RedundantIfStatement -- I like the parallel here.
        if (!this.plugin.config().worldBlacklist
                && !this.plugin.config().worlds.contains(player.getWorld().getName().toLowerCase())) {
            return false;
        }
        return true;
    }

    protected boolean shouldHandleEvent(final PlayerEvent playerEvent) {
        final Player player = playerEvent.getPlayer();
        return shouldHandle(player);
    }

    protected PlayerEventPredicate<T> checkPreference(Predicate<PlayerPreferences> predicate) {
        return (playerEvent) -> {
            SuperElytraPlayer superElytraPlayer = playerManager.getPlayer(playerEvent.getPlayer());
            if (superElytraPlayer == null) {
                this.errorLogger.handleError(new Throwable("SuperElytra player was null when player was not"));
                return false;
            }
            return superElytraPlayer.isEnabled() && predicate.test(superElytraPlayer.preferences);
        };
    }

    @SafeVarargs
    public final ThenDo checkPredicates(T event, PlayerEventPredicate<T>... predicates) {
        return runnable -> () -> {
            try {
                if (Arrays.stream(predicates).allMatch(pred -> pred.shouldHandle(event))) {
                    runnable.run();
                }
            } catch (Exception ex) {
                errorLogger.handleError(ex);
            }
        };
    }

    public interface ThenDo {
        Runnable then(Runnable runnable);
    }
}
