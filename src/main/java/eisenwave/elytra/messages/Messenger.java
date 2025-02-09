package eisenwave.elytra.messages;

import eisenwave.elytra.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * A class to send messages to {@link CommandSender CommandSenders}.
 *
 * @author UberPilot
 * @since 1.0.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Messenger {

    private final transient JavaPlugin instance;
    private final Messages messages;

    /**
     * Create a messenger.
     *
     * @param instance The instance of ShatteredDonations. Dependency injection.
     * @param messages MessageSet to link to.
     */
    public Messenger(final JavaPlugin instance, final Messages messages) {
        this.instance = instance;
        this.messages = messages;
    }

    public String getMessage(final String id, final Map<String, String> args) {
        String message = this.messages.getMessage(id);

        if (message == null) {
            this.instance.getLogger().severe("Failed to load message with id " + id);
            return "";
        }

        if (args != null) {
            for (final Map.Entry<String, String> entry : args.entrySet()) {
                message = message.replaceAll('%' +
                                entry.getKey() + '%',
                        entry.getValue());
            }
        }
        return message;
    }

    /**
     * Sends a message without any placeholders.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     */
    public void sendMessage(final CommandSender sender, final String id) {
        this.sendMessage(sender, id, null, false);
    }

    /**
     * Sends a message without any placeholders.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param prefix Whether to append the prefix.
     */
    public void sendMessage(final CommandSender sender, final String id, final boolean prefix) {
        this.sendMessage(sender, id, null, prefix);
    }

    /**
     * Sends a message with placeholders.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param vars   The list of placeholders to replace.
     */
    public void sendMessage(final CommandSender sender, final String id, final Map<String, String> vars) {
        this.sendMessage(sender, id, vars, false);
    }

    /**
     * Sends a message with placeholders.
     *
     * @param sender       The sender to send a message to.
     * @param id           The id of the message from {@link Messages} to send.
     * @param vars         The list of placeholders to replace.
     * @param appendPrefix Whether to append the prefix.
     */
    public void sendMessage(
            final CommandSender sender, final String id, final Map<String, String> vars, final boolean appendPrefix) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null.");
        }

        String prefix = "";
        if (appendPrefix) {
            prefix = this.messages.getMessage("prefix");
            if (prefix == null) {
                prefix = "";
            }
        }

        String message = this.messages.getMessage(id);

        if (message == null) {
            this.instance.getLogger().severe("Failed to load message with id " + id);
            return;
        }

        if (vars != null) {
            for (final Map.Entry<String, String> entry : vars.entrySet()) {
                message = message.replaceAll('%' + entry.getKey() + '%',
                        entry.getValue());
            }
        }
        sender.sendMessage(prefix + message);
    }

    /**
     * Sends an error message without any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     */
    public void sendErrorMessage(final CommandSender sender, final String id) {
        this.sendErrorMessage(sender, id, null, false);
    }

    /**
     * Sends an error message without any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param prefix Whether to append the prefix.
     */
    public void sendErrorMessage(final CommandSender sender, final String id, final boolean prefix) {
        this.sendErrorMessage(sender, id, null, prefix);
    }

    /**
     * Sends an error message with any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param vars   The list of placeholders to replace.
     */
    public void sendErrorMessage(final CommandSender sender, final String id, final Map<String, String> vars) {
        this.sendMessage(sender, id, vars, false);
    }

    /**
     * Sends an error message with any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param vars   The list of placeholders to replace.
     * @param prefix Whether to append the prefix.
     */
    public void sendErrorMessage(
            final CommandSender sender, final String id, final Map<String, String> vars, final boolean prefix) {
        this.sendMessage(sender, id, vars, prefix);
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.NOTE_BASS.bukkitSound(), 1, .8f);
        }
    }

    /**
     * Sends an important message without any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     */
    public void sendImportantMessage(final CommandSender sender, final String id) {
        this.sendImportantMessage(sender, id, null);
    }

    /**
     * Sends an important message with any placeholders. Includes a sound effect.
     *
     * @param sender The sender to send a message to.
     * @param id     The id of the message from {@link Messages} to send.
     * @param vars   The list of placeholders to replace.
     */
    public void sendImportantMessage(final CommandSender sender, final String id, final Map<String, String> vars) {
        this.sendMessage(sender, id, vars);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.ORB_PICKUP.bukkitSound(), 1, .5F);
            Bukkit.getScheduler()
                    .scheduleSyncDelayedTask(
                            this.instance,
                            () -> player
                                    .playSound(player.getLocation(), Sound.ORB_PICKUP.bukkitSound(), 1, .5F),
                            4L);
        }
    }
}
