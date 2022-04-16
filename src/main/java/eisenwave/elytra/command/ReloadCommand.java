package eisenwave.elytra.command;

import eisenwave.elytra.SuperElytraPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final SuperElytraPlugin plugin;

    public ReloadCommand(final SuperElytraPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s,
                             final String[] strings) {
        if (commandSender.hasPermission("superelytra.command.reload")) {
            this.plugin.reload();
            this.plugin.getMessenger().sendMessage(commandSender, "reloaded", true);
        } else {
            this.plugin.getMessenger().sendMessage(commandSender, "no-permission", true);
        }
        return true;
    }
}
