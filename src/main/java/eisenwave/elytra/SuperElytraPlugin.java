package eisenwave.elytra;

import eisenwave.elytra.command.ElytraModeCommand;
import eisenwave.elytra.command.ElytraToggleCommand;
import eisenwave.elytra.command.ReloadCommand;
import eisenwave.elytra.data.PlayerPreferences;
import eisenwave.elytra.data.SuperElytraConfig;
import eisenwave.elytra.messages.Messageable;
import eisenwave.elytra.messages.Messages;
import eisenwave.elytra.messages.Messenger;
import io.sentry.Sentry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

public class SuperElytraPlugin extends JavaPlugin implements Listener, Messageable {

    private SuperElytraListener eventHandler;
    private SuperElytraConfig config;
    private Messenger messenger;
    private Messages messages;
    private CooldownManager launchCooldownManager;
    private CooldownManager boostCooldownManager;

    public SuperElytraConfig config() {
        return this.config;
    }

    public void reload() {
        if (getConfig().contains("config")) {
            reloadConfig();
            config = (SuperElytraConfig) getConfig().get("config");
        } else {
            try {
                final File f = new File(getDataFolder(), "config.yml");
                final File backup = new File(getDataFolder(), "config.old.yml");
                FileUtil.copy(f, backup);
                config = new SuperElytraConfig(
                        getConfig().getInt("chargeup_time", 60),
                        getConfig().getDouble("speed_multiplier", 1.0d),
                        getConfig().getDouble("launch_multiplier", 1.0d),
                        getConfig().getBoolean("default", true),
                        Sound.valueOf(getConfig().getString("charge-sound", "FUSE")),
                        Sound.valueOf(getConfig().getString("ready-sound", "BAT_TAKEOFF")),
                        Sound.valueOf(getConfig().getString("launch-sound", "ENDERDRAGON_WINGS")),
                        600,
                        50,
                        new ArrayList<>(),
                        true,
                        false, 40, 0.0, -90);
                getConfig().set("chargeup_time", null);
                getConfig().set("speed_multiplier", null);
                getConfig().set("launch_multiplier", null);
                getConfig().set("default", null);
                getConfig().set("charge-sound", null);
                getConfig().set("ready-sound", null);
                getConfig().set("launch-sound", null);
                getConfig().set("config", config);
                getConfig().save(f);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        launchCooldownManager = new CooldownManager(config.cooldown);
        boostCooldownManager = new CooldownManager(50);
        this.loadMessages();
    }

    @Override
    public void onDisable() {
        autosave();
    }

    public void autosave() {
        for (final SuperElytraPlayer player : PlayerManager.getInstance()) {
            if (player == null || player.getPlayer() == null) {
                continue;
            }
            player.preferences.save(player.getPlayer().getUniqueId());
        }
    }

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(PlayerPreferences.class);
        ConfigurationSerialization.registerClass(SuperElytraConfig.class);
    }

    @Override
    public void onEnable() {
        final MetricsLite metrics = new MetricsLite(this, 7488);
        this.saveDefaultConfig();
        this.reload();

        this.initListeners();
        this.initCommands();

        Sentry.init(options -> {
            options.setDsn("https://d48172d870b54d749614e2711d6f377d@o244958.ingest.sentry.io/6339955");
            options.setTracesSampleRate(1.0);
        });
        Sentry.setExtra("plugin_version", this.getDescription().getVersion());
        Sentry.setExtra("bukkit_version", this.getServer().getBukkitVersion());
        Sentry.setExtra("server_version", this.getServer().getVersion());
    }

    private void loadMessages() {
        if (!this.getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.getDataFolder().mkdirs();
        }
        final File messagesFile = new File(this.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            this.saveResource("messages.yml", false);
        }
        final Messages messages = new Messages(this, YamlConfiguration.loadConfiguration(messagesFile));
        this.messenger = new Messenger(this, messages);
    }

    private void initListeners() {
        eventHandler = new SuperElytraListener(this);

        this.getServer().getPluginManager().registerEvents(this.eventHandler, this);

        this.getServer().getScheduler().runTaskTimer(this, () -> this.eventHandler.onTick(), 0, 1);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::autosave, 0, 20 * this.config.autosaveInterval);
    }

    private void initCommands() {
        this.getCommand("elytramode").setExecutor(new ElytraModeCommand(this));
        this.getCommand("elytrareload").setExecutor(new ReloadCommand(this));
        this.getCommand("elytraprefs").setExecutor(new ElytraToggleCommand(this));
    }

    public SuperElytraListener getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public Messenger getMessenger() {
        return this.messenger;
    }

    public CooldownManager getLaunchCooldownManager() {
        return launchCooldownManager;
    }
}
