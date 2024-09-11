package to.itsme.itsmyconfig.listener.impl;

import com.comphenix.protocol.PacketType;
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
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.listener.PacketListener;
import to.itsme.itsmyconfig.util.Strings;
import to.itsme.itsmyconfig.util.Utilities;
import to.itsme.itsmyconfig.util.Versions;

@SuppressWarnings("deprecation")
public final class PacketChatListener extends PacketListener {

    private static final String DEBUG_HYPHEN = "###############################################";

    private final boolean internalAdventure;
    private final BungeeComponentSerializer bungee = BungeeComponentSerializer.get();

    public PacketChatListener(
            final ItsMyConfig plugin
    ) {
        super(
                plugin,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.KICK_DISCONNECT
        );

        this.internalAdventure = Versions.IS_PAPER;
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer container = event.getPacket();
        Utilities.debug(() -> "################# CHAT PACKET #################\nProccessing packet " + container.getType().name());
        final PacketResponse response = this.processPacket(container);
        if (response == null || response.message.isEmpty()) {
            Utilities.debug(() -> "Packet is null or empty\n" + DEBUG_HYPHEN);
            return;
        }

        final String message = response.message;
        Utilities.debug(() -> "Checking: " + message);
        if (!Strings.startsWithSymbol(message)) {
            Utilities.debug(() -> "Message doesn't start w/ the symbol-prefix: " + message + "\n" + DEBUG_HYPHEN);
            return;
        }

        final Player player = event.getPlayer();
        final Component parsed = Utilities.translate(Strings.processMessage(message), player);
        if (parsed.equals(Component.empty())) {
            event.setCancelled(true);
            Utilities.debug(() -> "Component is empty, cancelling...\n" + DEBUG_HYPHEN);
            return;
        }

        Utilities.debug(() -> "Overriding Message as " + response.type.name());
        switch (response.type) {
            case JSON:
                container.getStrings().write(0, Utilities.GSON_SERIALIZER.serialize(parsed));
                break;
            case WRAPPED_COMPONENT:
                container.getChatComponents().write(0, WrappedChatComponent.fromJson(
                        Utilities.GSON_SERIALIZER.serialize(parsed)
                ));
                break;
            case BUNGEE_COMPONENT:
                container.getModifier().withType(TextComponent.class).write(0, new TextComponent(
                        bungee.serialize(parsed)
                ));
                break;
            case SERVER_ADVENTURE:
                final StructureModifier<Object> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());
                final String json = Utilities.GSON_SERIALIZER.serialize(parsed);
                modifier.write(0, AdventureComponentConverter.fromJsonAsObject(json));
                break;
        }

        Utilities.debug(() -> DEBUG_HYPHEN);
    }

    private PacketResponse processPacket(final PacketContainer container) {
        if (internalAdventure) {
            final StructureModifier<Component> modifier = container.getModifier().withType(Component.class);
            if (modifier.size() == 1) {
                Utilities.debug(() -> "Trying SERVER_ADVENTRURE..");
                final Component component = modifier.readSafely(0);
                if (component != null) {
                    Utilities.debug(() -> "Found an adventure component!");
                    return new PacketResponse(ResponseType.SERVER_ADVENTURE, AbstractComponent.parse(component).toMiniMessage());
                }
            }
        }

        final WrappedChatComponent wrappedComponent = container.getChatComponents().readSafely(0);
        if (wrappedComponent != null) {
            Utilities.debug(() -> "Trying ProtocolLib's ChatComponent..");
            final String found = wrappedComponent.getJson();
            if (!found.isEmpty()) {
                Utilities.debug(() -> "Found String: " + found);
                try {
                    return new PacketResponse(ResponseType.WRAPPED_COMPONENT, AbstractComponent.parse(found).toMiniMessage());
                } catch (final Exception e) {
                    Utilities.debug(() -> "An error happened while de/serializing " + found + ": ", e);
                }
            }
        }

        final StructureModifier<TextComponent> textComponentModifier = container.getModifier().withType(TextComponent.class);
        if (textComponentModifier.size() == 1) {
            Utilities.debug(() -> "Trying Bungeecord TextComponent..");
            return new PacketResponse(ResponseType.BUNGEE_COMPONENT, processBaseComponents(textComponentModifier.readSafely(0)));
        }

        final String rawMessage = container.getStrings().readSafely(0);
        if (rawMessage != null) {
            Utilities.debug(() -> "Raw-Parsing message: " + rawMessage);
            return new PacketResponse(ResponseType.JSON, AbstractComponent.parse(rawMessage).toMiniMessage());
        }

        return null;
    }

    private String processBaseComponents(final BaseComponent... components) {
        return AbstractComponent.parse(bungee.deserialize(components)).toMiniMessage();
    }

    private record PacketResponse(ResponseType type, String message) {}

    private enum ResponseType {
        JSON, WRAPPED_COMPONENT, BUNGEE_COMPONENT, SERVER_ADVENTURE
    }

}
