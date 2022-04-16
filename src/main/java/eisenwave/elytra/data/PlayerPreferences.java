package eisenwave.elytra.data;

import eisenwave.elytra.SuperElytraPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("SuperElytraPlayerPreferences")
public class PlayerPreferences implements ConfigurationSerializable {
    public boolean launch;
    public boolean boost;
    public boolean firework;

    public PlayerPreferences(final boolean launch, final boolean boost, final boolean firework) {
        this.launch = launch;
        this.boost = boost;
        this.firework = firework;
    }

    public static PlayerPreferences loadPreferences(final Player player) {
        try {
            final File f = new File(JavaPlugin.getPlugin(SuperElytraPlugin.class).getDataFolder(), "data" +
                    File.separator + player.getUniqueId() + ".yml");
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            if (config.contains("settings")) {
                return (PlayerPreferences) config.get("settings");
            }
            final PlayerPreferences prefs = new PlayerPreferences(true, true, true);
            config.set("settings", prefs);
            config.save(f);
            return prefs;
        } catch (final IOException e) {
            e.printStackTrace();
            return new PlayerPreferences(true, true, true);
        }
    }

    public static PlayerPreferences deserialize(final Map<String, Object> map) {
        final boolean launch = ConfigUtil.getIfValid(map, "launch", Boolean.class, true);
        final boolean boost = ConfigUtil.getIfValid(map, "boost", Boolean.class, true);
        final boolean firework = ConfigUtil.getIfValid(map, "firework", Boolean.class, true);
        return new PlayerPreferences(launch, boost, firework);
    }

    public void save(final UUID uuid) {
        try {
            final File f = new File(JavaPlugin.getPlugin(SuperElytraPlugin.class).getDataFolder(), "data" +
                    File.separator + uuid.toString() + ".yml");
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            final PlayerPreferences prefs = (PlayerPreferences) config.get("settings", new PlayerPreferences(true, true, true));
            config.set("settings", prefs);
            config.save(f);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("launch", this.launch);
        map.put("boost", this.boost);
        map.put("firework", this.firework);
        return map;
    }
}
