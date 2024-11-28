package to.itsme.itsmyconfig.listener.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.listener.PacketListener;
import to.itsme.itsmyconfig.processor.PacketForm;
import to.itsme.itsmyconfig.processor.UnpackedPacket;
import to.itsme.itsmyconfig.util.Strings;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.HashMap;
import java.util.Map;

public final class PacketChatListener extends PacketListener {

    private static final String DEBUG_HYPHEN = "###############################################";

    /* Here we cache the packet check types for faster handling */
    private final Map<PacketType, PacketForm> packetTypeMap = new HashMap<>(4);

    public PacketChatListener(
            final ItsMyConfig plugin
    ) {
        super(
                plugin,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.KICK_DISCONNECT
        );
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer container = event.getPacket();
        final PacketType type = container.getType();
        Utilities.debug(() -> "################# CHAT PACKET #################\nProccessing packet " + type.name());
        final UnpackedPacket packet = this.processPacket(container);
        if (packet == null || packet.isEmpty()) {
            Utilities.debug(() -> "Packet is null or empty\n" + DEBUG_HYPHEN);
            return;
        }

        final String message = packet.message();
        Utilities.debug(() -> "Found message: " + message);
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

        Utilities.debug(() -> "Final Product: " + AbstractComponent.parse(parsed).toMiniMessage() + "\n" + "Overriding...");
        packet.save(container, parsed);
        Utilities.debug(() -> DEBUG_HYPHEN);
    }

    private UnpackedPacket processPacket(final PacketContainer container) {
        final PacketType type = container.getType();
        final PacketForm foundForm = packetTypeMap.get(type);
        if (foundForm != null) {
            Utilities.debug(() -> "Using " + foundForm.name() + " to unpack the packet (cached)");
            return foundForm.unpack(container);
        }

        Utilities.debug(() -> "Figuring " + type.name() + "'s packet form..");
        for (final PacketForm form : PacketForm.values()) {
            Utilities.debug(() -> "Trying " + form.name() + "..");
            final UnpackedPacket unpacked = form.unpack(container);
            if (unpacked != null) {
                packetTypeMap.put(type, form);
                Utilities.debug(() -> "Matched form " + form.name() + " for packet " + type.name());
                return unpacked;
            }
            Utilities.debug(() -> "Didn't work, trying next (if there is) ..");
        }

        return null;
    }

}
