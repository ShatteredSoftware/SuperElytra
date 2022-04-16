package eisenwave.elytra.command;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlugin;
import eisenwave.elytra.data.PlayerPreferences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ElytraToggleCommand implements CommandExecutor, TabCompleter {
    private final SuperElytraPlugin plugin;

    public ElytraToggleCommand(final SuperElytraPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null.");
        }
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s,
                             final String[] args) {
        if (!(commandSender instanceof Player)) {
            this.plugin.getMessenger().sendMessage(commandSender, "no-console");
            return true;
        }
        final Player player = (Player) commandSender;
        if (!player.hasPermission("superelytra.command.elytraprefs")) {
            this.plugin.getMessenger().sendErrorMessage(player, "no-permission", true);
            return true;
        }
        if (args.length < 1) {
            final HashMap<String, String> vars = new HashMap<>();
            vars.put("argc", String.valueOf(args.length));
            vars.put("argx", "more than 1");
            this.plugin.getMessenger().sendErrorMessage(player, "not-enough-args", vars, true);
            return true;
        }
        final PlayerPreferences prefs = PlayerManager.getInstance().getPlayer(player).preferences;
        if (args[0].equalsIgnoreCase("launch")) {
            if (args.length == 1) {
                prefs.launch = !prefs.launch;
            }
            if (args.length == 2) {
                final String parse = args[1];
                if (parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
                    prefs.launch = true;
                } else if (parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
                    prefs.launch = false;
                } else if (parse.equalsIgnoreCase("toggle")) {
                    prefs.launch = !prefs.launch;
                } else {
                    final HashMap<String, String> msgArgs = new HashMap<>();
                    msgArgs.put("invalid", args[1]);
                    msgArgs.put("exptected", "'on', 'off', 'true', 'false', 'enable' 'disable', or 'toggle'");
                    this.plugin.getMessenger().sendErrorMessage(commandSender, "invalid-argument", msgArgs, true);
                    return true;
                }
            }
            if (prefs.launch) {
                this.plugin.getMessenger().sendMessage(commandSender, "launch-enabled");
            } else {
                this.plugin.getMessenger().sendMessage(commandSender, "launch-disabled");
            }
        }
        if (args[0].equalsIgnoreCase("boost")) {
            if (args.length == 1) {
                prefs.boost = !prefs.boost;
            }
            if (args.length == 2) {
                final String parse = args[1];
                if (parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
                    prefs.boost = true;
                } else if (parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
                    prefs.boost = false;
                } else if (parse.equalsIgnoreCase("toggle")) {
                    prefs.boost = !prefs.boost;
                } else {
                    final HashMap<String, String> msgArgs = new HashMap<>();
                    msgArgs.put("invalid", args[1]);
                    msgArgs.put("exptected", "'on', 'off', 'true', 'false', 'enable' 'disable', or 'toggle'");
                    this.plugin.getMessenger().sendErrorMessage(commandSender, "invalid-argument", msgArgs, true);
                    return true;
                }
            }
            if (prefs.boost) {
                this.plugin.getMessenger().sendMessage(commandSender, "boost-enabled", true);
            } else {
                this.plugin.getMessenger().sendMessage(commandSender, "boost-disabled", true);
            }
        }
        if (args[0].equalsIgnoreCase("firework")) {
            if (args.length == 1) {
                prefs.firework = !prefs.firework;
            }
            if (args.length == 2) {
                final String parse = args[1];
                if (parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
                    prefs.firework = true;
                } else if (parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
                    prefs.firework = false;
                } else if (parse.equalsIgnoreCase("toggle")) {
                    prefs.firework = !prefs.firework;
                } else {
                    final HashMap<String, String> msgArgs = new HashMap<>();
                    msgArgs.put("invalid", args[1]);
                    msgArgs.put("exptected", "'on', 'off', 'true', 'false', 'enable' 'disable', or 'toggle'");
                    this.plugin.getMessenger().sendErrorMessage(commandSender, "invalid-argument", msgArgs, true);
                    return true;
                }
            }
            if (prefs.firework) {
                this.plugin.getMessenger().sendMessage(commandSender, "firework-enabled", true);
            } else {
                this.plugin.getMessenger().sendMessage(commandSender, "firework-disabled", true);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s,
                                      final String[] args) {
        if (args.length == 0) return null;

        if (args.length == 1) {
            final String arg = args[0].toLowerCase();
            if (arg.isEmpty())
                return Arrays.asList("launch", "boost", "firework");
            else if ("launch".startsWith(arg))
                return Collections.singletonList("launch");
            else if ("boost".startsWith(arg))
                return Collections.singletonList("boost");
            else if ("firework".startsWith(arg))
                return Collections.singletonList("firework");
            else
                return Collections.emptyList();
        }
        if (args.length == 2) {
            final String parent = args[0].toLowerCase();
            final String arg = args[1].toLowerCase();
            if (parent.equalsIgnoreCase("launch") || parent.equalsIgnoreCase("boost") || parent.equalsIgnoreCase("firework")) {
                if (arg.isEmpty())
                    return Arrays.asList("enable", "disable", "toggle");
                else if ("enable".startsWith(arg)) {
                    return Collections.singletonList("enable");
                } else if ("true".startsWith(arg)) {
                    return Collections.singletonList("true");
                } else if ("on".startsWith(arg)) {
                    return Collections.singletonList("on");
                } else if ("disable".startsWith(arg)) {
                    return Collections.singletonList("disable");
                } else if ("false".startsWith(arg)) {
                    return Collections.singletonList("false");
                } else if ("off".startsWith(arg)) {
                    return Collections.singletonList("off");
                } else if ("toggle".startsWith(arg)) {
                    return Collections.singletonList("toggle");
                }
            }
        }
        return Collections.emptyList();
    }
}
