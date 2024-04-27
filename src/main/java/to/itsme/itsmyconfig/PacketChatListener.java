package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.AdventureComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class PacketChatListener extends PacketAdapter {

    private final ItsMyConfig plugin;
    private final Method fromComponent;
    private final boolean internalAdventure;
    private final Pattern colorSymbolPattern, symbolPrefixPattern;
    private final Pattern tagPattern = Pattern.compile("<(?:\\\\.|[^<>])*>");
    private final BungeeComponentSerializer bungee = BungeeComponentSerializer.get();

    public PacketChatListener(
            final ItsMyConfig plugin,
            final PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
        this.colorSymbolPattern = Pattern.compile(Pattern.quote("§"));
        this.symbolPrefixPattern = Pattern.compile(Pattern.quote(plugin.getSymbolPrefix()));
        Method fromComponent;
        try {
            fromComponent = AdventureComponentConverter.class.getDeclaredMethod(
                    "fromComponent",
                    AdventureComponentConverter.getComponentClass()
            );
        } catch (final Throwable ignored) {
            fromComponent = null;
        }

        this.fromComponent = fromComponent;
        this.internalAdventure = this.fromComponent != null;
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        Utilities.debug("######################################");
        final String message = this.processPacket(packetContainer);
        if (message == null || message.isEmpty()) {
            Utilities.debug("######################################");
            return;
        }

        Utilities.debug("Checking: " + message);
        if (!this.startsWithSymbol(message)) {
            Utilities.debug("Message doesn't start w/ the symbol-prefix: " + message);
            Utilities.debug("######################################");
            return;
        }

        event.setCancelled(true);
        final Player player = event.getPlayer();
        final Component parsed = Utilities.translate(this.processMessage(message), player);
        Utilities.debug("######################################");
        Utilities.applyChatColors(parsed);
        if (!parsed.equals(Component.empty())) {
            plugin.adventure().player(player).sendMessage(parsed);
        }
    }

    /**
     * Checks if the provided message starts with the "$" symbol
     * @param message the checked message
     */
    private boolean startsWithSymbol(final String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        return tagPattern.matcher(Utilities.colorless(message)).replaceAll("").trim().startsWith(plugin.getSymbolPrefix());
    }

    /**
     * Removes the '§' symbol and replaces it with '&'
     * <br>
     * Also removes the first '$' symbol it meets
     *
     * @param message the provided message
     */
    private String processMessage(final String message) {
        return colorSymbolPattern.matcher(symbolPrefixPattern.matcher(message).replaceFirst("")).replaceAll("&");
    }

    private String processPacket(final PacketContainer container) {
        Utilities.debug("Proccessing a packet");
        if (internalAdventure) {
            try {
                final StructureModifier<?> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());
                if (modifier.size() == 1) {
                    final WrappedChatComponent wrappedComponent = (WrappedChatComponent) fromComponent.invoke(null, modifier.readSafely(0));
                    final String json = wrappedComponent.getJson();
                    Utilities.debug("Performing Server-Side Adventure for " + json);
                    return AbstractComponent.parse(json).toMiniMessage();
                } else {
                    Utilities.debug("Failed to use Server-Side Adventure, Trying Bungeecord TextComponent..");
                }
            } catch (Throwable ignored) {
                Utilities.debug("Failed to use Server-Side Adventure, Trying Bungeecord TextComponent..");
            }
        }

        final StructureModifier<TextComponent> textComponentModifier = container.getModifier().withType(TextComponent.class);
        if (textComponentModifier.size() == 1) {
            Utilities.debug("Using Bungeecord TextComponent..");
            return processBaseComponents(textComponentModifier.readSafely(0));
        } else {
            Utilities.debug("Failed to use Bungeecord TextComponent, trying ProtocolLib's WrappedChatComponent");
        }

        final WrappedChatComponent wrappedComponent = container.getChatComponents().readSafely(0);
        if (wrappedComponent != null) {
            final String found = wrappedComponent.getJson();
            if (!found.isEmpty()) {
                Utilities.debug("Found String: " + found);
                try {
                    Utilities.debug("Trying as json");
                    return AbstractComponent.parse(found).toMiniMessage();
                } catch (final Exception e) {
                    Utilities.debug("An error happened while de/serializing " + found + ": ", e);
                }
            }
        }

        final String rawMessage = container.getStrings().readSafely(0);
        if (rawMessage == null) {
            Utilities.debug("Found nothing.. returning null.");
            return null;
        }

        Utilities.debug("Raw-Parsing message: " + rawMessage);
        return AbstractComponent.parse(rawMessage).toMiniMessage();
    }

    private String processBaseComponents(final BaseComponent... components) {
        return AbstractComponent.parse(bungee.deserialize(components)).toMiniMessage();
    }

}
