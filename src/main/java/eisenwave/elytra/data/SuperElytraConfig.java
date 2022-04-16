package eisenwave.elytra.data;

import eisenwave.elytra.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eisenwave.elytra.data.ConfigUtil.getIfValid;
import static eisenwave.elytra.data.ConfigUtil.getInEnum;

@SerializableAs("SuperElytraConfig")
public class SuperElytraConfig implements ConfigurationSerializable {

    public final int chargeupTicks;
    public final double speedMultiplier;
    public final double launchMultiplier;
    public final transient double speed;
    public final transient double launch;
    public final boolean enabledDefault;
    public final Sound chargeSound;
    public final Sound readySound;
    public final Sound launchSound;
    public final int autosaveInterval;
    public final int cooldown;
    public final List<String> worlds;
    public final boolean worldBlacklist;
    public final boolean allowBoosting;
    public final int boostDuration;
    public final double boostModifier;
    public final double maxGlideAngle;

    private static final double BASE_LAUNCH = 3f;
    private static final double BASE_SPEED = 0.05f;

    public SuperElytraConfig(final int chargeupTicks, final double speedMultiplier, final double launchMultiplier,
                             final boolean enabledDefault, final Sound chargeSound, final Sound readySound,
                             final Sound launchSound, final int autosaveInterval, final int cooldown, final List<String> worlds,
                             final boolean worldBlacklist, final boolean allowBoosting, final int boostDuration, final double boostModifier, final double maxGlideAngle) {
        this.chargeupTicks = chargeupTicks;
        this.speedMultiplier = speedMultiplier;
        speed = speedMultiplier * SuperElytraConfig.BASE_SPEED;
        this.launchMultiplier = launchMultiplier;
        launch = launchMultiplier * SuperElytraConfig.BASE_LAUNCH;
        this.enabledDefault = enabledDefault;
        this.chargeSound = chargeSound;
        this.readySound = readySound;
        this.launchSound = launchSound;
        this.autosaveInterval = autosaveInterval;
        this.cooldown = cooldown;
        this.worlds = worlds;
        this.worldBlacklist = worldBlacklist;
        this.allowBoosting = allowBoosting;
        this.boostDuration = boostDuration;
        this.boostModifier = boostModifier;
        this.maxGlideAngle = maxGlideAngle;
    }

    public static SuperElytraConfig deserialize(final Map<String, Object> map) {
        final int chargeupTicks = getIfValid(map, "chargeup-ticks", Integer.class, 60);
        final double speedMultiplier = getIfValid(map, "speed-multiplier", Double.class, 1.0d);
        final double launchMultiplier = getIfValid(map, "launch-multiplier", Double.class, 1.0d);
        final boolean enabledDefault = getIfValid(map, "default", Boolean.class, true);
        final Sound chargeSound = getInEnum(map, "charge-sound", Sound.class, Sound.FUSE);
        final Sound readySound = getInEnum(map, "ready-sound", Sound.class, Sound.BAT_TAKEOFF);
        final Sound launchSound = getInEnum(map, "launch-sound", Sound.class, Sound.ENDERDRAGON_WINGS);
        final int autosaveInterval = getIfValid(map, "autosave-interval", Integer.class, 600);
        final int cooldown = getIfValid(map, "cooldown", Integer.class, 5000);
        //noinspection unchecked
        List<String> worlds = (ArrayList<String>) getIfValid(map, "worlds", ArrayList.class, new ArrayList<String>());
        worlds = worlds.stream().map(String::toLowerCase).collect(Collectors.toList());
        final boolean worldBlacklist = getIfValid(map, "world-blacklist", Boolean.class, true);
        final boolean allowBoosting = getIfValid(map, "allow-boosting", Boolean.class, false);
        final int boostDuration = getIfValid(map, "boost-duration", Integer.class, 40);
        final double boostModifier = getIfValid(map, "boost-modifier", Double.class, 0.0);
        final double maxGlideAngle = getIfValid(map, "max-glide-angle", Double.class, -90.0);
        return new SuperElytraConfig(chargeupTicks, speedMultiplier, launchMultiplier, enabledDefault, chargeSound, readySound, launchSound, autosaveInterval, cooldown, worlds, worldBlacklist, allowBoosting, boostDuration, boostModifier, maxGlideAngle);
    }

    @Override
    public String toString() {
        return "SuperElytraConfig{" +
                "chargeupTicks=" + this.chargeupTicks +
                ", speedMultiplier=" + this.speedMultiplier +
                ", launchMultiplier=" + this.launchMultiplier +
                ", speed=" + this.speed +
                ", launch=" + this.launch +
                ", enabledDefault=" + this.enabledDefault +
                ", chargeSound=" + this.chargeSound +
                ", readySound=" + this.readySound +
                ", launchSound=" + this.launchSound +
                ", autosaveInterval=" + this.autosaveInterval +
                ", cooldown=" + this.cooldown +
                ", worlds=" + this.worlds +
                ", worldBlacklist=" + this.worldBlacklist +
                ", allowBoosting=" + this.allowBoosting +
                ", boostDuration=" + this.boostDuration +
                ", boostModifier=" + this.boostModifier +
                '}';
    }

    @Override
    public Map<String, Object> serialize() {
        return ConfigUtil.reflectiveSerialize(this, SuperElytraConfig.class);
    }
}
