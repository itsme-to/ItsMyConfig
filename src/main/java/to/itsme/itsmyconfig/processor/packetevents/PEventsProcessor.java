package to.itsme.itsmyconfig.processor.packetevents;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.processor.PacketContent;
import to.itsme.itsmyconfig.processor.PacketProcessor;

public class PEventsProcessor {

    public static final PacketProcessor<PacketSendEvent> CHAT_MESSAGE = new PacketProcessor<>() {

        @Override
        public String name() {
            return "CHAT_MESSAGE";
        }

        @Override
        public void edit(PacketSendEvent container, Component component) {
            new WrapperPlayServerChatMessage(container).getMessage().setChatContent(component);
        }

        @Override
        public @NotNull PacketContent<PacketSendEvent> unpack(PacketSendEvent container) {
            return new PacketContent<>(container,this, AbstractComponent.parse(new WrapperPlayServerChatMessage(container).getMessage().getChatContent()).toMiniMessage());
        }

    };

    public static final PacketProcessor<PacketSendEvent> SYSTEM_CHAT_MESSAGE = new PacketProcessor<>() {

        @Override
        public String name() {
            return "SYSTEM_CHAT_MESSAGE";
        }

        @Override
        public void edit(PacketSendEvent container, Component component) {
            new WrapperPlayServerSystemChatMessage(container).setMessage(component);
        }

        @Override
        public @NotNull PacketContent<PacketSendEvent> unpack(PacketSendEvent container) {
            return new PacketContent<>(container, this, AbstractComponent.parse(new WrapperPlayServerSystemChatMessage(container).getMessage()).toMiniMessage());
        }

    };

    public static final PacketProcessor<PacketSendEvent> DISCONNECT = new PacketProcessor<>() {

        @Override
        public String name() {
            return "DISCONNECT";
        }

        @Override
        public void edit(PacketSendEvent container, Component component) {
            new WrapperPlayServerDisconnect(container).setReason(component);
        }

        @Override
        public @NotNull PacketContent<PacketSendEvent> unpack(PacketSendEvent container) {
            return new PacketContent<>(container, this, AbstractComponent.parse(new WrapperPlayServerDisconnect(container).getReason()).toMiniMessage());
        }

    };

}
