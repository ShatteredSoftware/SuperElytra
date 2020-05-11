package eisenwave.elytra.command;

import eisenwave.elytra.SuperElytraPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final SuperElytraPlugin plugin;

    public ReloadCommand(SuperElytraPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s,
        String[] strings) {
        if(commandSender.hasPermission("superelytra.command.reload")) {
            plugin.reload();
            plugin.getMessenger().sendMessage(commandSender, "reloaded", true);
        }
        else {
            plugin.getMessenger().sendMessage(commandSender, "no-permission", true);
        }
        return true;
    }
}
