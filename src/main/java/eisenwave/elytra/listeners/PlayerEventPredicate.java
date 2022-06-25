package eisenwave.elytra.listeners;

import org.bukkit.event.player.PlayerEvent;

public interface PlayerEventPredicate<T extends PlayerEvent> {
    boolean shouldHandle(T event);
}
