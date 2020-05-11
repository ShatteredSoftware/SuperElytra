package eisenwave.elytra.command;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElytraModeCommand implements CommandExecutor, TabCompleter {

    private final SuperElytraPlugin plugin;
    
    public ElytraModeCommand(SuperElytraPlugin plugin) {
        if(plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null.");
        }
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            HashMap<String, String> vars = new HashMap<>();
            vars.put("argc", String.valueOf(args.length));
            vars.put("argx", "1");
            plugin.getMessenger().sendMessage(sender, "not-enough-args", vars, true);
            return true;
        }
        
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendMessage(sender, "no-console");
            return true;
        }
        
        Player player = (Player) sender;
        SuperElytraPlayer sePlayer = PlayerManager.getInstance().getPlayer(player);
        
        switch (args[0].toLowerCase()) {
            case "normal": {
                sePlayer.setEnabled(false);
            }
            case "super": {
                sePlayer.setEnabled(true);
            }
            default: {
                sePlayer.setEnabled(!sePlayer.isEnabled());
            }
        }
        if(sePlayer.isEnabled()) {
            plugin.getMessenger().sendMessage(sender, "all-enabled", true);
        }
        else {
            plugin.getMessenger().sendMessage(sender, "all-disabled", true);
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return null;
        
        String arg = args[0].toLowerCase();
        if (arg.isEmpty())
            return Arrays.asList("normal", "super");
        else if ("normal".startsWith(arg))
            return Collections.singletonList("normal");
        else if ("super".startsWith(arg))
            return Collections.singletonList("super");
        else
            return Collections.emptyList();
    }
    
}
