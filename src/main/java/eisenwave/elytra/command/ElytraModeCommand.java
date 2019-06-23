package eisenwave.elytra.command;

import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
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
    
    private static final String
        PREFIX_ERR = ChatColor.RED + "[SuperElytra] " + ChatColor.RESET,
        PREFIX_MSG = ChatColor.BLUE + "[SuperElytra] " + ChatColor.RESET,
        PREFIX_USE = ChatColor.RED + "Usage: /",
        USAGE = PREFIX_USE + "elytramode (normal|super)",
        ERROR_BAD_PLAYER = PREFIX_ERR + "Only players can toggle their elytra mode",
        MSG_ON = PREFIX_MSG + "You enabled enhanced flight",
        MSG_OFF = PREFIX_MSG + "You disabled enhanced flight";
    
    private final SuperElytraPlugin plugin;
    
    public ElytraModeCommand(SuperElytraPlugin plugin) {
        if(plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null.");
        }
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
        if (args.length < 1) return false;
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ERROR_BAD_PLAYER);
            return true;
        }
        
        Player player = (Player) sender;
        SuperElytraPlayer sePlayer = plugin.getEventHandler().getPlayer(player);
        
        switch (args[0]) {
            case "normal": {
                sePlayer.setEnabled(false);
                player.sendMessage(MSG_OFF);
                return true;
            }
            case "super": {
                sePlayer.setEnabled(true);
                player.sendMessage(MSG_ON);
                return true;
            }
            default: return false;
        }
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
