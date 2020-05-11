package eisenwave.elytra.command;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.data.PlayerPreferences;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ElytraToggleCommand implements CommandExecutor, TabCompleter {
    private final SuperElytraPlugin plugin;

    public ElytraToggleCommand(SuperElytraPlugin plugin) {
        if(plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null.");
        }
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s,
        String[] args) {
        if(!(commandSender instanceof Player)) {
            plugin.getMessenger().sendMessage(commandSender, "no-console");
            return true;
        }
        Player player = (Player) commandSender;
        if(!player.hasPermission("superelytra.command.elytraprefs")) {
            plugin.getMessenger().sendErrorMessage(player, "no-permission", true);
            return true;
        }
        if(args.length < 1) {
            HashMap<String, String> vars = new HashMap<>();
            vars.put("argc", String.valueOf(args.length));
            vars.put("argx", "more than 1");
            plugin.getMessenger().sendErrorMessage(player, "not-enough-args", vars, true);
            return true;
        }
        PlayerPreferences prefs = PlayerManager.getInstance().getPlayer(player).preferences;
        if(args[0].equalsIgnoreCase("launch")) {
            if(args.length == 1) {
                prefs.launch = !prefs.launch;
            }
            if(args.length == 2) {
                String parse = args[1];
                if(parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
                    prefs.launch = true;
                }
                else if(parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
                    prefs.launch = false;
                }
            }
            if(prefs.launch) {
                plugin.getMessenger().sendMessage(commandSender, "launch-enabled");
            }
            else {
                plugin.getMessenger().sendMessage(commandSender, "launch-disabled");
            }
        }
        if(args[0].equalsIgnoreCase("boost")) {
            if(args.length == 1) {
                prefs.boost = !prefs.boost;
            }
            if(args.length == 2) {
                String parse = args[1];
                if(parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
                    prefs.boost = true;
                }
                else if(parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
                    prefs.boost = false;
                }
                else if(parse.equalsIgnoreCase("toggle")) {
                    prefs.boost = !prefs.boost;
                }
            }
            if(prefs.boost) {
                plugin.getMessenger().sendMessage(commandSender, "boost-enabled");
            }
            else {
                plugin.getMessenger().sendMessage(commandSender, "boost-disabled");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s,
        String[] args) {
        if (args.length == 0) return null;

        if(args.length == 1) {
            String arg = args[0].toLowerCase();
            if (arg.isEmpty())
                return Arrays.asList("launch", "boost");
            else if ("launch".startsWith(arg))
                return Collections.singletonList("launch");
            else if ("boost".startsWith(arg))
                return Collections.singletonList("boost");
            else
                return Collections.emptyList();
        }
        if(args.length == 2) {
            String parent = args[0].toLowerCase();
            String arg = args[1].toLowerCase();
            if(parent.equalsIgnoreCase("launch")) {
                if (arg.isEmpty())
                    return Arrays.asList("enable", "disable", "toggle");
                else if("enable".startsWith(arg)) {
                    return Collections.singletonList("enable");
                }
                else if("true".startsWith(arg)) {
                    return Collections.singletonList("true");
                }
                else if("on".startsWith(arg)) {
                    return Collections.singletonList("on");
                }
                else if("disable".startsWith(arg)) {
                    return Collections.singletonList("disable");
                }
                else if("false".startsWith(arg)) {
                    return Collections.singletonList("false");
                }
                else if("off".startsWith(arg)) {
                    return Collections.singletonList("off");
                }
                else if("toggle".startsWith(arg)) {
                    return Collections.singletonList("toggle");
                }
            }
            else if(parent.equalsIgnoreCase("boost")) {
                if (arg.isEmpty())
                    return Arrays.asList("enable", "disable", "toggle");
                else if("enable".startsWith(arg)) {
                    return Collections.singletonList("enable");
                }
                else if("true".startsWith(arg)) {
                    return Collections.singletonList("true");
                }
                else if("on".startsWith(arg)) {
                    return Collections.singletonList("on");
                }
                else if("disable".startsWith(arg)) {
                    return Collections.singletonList("disable");
                }
                else if("false".startsWith(arg)) {
                    return Collections.singletonList("false");
                }
                else if("off".startsWith(arg)) {
                    return Collections.singletonList("off");
                }
                else if("toggle".startsWith(arg)) {
                    return Collections.singletonList("toggle");
                }
            }
        }
        return Collections.emptyList();
    }
}
