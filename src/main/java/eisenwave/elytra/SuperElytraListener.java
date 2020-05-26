package eisenwave.elytra;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SuperElytraListener implements Listener {
    
    // STATIC CONST
    
    private final static String
        PERMISSION_LAUNCH = "superelytra.launch",
        PERMISSION_GLIDE = "superelytra.glide";

    private transient SuperElytraPlugin plugin;
    public SuperElytraListener(SuperElytraPlugin plugin) {
        this.plugin = plugin;
    }
    
    /*public static String[] splitIntoParts(String str, int partLength) {
        int strLength = str.length();
        String[] parts = new String[(int) Math.ceil(strLength / (float) partLength)];
        for (int i = 0; i < parts.length; i++)
            parts[i] = str.substring(i * partLength, Math.min(strLength, (i + 1) * partLength));
        return parts;
    }*/

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerManager.getInstance().removePlayer(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    public void onTick() {
        for (SuperElytraPlayer sePlayer : PlayerManager.getInstance()) {
            Player player = sePlayer.getPlayer();
            if (!player.isOnGround() || !sePlayer.isChargingLaunch()) continue;
            
            int time = sePlayer.getChargeUpTicks();
            sePlayer.setChargeUpTicks(++time);
            
            Location loc = player.getLocation();
            World world = player.getWorld();
    
            world.spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0.2F, 0.2F, 0.2F, 0.0F); // radius 30
            if (time % 3 == 0) {
                if(plugin.config().chargeSound != null)
                    player.playSound(player.getLocation(), plugin.config().chargeSound.bukkitSound(), 0.1F, 0.1F);
                if (time >= plugin.config().chargeupTicks) {
                    world.spawnParticle(Particle.FLAME, loc, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                    if(plugin.config().readySound != null)
                        player.playSound(player.getLocation(), plugin.config().readySound.bukkitSound(), 0.1F, 0.1F);
                }
            }
        }
    }
    
    // BUKKIT EVENT HANDLERS
    
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!player.isGliding()) {
            return;
        }
        if(!player.hasPermission(PERMISSION_GLIDE)) {
            return;
        }
        SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);
        if(!superElytraPlayer.isEnabled() || !superElytraPlayer.preferences.boost) {
            return;
        }
        Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
        player.setVelocity(player.getVelocity().add(unitVector.multiply(plugin.config().speed)));
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERMISSION_LAUNCH)) return;
        
        ItemStack chestPlate = player.getEquipment().getChestplate();
        if (chestPlate == null || chestPlate.getType() != Material.ELYTRA)
            return;
        SuperElytraPlayer superElytraPlayer = PlayerManager.getInstance().getPlayer(player);
        if(!superElytraPlayer.isEnabled() || !superElytraPlayer.preferences.launch) {
            return;
        }

        // start charging up
        if (event.isSneaking()) {
            PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(0);
        }
        
        // release charge
        else {
            if (PlayerManager.getInstance().getPlayer(player).getChargeUpTicks() >= plugin.config().chargeupTicks) {
                Location loc = player.getLocation();
                Vector dir = loc.getDirection().add(new Vector(0, plugin.config().launch, 0));
                
                player.setVelocity(player.getVelocity().add(dir));
                loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.5F, 0.5F, 0.5F, 0.0F);
                if(plugin.config().launchSound != null)
                    player.playSound(loc, plugin.config().launchSound.bukkitSound(), 0.1F, 2.0F);
            }
            PlayerManager.getInstance().getPlayer(player).setChargeUpTicks(-1);
        }
    }
    
}
