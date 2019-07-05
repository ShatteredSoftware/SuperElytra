package eisenwave.elytra;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class SuperElytraListener implements Listener {
    
    // STATIC CONST
    
    private final static String
        PERMISSION_LAUNCH = "superelytra.launch",
        PERMISSION_GLIDE = "superelytra.glide",
        CONFIG_SPEED_MULTIPLIER = "speed_multiplier",
        CONFIG_LAUNCH_MULTIPLIER = "launch_multiplier",
        CONFIG_CHARGE_UP_TIME = "chargeup_time",
        CONFIG_DEFAULT = "default",
        CONFIG_CHARGE_SOUND = "charge-sound",
        CONFIG_READY_SOUND = "ready-sound",
        CONFIG_LAUNCH_SOUND = "launch-sound";
    
    private final static int DEFAULT_CHARGE_UP_TIME = 60;
    private final static double
        DEFAULT_SPEED_MULTIPLIER = 1,
        DEFAULT_LAUNCH_MULTIPLIER = 1,
        BASE_SPEED = 0.05,
        BASE_LAUNCH = 3;
    private final static boolean DEFAULT_DEFAULT = true;
    private final static Sound DEFAULT_CHARGE_SOUND = Sound.FUSE;
    private final static Sound DEFAULT_READY_SOUND = Sound.BAT_TAKEOFF;
    private final static Sound DEFAULT_LAUNCH_SOUND = Sound.ENDERDRAGON_WINGS;

    // INSTANCE
    
    private Map<Player, SuperElytraPlayer> playerMap = new WeakHashMap<>();
    
    private final double speed, launchStrength;
    private final int chargeUpTime;
    private final boolean _default;
    private Sound chargeSound, readySound, launchSound;
    
    public SuperElytraListener(SuperElytraPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        this.speed = config.getDouble(CONFIG_SPEED_MULTIPLIER, DEFAULT_SPEED_MULTIPLIER) * BASE_SPEED;
        this.launchStrength = config.getDouble(CONFIG_LAUNCH_MULTIPLIER, DEFAULT_LAUNCH_MULTIPLIER) * BASE_LAUNCH;
        this.chargeUpTime = config.getInt(CONFIG_CHARGE_UP_TIME, DEFAULT_CHARGE_UP_TIME);
        this._default = config.getBoolean(CONFIG_DEFAULT, DEFAULT_DEFAULT);
        try
        {
            String sound = config.getString(CONFIG_CHARGE_SOUND, "FUSE").toUpperCase();
            if(sound.equalsIgnoreCase("NONE"))
            {
                chargeSound = null;
            }
            else
            {
                this.chargeSound = Sound.valueOf(sound);
            }
        }
        catch (IllegalArgumentException ex)
        {
            this.chargeSound = DEFAULT_CHARGE_SOUND;
        }
        try
        {
            String sound = config.getString(CONFIG_READY_SOUND, "BAT_TAKEOFF").toUpperCase();
            if(sound.equalsIgnoreCase("NONE"))
            {
                readySound = null;
            }
            else
            {
                this.readySound = Sound.valueOf(sound);
            }
        }
        catch (IllegalArgumentException ex)
        {
            this.readySound = DEFAULT_READY_SOUND;
        }
        try
        {
            String sound = config.getString(CONFIG_LAUNCH_SOUND, "BAT_TAKEOFF").toUpperCase();
            if(sound.equalsIgnoreCase("NONE"))
            {
                launchSound = null;
            }
            else
            {
                this.launchSound = Sound.valueOf(sound);
            }
        }
        catch (IllegalArgumentException ex)
        {
            this.launchSound = DEFAULT_LAUNCH_SOUND;
        }
    }
    
    /*public static String[] splitIntoParts(String str, int partLength) {
        int strLength = str.length();
        String[] parts = new String[(int) Math.ceil(strLength / (float) partLength)];
        for (int i = 0; i < parts.length; i++)
            parts[i] = str.substring(i * partLength, Math.min(strLength, (i + 1) * partLength));
        return parts;
    }*/

    public SuperElytraPlayer getPlayer(Player player) {
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (playerMap.containsKey(player)) {
            return playerMap.get(player);
        }
        else {
            SuperElytraPlayer sePlayer = new SuperElytraPlayer(player, _default);
            playerMap.put(player, sePlayer);
            return sePlayer;
        }
    }

    @SuppressWarnings("deprecation")
    public void onTick() {
        for (Map.Entry<Player, SuperElytraPlayer> entry : playerMap.entrySet()) {
            Player player = entry.getKey();
            SuperElytraPlayer sePlayer = entry.getValue();
            if (!player.isOnGround() || !sePlayer.isChargingLaunch()) continue;
            
            int time = sePlayer.getChargeUpTicks();
            sePlayer.setChargeUpTicks(++time);
            
            Location loc = player.getLocation();
            World world = player.getWorld();
    
            world.spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0.2F, 0.2F, 0.2F, 0.0F); // radius 30
            if (time % 3 == 0) {
                if(chargeSound != null)
                    player.playSound(player.getLocation(), chargeSound.bukkitSound(), 0.1F, 0.1F);
                if (time >= chargeUpTime) {
                    world.spawnParticle(Particle.FLAME, loc, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                    if(readySound != null)
                        player.playSound(player.getLocation(), readySound.bukkitSound(), 0.1F, 0.1F);
                }
            }
        }
    }
    
    // BUKKIT EVENT HANDLERS
    
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isGliding() && player.hasPermission(PERMISSION_GLIDE) && getPlayer(player).isEnabled()) {
            Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
            player.setVelocity(player.getVelocity().add(unitVector.multiply(speed)));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERMISSION_LAUNCH)) return;
        
        ItemStack chestPlate = player.getEquipment().getChestplate();
        if (chestPlate == null || chestPlate.getType() != Material.ELYTRA)
            return;
        
        // start charging up
        if (event.isSneaking()) {
            getPlayer(player).setChargeUpTicks(0);
        }
        
        // release charge
        else {
            if (getPlayer(player).getChargeUpTicks() >= chargeUpTime) {
                Location loc = player.getLocation();
                Vector dir = loc.getDirection().add(new Vector(0, launchStrength, 0));
                
                player.setVelocity(player.getVelocity().add(dir));
                loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
                if(launchSound != null)
                    player.playSound(loc, launchSound.bukkitSound(), 0.1F, 2.0F);
            }
            getPlayer(player).setChargeUpTicks(-1);
        }
    }
    
}
