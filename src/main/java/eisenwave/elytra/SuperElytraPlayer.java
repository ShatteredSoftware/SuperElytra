package eisenwave.elytra;

import eisenwave.elytra.data.PlayerPreferences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The SuperElytra wrapper for a player.
 */
public class SuperElytraPlayer {

    private final Player player;
    public final PlayerPreferences preferences;

    private int chargeUpTicks = -1;
    private boolean enabled;
    private int boostTicks;

    private boolean infiniteBoosting = false;

    public SuperElytraPlayer(final Player player) {
        this(player, null);
    }

    public SuperElytraPlayer(final Player player, final Boolean enabled) {
        this.player = player;
        if (enabled == null) {
            this.enabled = JavaPlugin.getPlugin(SuperElytraPlugin.class).config().enabledDefault;
        }
        preferences = PlayerPreferences.loadPreferences(player);
    }

    public boolean isGrounded() {
        return player.getLocation().getY() - (double) player.getLocation().getBlockY() < 0.0001;
    }

    // GETTERS

    /**
     * Returns the wrapped player.
     *
     * @return the wrapped player
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.player.getUniqueId());
    }

    /**
     * Returns the preferences of the player.
     * 
     * @return the preferences
     */
    public PlayerPreferences getPrefs() {
        return this.preferences;
    }

    /**
     * Returns the amount of charge-up ticks. If the number is negative, the player
     * is not charging up a launch.
     *
     * @return the amount of charge-up ticks
     */
    public int getChargeUpTicks() {
        return this.chargeUpTicks;
    }

    // PREDICATES

    /**
     * Returns whether enhanced flight is enabled for this player.
     *
     * @return enhanced flight
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns whether the player is charging up a launch.
     *
     * @return whether the player is charging up a launch
     */
    public boolean isChargingLaunch() {
        return this.chargeUpTicks >= 0;
    }

    public boolean isBoosting() {
        return this.boostTicks > 0 || this.infiniteBoosting;
    }

    // SETTERS

    /**
     * Sets the enhanced flight mode for this player.
     *
     * @param enabled the enhanced flight mode
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the amount of launch charge-up ticks of the player. If the given amount
     * is negative, this indicates no
     * charge-up at all.
     *
     * @param ticks the amount of ticks
     */
    public void setChargeUpTicks(final int ticks) {
        chargeUpTicks = ticks;
    }

    public void setBoostTicks(final int ticks) {
        boostTicks = ticks;
    }

    public void setInfiniteBoosting(final boolean boost) {
        this.infiniteBoosting = boost;
    }

    public void addBoostTicks(final int ticks) {
        boostTicks += ticks;
    }

    public void decrementBoostTicks() {
        boostTicks--;
    }

    public int getBoostTicks() {
        return boostTicks;
    }
}
