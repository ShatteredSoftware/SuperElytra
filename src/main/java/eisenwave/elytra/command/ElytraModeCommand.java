package eisenwave.elytra.command;

import eisenwave.elytra.PlayerManager;
import eisenwave.elytra.SuperElytraPlayer;
import eisenwave.elytra.SuperElytraPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ElytraModeCommand implements CommandExecutor, TabCompleter {

    private final SuperElytraPlugin plugin;

    public ElytraModeCommand(final SuperElytraPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null.");
        }
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            final HashMap<String, String> vars = new HashMap<>();
            vars.put("argc", String.valueOf(args.length));
            vars.put("argx", "1");
            this.plugin.getMessenger().sendMessage(sender, "not-enough-args", vars, true);
            return true;
        }

        if (!(sender instanceof Player)) {
            this.plugin.getMessenger().sendMessage(sender, "no-console");
            return true;
        }

        final Player player = (Player) sender;
        final SuperElytraPlayer sePlayer = PlayerManager.getInstance().getPlayer(player);
        if (!player.hasPermission("superelytra.command.elytramode")) {
            final HashMap<String, String> vars = new HashMap<>();
            this.plugin.getMessenger().sendErrorMessage(sender, "no-permission", vars, true);
            return true;
        }

        final String parse = args[0];
        if (parse.equalsIgnoreCase("on") || parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("enable")) {
            sePlayer.setEnabled(true);
        } else if (parse.equalsIgnoreCase("off") || parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("disable")) {
            sePlayer.setEnabled(false);
        } else if (parse.equalsIgnoreCase("toggle")) {
            sePlayer.setEnabled(!sePlayer.isEnabled());
        } else {
            final HashMap<String, String> msgArgs = new HashMap<>();
            msgArgs.put("invalid", args[0]);
            msgArgs.put("expected", "'on', 'off', 'true', 'false', 'enable' 'disable', or 'toggle'");
            this.plugin.getMessenger().sendErrorMessage(sender, "invalid-argument", msgArgs, true);
            return true;
        }
        if (sePlayer.isEnabled()) {
            this.plugin.getMessenger().sendMessage(sender, "all-enabled", true);
        } else {
            this.plugin.getMessenger().sendMessage(sender, "all-disabled", true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length != 1) return null;

        final String arg = args[0].toLowerCase();
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
        return Collections.emptyList();
    }

}
