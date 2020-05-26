package eisenwave.elytra;

import eisenwave.elytra.data.PlayerPreferences;
import java.util.Iterator;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

public class PlayerManager implements Iterable<SuperElytraPlayer> {
    private static PlayerManager instance = null;

    private final WeakHashMap<UUID, SuperElytraPlayer> players;

    private PlayerManager() {
        players = new WeakHashMap<>();
    }

    public static PlayerManager getInstance() {
        if(instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public void loadPlayer(Player player) {
        players.put(player.getUniqueId(), new SuperElytraPlayer(player));
    }

    public void removePlayer(Player player) {
        SuperElytraPlayer sePlayer = players.get(player.getUniqueId());
        players.remove(player.getUniqueId());
        sePlayer.preferences.save(player);
    }

    public SuperElytraPlayer getPlayer(Player player) {
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (players.containsKey(player.getUniqueId())) {
            return players.get(player.getUniqueId());
        }
        else {
            SuperElytraPlayer sePlayer = new SuperElytraPlayer(player);
            players.put(player.getUniqueId(), sePlayer);
            return sePlayer;
        }
    }

    @Override
    public Iterator<SuperElytraPlayer> iterator() {
        return players.values().iterator();
    }
}
