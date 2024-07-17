package to.itsme.itsmyconfig.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.command.CommandActor;
import to.itsme.itsmyconfig.ItsMyConfig;

import java.util.List;

public enum Message {
    INVALID_USE("invalid-use"),
    RELOAD("reload"),
    NO_PERMISSION("no-permission"),
    MESSAGE_SENT("message-sent");

    private static final ItsMyConfig plugin = ItsMyConfig.getInstance();
    private final String path;

    Message(final String path) {
        this.path = path;
    }

    public void send(final Player player, final TagResolver... resolvers) {
        final Component component = Utilities.translate(this.toString(), player, resolvers);
        plugin.adventure().player(player).sendMessage(component);
    }

    public void send(final CommandActor actor, final TagResolver... replacers) {
        send((BukkitCommandActor) actor, replacers);
    }

    public void send(final BukkitCommandActor actor, final TagResolver... replacers) {
        if (actor.isPlayer()) {
            send(actor.requirePlayer(), replacers);
        } else {
            actor.reply(Utilities.MM.deserialize(toString(), replacers));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        final Object msg = plugin.getConfig().get("messages." + this.path);

        final String result;
        if (msg instanceof List<?>) {
            result = Strings.toString((List<String>) msg);
        } else if (msg instanceof String) {
            result = (String) msg;
        } else {
            result = "";
        }

        return result;
    }

}
