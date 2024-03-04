package to.itsme.itsmyconfig.command.impl;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Message;
import to.itsme.itsmyconfig.util.Utilities;

@Command("itsmyconfig")
public class ItsMyConfigCommand {

    private final ItsMyConfig plugin;

    public ItsMyConfigCommand(final ItsMyConfig plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("~")
    public void usage(final CommandActor actor) {
        Message.INVALID_USE.send(actor, Placeholder.parsed("usage", "itsmyconfig <message/reload>"));
    }

    @Subcommand("reload")
    @CommandPermission("itsmyconfig.reload")
    public void reload(final BukkitCommandActor actor) {
        plugin.loadConfig();
        Message.RELOAD.send(actor);
    }

    @Subcommand("message")
    @CommandPermission("itsmyconfig.message")
    public void message(
            final BukkitCommandActor actor,
            @Named("target") final EntitySelector<Player> players,
            @Named("message") final String message
    ) {
        players.forEach(player -> {
            final String[] strings = message.split("\\\\r?\\\\n|\\\\r");
            for (final String string : strings) {
                player.sendMessage(this.plugin.getSymbolPrefix() + string);
            }
        });

        if (actor.isPlayer()) {
            Message.MESSAGE_SENT.send(actor);
        }
    }

    @Subcommand("config")
    @CommandPermission("itsmyconfig.config")
    public void config(
            final BukkitCommandActor actor,
            @Named("placeholder") final String placeholder,
            @Named("value") final String value
    ) {
        final FileConfiguration config = plugin.getConfig();
        final ConfigurationSection section = config.getConfigurationSection("custom-placeholder." + placeholder);
        if (section == null) {
            actor.reply(Utilities.MM.deserialize("<red>Placeholder <yellow>" + placeholder + "</yellow> was not found.</red>"));
            return;
        }

        final PlaceholderType type = PlaceholderType.find(section.getString("type"));
        if (type == PlaceholderType.ANIMATED || type == PlaceholderType.RANDOM) {
            actor.reply(Utilities.MM.deserialize("<red>Placeholder <yellow>" + placeholder + "</yellow>'s type is not supported via commands.</red>"));
            return;
        }

        section.set("value", value);
        plugin.saveConfig();
        plugin.loadConfig();
        actor.reply(Utilities.MM.deserialize("<green>Placeholder <yellow>" + placeholder + "</yellow>'s value was updated successfully!</green>"));
    }

    @Command("message")
    @CommandPermission("itsmyconfig.message")
    public void msgCommand(
            final BukkitCommandActor actor,
            @Named("target") final EntitySelector<Player> players,
            @Named("message") final String message
    ) {
        this.message(actor, players, message);
    }

    @Command("config")
    @CommandPermission("itsmyconfig.config")
    public void configCommand(
            final BukkitCommandActor actor,
            @Named("placeholder") final String placeholder,
            @Named("value") final String value
    ) {
        this.config(actor, placeholder, value);
    }

}
