package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.AdventureComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.parser.MinecraftComponent;
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.Method;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PacketChatListener extends PacketAdapter {

    private final ItsMyConfig plugin;
    private final Method fromComponent;
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
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        final String message = this.processPacket(packetContainer);
        if (message == null || message.isEmpty()) {
            return;
        }

        if (!this.startsWithSymbol(message)) {
            return;
        }

        event.setCancelled(true);
        final Player player = event.getPlayer();
        final Component parsed = fixClickEvent(
                Utilities.EMPTY_MM.deserialize(
                        this.processMessage(message),
                        Utilities.itsMyConfigTag(player), Utilities.papiTag(player),
                        StandardTags.defaults(), Utilities.playerSubtags(player)
                )
        );
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

    /**
     * Serialized then deserialized components with a click event have their value starting with "&f"
     * <br>
     * This fixes it.
     *
     * @return  the fixed component
     */
    private Component fixClickEvent(final Component component) {
        final ClickEvent event = component.clickEvent();
        Component copied = component;

        // Serialized then deserialized components with a click event have their value starting with "&f".
        if (event != null && event.value().startsWith("&f")) {
            copied = component.clickEvent(ClickEvent.clickEvent(event.action(), event.value().substring(2)));
        }

        copied = copied.children(copied.children().stream().map(this::fixClickEvent).collect(Collectors.toList()));
        return copied;
    }

    private String processPacket(final PacketContainer container) {
        try {
            final StructureModifier<?> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());
            if (modifier.size() == 1) {
                final WrappedChatComponent wrappedComponent = (WrappedChatComponent) fromComponent.invoke(null, modifier.readSafely(0));
                return MinecraftComponent.parse(wrappedComponent.getJson()).toMiniMessage();
            }
        } catch (Throwable ignored) {}

        final StructureModifier<TextComponent> textComponentModifier = container.getModifier().withType(TextComponent.class);
        if (textComponentModifier.size() == 1) {
            return processBaseComponents(textComponentModifier.readSafely(0));
        }

        final WrappedChatComponent wrappedComponent = container.getChatComponents().readSafely(0);
        if (wrappedComponent != null) {
            final String json = wrappedComponent.getJson();
            if (!json.isEmpty()) {
                try {
                    return MinecraftComponent.parse(json).toMiniMessage();
                } catch (final IllegalStateException | JsonSyntaxException e) {
                    if (e.getMessage().contains("Not a JSON Object")) {
                        return json;
                    } else {
                        throw new RuntimeException("An error happened while de/serializing " + json, e);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException("An error happened while de/serializing " + json, e);
                }
            }
        }

        return parseString(container.getStrings().readSafely(0));
    }

    private String parseString(final String rawMessage) {
        if (rawMessage == null) {
            return null;
        }

        return processBaseComponents(net.md_5.bungee.chat.ComponentSerializer.parse(rawMessage));
    }

    private String processBaseComponents(final BaseComponent... components) {
        return MinecraftComponent.parse(bungee.deserialize(components)).toMiniMessage();
    }

}
