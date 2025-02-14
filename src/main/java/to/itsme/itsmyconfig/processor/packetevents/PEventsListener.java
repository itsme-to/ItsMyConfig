package to.itsme.itsmyconfig.processor.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.processor.PacketContent;
import to.itsme.itsmyconfig.processor.PacketListener;
import to.itsme.itsmyconfig.processor.PacketProcessor;
import to.itsme.itsmyconfig.util.Strings;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Map;

public class PEventsListener implements PacketListener, com.github.retrooper.packetevents.event.PacketListener {

    private static final String FAIL_MESSAGE_PREFIX = "<color:red><lang:multiplayer.message_not_delivered:";

    /* Here we cache the packet check types for faster handling */
    private final Map<PacketTypeCommon, PacketProcessor<PacketSendEvent>> packetTypeMap = Map.of(
            PacketType.Play.Server.CHAT_MESSAGE, PEventsProcessor.CHAT_MESSAGE,
            PacketType.Play.Server.SYSTEM_CHAT_MESSAGE, PEventsProcessor.SYSTEM_CHAT_MESSAGE,
            PacketType.Play.Server.DISCONNECT, PEventsProcessor.DISCONNECT
    );

    @Override
    public String name() {
        return "PacketEvents";
    }

    @Override
    public void load() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        final PacketTypeCommon type = event.getPacketType();

        if (!(type instanceof PacketType.Play.Server server)) {
            return;
        }

        final PacketProcessor<PacketSendEvent> processor = packetTypeMap.get(server);
        if (processor == null) {
            return;
        }

        Utilities.debug(() -> "################# CHAT PACKET #################\nProccessing packet " + server.name());
        final PacketContent<PacketSendEvent> packet = processor.unpack(event);
        if (packet == null || packet.isEmpty()) {
            Utilities.debug(() -> "Packet is null or empty\n" + Strings.DEBUG_HYPHEN);
            return;
        }

        final String message = packet.message();
        Utilities.debug(() -> "Found message: " + message);

        if (message.startsWith(FAIL_MESSAGE_PREFIX)) {
            Utilities.debug(()-> "Message send failure message, cancelling...");
            event.setCancelled(true);
            return;
        }

        if (!Strings.startsWithSymbol(message)) {
            Utilities.debug(() -> "Message doesn't start w/ the symbol-prefix: " + message + "\n" + Strings.DEBUG_HYPHEN);
            return;
        }

        final Player player = event.getPlayer();
        final Component parsed = Utilities.translate(Strings.processMessage(message), player);
        if (parsed.equals(Component.empty())) {
            event.setCancelled(true);
            Utilities.debug(() -> "Component is empty, cancelling...\n" + Strings.DEBUG_HYPHEN);
            return;
        }

        Utilities.debug(() -> "Final Product: " + AbstractComponent.parse(parsed).toMiniMessage() + "\n" + "Overriding...");
        event.markForReEncode(true);
        packet.save(parsed);
        Utilities.debug(() -> Strings.DEBUG_HYPHEN);
    }

}
