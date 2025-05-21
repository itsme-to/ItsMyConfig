package to.itsme.itsmyconfig.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import to.itsme.itsmyconfig.util.IMCSerializer;
import to.itsme.itsmyconfig.util.Strings;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Optional;

public class PlayerListener implements Listener {

    private static final String DEBUG = "Detected a %s message with the following message: %s";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreJoin(final AsyncPlayerPreLoginEvent event) {
        final AsyncPlayerPreLoginEvent.Result result = event.getLoginResult();
        if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        final Component kick = event.kickMessage();
        final String miniMessage = IMCSerializer.toMiniMessage(kick);
        final Optional<String> parsed = Strings.parsePrefixedMessage(miniMessage);
        if (parsed.isPresent()) {
            final String message = parsed.get();
            Utilities.debug(() -> DEBUG.formatted("disallowed prejoin", message));
            final OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
            event.kickMessage(Utilities.translate(message, player));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLoginBanned(final PlayerLoginEvent event) {
        final PlayerLoginEvent.Result result = event.getResult();
        if (result == PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        final Component kick = event.kickMessage();
        final String miniMessage = IMCSerializer.toMiniMessage(kick);
        final Optional<String> parsed = Strings.parsePrefixedMessage(miniMessage);
        if (parsed.isPresent()) {
            final String message = parsed.get();
            Utilities.debug(() -> DEBUG.formatted("disallowed while logged in", message));
            event.kickMessage(Utilities.translate(message, event.getPlayer()));
        }
    }

}
