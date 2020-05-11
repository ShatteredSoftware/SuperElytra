package eisenwave.elytra.data;

import static eisenwave.elytra.data.ConfigUtil.getIfValid;
import static eisenwave.elytra.data.ConfigUtil.getInEnum;

import eisenwave.elytra.Sound;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SuperElytraConfig")
public class SuperElytraConfig implements ConfigurationSerializable {

    public final int chargupTicks;
    private final double speedMultiplier;
    private final double launchMultiplier;
    public transient final double speed;
    public transient final double launch;
    public final boolean enabledDefault;
    public final Sound chargeSound;
    public final Sound readySound;
    public final Sound launchSound;
    public final int autosaveInterval;

    private static final double BASE_LAUNCH = 3f;
    private static final double BASE_SPEED = 0.05f;

    public SuperElytraConfig(int chargupTicks, double speedMultiplier, double launchMultiplier,
        boolean enabledDefault, Sound chargeSound, Sound readySound,
        Sound launchSound, int autosaveInterval) {
        this.chargupTicks = chargupTicks;
        this.speedMultiplier = speedMultiplier;
        this.speed = speedMultiplier * BASE_SPEED;
        this.launchMultiplier = launchMultiplier;
        this.launch = launchMultiplier * BASE_LAUNCH;
        this.enabledDefault = enabledDefault;
        this.chargeSound = chargeSound;
        this.readySound = readySound;
        this.launchSound = launchSound;
        this.autosaveInterval = autosaveInterval;
    }

    public static SuperElytraConfig deserialize(Map<String, Object> map) {
        int chargeupTicks = getIfValid(map, "chargeup-ticks", Integer.class, 60);
        double speedMultiplier = getIfValid(map, "speed-multiplier", Double.class, 1.0d);
        double launchMultiplier = getIfValid(map, "launch-multiplier", Double.class, 1.0d);
        boolean enabledDefault = getIfValid(map, "default", Boolean.class, true);
        Sound chargeSound = getInEnum(map, "charge-sound", Sound.class, Sound.FUSE);
        Sound readySound = getInEnum(map, "ready-sound", Sound.class, Sound.BAT_TAKEOFF);
        Sound launchSound = getInEnum(map, "launch-sound", Sound.class, Sound.ENDERDRAGON_WINGS);
        int autosaveInterval = getIfValid(map, "autosave-interval", Integer.class, 600);
        return new SuperElytraConfig(chargeupTicks, speedMultiplier, launchMultiplier, enabledDefault, chargeSound, readySound, launchSound, autosaveInterval);
    }

    @Override
    public Map<String, Object> serialize() {
        return ConfigUtil.reflectiveSerialize(this, SuperElytraConfig.class);
    }
}
