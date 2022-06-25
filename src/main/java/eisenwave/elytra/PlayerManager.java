package eisenwave.elytra;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.UUID;
import java.util.WeakHashMap;

public class PlayerManager implements Iterable<SuperElytraPlayer> {
    private final WeakHashMap<UUID, SuperElytraPlayer> players;

    public PlayerManager() {
        this.players = new WeakHashMap<>();
    }

    public void loadPlayer(final Player player) {
        this.players.put(player.getUniqueId(), new SuperElytraPlayer(player));
    }

    public void removePlayer(final Player player) {
        final SuperElytraPlayer sePlayer = this.players.get(player.getUniqueId());
        if (sePlayer == null) {
            return;
        }
        this.players.remove(player.getUniqueId());
        sePlayer.preferences.save(player.getUniqueId());
    }

    public SuperElytraPlayer getPlayer(final Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (this.players.containsKey(player.getUniqueId())) {
            return this.players.get(player.getUniqueId());
        } else {
            final SuperElytraPlayer sePlayer = new SuperElytraPlayer(player);
            this.players.put(player.getUniqueId(), sePlayer);
            return sePlayer;
        }
    }

    @Override
    public Iterator<SuperElytraPlayer> iterator() {
        return this.players.values().iterator();
    }
}
