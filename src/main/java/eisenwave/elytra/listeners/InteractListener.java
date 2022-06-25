package eisenwave.elytra.listeners;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.errors.ErrorLogger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class InteractListener extends BaseListener<PlayerInteractEvent> implements Listener {

    public InteractListener(SuperElytraPlugin plugin, ErrorLogger errorLogger, PlayerManager playerManager) {
        super(plugin, errorLogger, playerManager);
    }

    @EventHandler
    public void onBoost(final PlayerInteractEvent event) {
        this.checkPredicates(event,
                this::shouldHandleEvent,
                checkPreference((prefs) -> prefs.firework),
                (playerEvent) -> playerEvent.getAction() == Action.RIGHT_CLICK_AIR,
                (playerEvent) -> {
                    final ItemStack item = playerEvent.getItem();
                    return item != null && item.getType() == Material.FIREWORK_ROCKET;
                }
        ).then(() -> {
            SuperElytraPlayer player = playerManager.getPlayer(event.getPlayer());
            ItemStack item = event.getItem();
            final FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            player.setBoostTicks((meta.getPower() + 1) * this.plugin.config().boostDuration);
        }).run();
    }
}
