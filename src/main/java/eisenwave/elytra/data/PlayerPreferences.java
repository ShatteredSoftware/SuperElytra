package eisenwave.elytra.data;

import eisenwave.elytra.SuperElytraPlugin;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SerializableAs("SuperElytraPlayerPreferences")
public class PlayerPreferences implements ConfigurationSerializable {
    public boolean launch;
    public boolean boost;

    public PlayerPreferences(boolean launch, boolean boost) {
        this.launch = launch;
        this.boost = boost;
    }

    public static PlayerPreferences loadPreferences(Player player) {
        try {
            File f = new File(JavaPlugin.getPlugin(SuperElytraPlugin.class).getDataFolder(), "data" +
            File.separator + player.getUniqueId().toString() + ".yml");
            if(!f.exists()) {
                f.getParentFile().mkdirs();
                    f.createNewFile();
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            if(config.contains("settings")) {
                return (PlayerPreferences) config.get("settings");
            }
            PlayerPreferences prefs = new PlayerPreferences(true, true);
            config.set("settings", prefs);
            config.save(f);
            return prefs;
        } catch (IOException e) {
            e.printStackTrace();
            return new PlayerPreferences(true, true);
        }
    }

    public static PlayerPreferences deserialize(Map<String, Object> map) {
        boolean launch = ConfigUtil.getIfValid(map, "launch", Boolean.class, true);
        boolean boost = ConfigUtil.getIfValid(map, "boost", Boolean.class, true);
        return new PlayerPreferences(launch, boost);
    }

    public void save(Player player) {
        try {
            File f = new File(JavaPlugin.getPlugin(SuperElytraPlugin.class).getDataFolder(), "data" +
                File.separator + player.getUniqueId().toString() + ".yml");
            if(!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            PlayerPreferences prefs = (PlayerPreferences) config.get("settings", new PlayerPreferences(true, true));
            config.set("settings", prefs);
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("launch", launch);
        map.put("boost", boost);
        return map;
    }
}
