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

    private final Method fromComponent;
    private final boolean internalAdventure;
    private final BungeeComponentSerializer bungee = BungeeComponentSerializer.get();

    public PacketChatListener(
            final ItsMyConfig plugin
    ) {
        super(plugin, PacketType.Play.Server.CHAT, PacketType.Play.Server.DISGUISED_CHAT, PacketType.Play.Server.SYSTEM_CHAT);
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
        Utilities.debug("################# CHAT PACKET #################");
        final PacketContainer packetContainer = event.getPacket();
        final String message = this.processPacket(packetContainer);
        if (message == null || message.isEmpty()) {
            Utilities.debug("###############################################");
            return;
        }

        Utilities.debug("Checking: " + message);
        if (!this.startsWithSymbol(message)) {
            Utilities.debug("Message doesn't start w/ the symbol-prefix: " + message);
            Utilities.debug("###############################################");
            return;
        }

        event.setCancelled(true);
        final Player player = event.getPlayer();
        final Component parsed = Utilities.translate(this.processMessage(message), player);
        Utilities.debug("###############################################");
        if (!parsed.equals(Component.empty())) {
            plugin.adventure().player(player).sendMessage(parsed);
        }
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
