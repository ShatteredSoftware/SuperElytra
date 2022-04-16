package eisenwave.elytra;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns. Create multiple instances for different types of cooldowns.
 */
public class CooldownManager {

    public static final int DEFAULT_COOLDOWN = 1000; // 1000 millis = 1 second
    private final Map<UUID, Long> lastUse;
    private final int cooldown;

    /**
     * Constructs a cooldown manager with {@link #DEFAULT_COOLDOWN} as the cooldown.
     */
    public CooldownManager() {
        this(CooldownManager.DEFAULT_COOLDOWN);
    }

    /**
     * Constructs a cooldown manager with a specified cooldown.
     *
     * @param cooldown The number of milliseconds between uses of this feature.
     */
    public CooldownManager(final Integer cooldown) {
        this.lastUse = new HashMap<>();
        if (cooldown == null) {
            this.cooldown = CooldownManager.DEFAULT_COOLDOWN;
        } else {
            this.cooldown = cooldown;
        }
    }

    /**
     * Checks if a player has time left on this cooldown.
     *
     * @param uuid The player to check.
     * @return Whether this player has time left on their cooldown.
     */
    public boolean canUse(final UUID uuid) {
        if (!this.lastUse.containsKey(uuid)) {
            return true;
        }
        return System.currentTimeMillis() > (this.lastUse.get(uuid) + this.cooldown);
    }

    /**
     * Sets last use of this cooldown to the current time for the given player.
     *
     * @param uuid The player to set the cooldown for.
     */
    public void use(final UUID uuid) {
        this.lastUse.put(uuid, System.currentTimeMillis());
    }

    /**
     * Gets the amount of time left on a cooldown, in milliseconds.
     *
     * @param uuid The UUID of the player to check.
     * @return The amount of time left on this cooldown, or 0 if there is none.
     */
    public long timeUntilUse(final UUID uuid) {
        if (!this.lastUse.containsKey(uuid)) {
            return 0;
        }
        final long left = (this.lastUse.get(uuid) + this.cooldown) - System.currentTimeMillis();
        return left > 0 ? left : 0;
    }
}
