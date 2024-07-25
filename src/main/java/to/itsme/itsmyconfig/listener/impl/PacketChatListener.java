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
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.Method;

public final class PacketChatListener extends PacketListener {

    private final boolean internalAdventure;
    private final Method fromComponent;
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
        final PacketContainer container = event.getPacket();
        Utilities.debug(() -> "################# CHAT PACKET #################\nProccessing packet " + container.getType().name());
        final PacketResponse response = this.processPacket(container);
        if (response == null || response.message.isEmpty()) {
            Utilities.debug(() -> "Packet is null or empty\n###############################################");
            return;
        }

        final String message = response.message;
        Utilities.debug(() -> "Checking: " + message);
        if (!this.startsWithSymbol(message)) {
            Utilities.debug(() -> "Message doesn't start w/ the symbol-prefix: " + message + "\n###############################################");
            return;
        }

        final Player player = event.getPlayer();
        final Component parsed = Utilities.translate(this.processMessage(message), player);
        if (parsed.equals(Component.empty())) {
            event.setCancelled(true);
            Utilities.debug(() -> "Component is empty, cancelling...\n###############################################");
            return;
        }

        Utilities.debug(() -> "Overriding Message as " + response.type.name());
        switch (response.type) {
            case JSON:
                container.getStrings().write(0, gsonComponentSerializer.serialize(parsed));
                break;
            case WRAPPED_COMPONENT:
                container.getChatComponents().write(0, WrappedChatComponent.fromJson(
                        gsonComponentSerializer.serialize(parsed)
                ));
                break;
            case BUNGEE_COMPONENT:
                container.getModifier().withType(TextComponent.class).write(0, new TextComponent(
                        bungee.serialize(parsed)
                ));
                break;
            case SERVER_ADVENTURE:
                final StructureModifier<Object> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());
                final String json = gsonComponentSerializer.serialize(parsed);
                modifier.write(0, AdventureComponentConverter.fromJsonAsObject(json));
                break;
        }

        Utilities.debug(() -> "###############################################");
    }

    private PacketResponse processPacket(final PacketContainer container) {
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

        if (internalAdventure) {
            try {
                final StructureModifier<?> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());
                if (modifier.size() == 1) {
                    Utilities.debug(() -> "Trying SERVER_ADVENTRURE..");
                    final WrappedChatComponent wrappedAComponent = (WrappedChatComponent) fromComponent.invoke(null, modifier.readSafely(0));
                    final String json = wrappedAComponent.getJson();
                    Utilities.debug(() -> "Found JSON: " + json);
                    return new PacketResponse(ResponseType.SERVER_ADVENTURE, AbstractComponent.parse(json).toMiniMessage());
                }
            } catch (Throwable ignored) {}
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

    private static final class PacketResponse {

        private final ResponseType type;
        private final String message;

        private PacketResponse(
                final ResponseType type,
                final String message
        ) {
            this.type = type;
            this.message = message;
        }

    }

    private enum ResponseType {
        JSON, WRAPPED_COMPONENT, BUNGEE_COMPONENT, SERVER_ADVENTURE
    }

}
