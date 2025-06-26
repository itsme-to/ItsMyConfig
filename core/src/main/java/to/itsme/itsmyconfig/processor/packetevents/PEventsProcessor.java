package to.itsme.itsmyconfig.processor.packetevents;

import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.processor.PacketContent;
import to.itsme.itsmyconfig.processor.PacketProcessor;
import to.itsme.itsmyconfig.util.AdventureUtil;
import to.itsme.itsmyconfig.util.IMCSerializer;

import java.lang.reflect.Method;

public class PEventsProcessor {

    public static final PacketProcessor<WrapperPlayServerChatMessage> CHAT_MESSAGE = new PacketProcessor<>() {
        private final Method setChatContent;

        {
            try {
                setChatContent = ChatMessage.class.getMethod("setChatContent", AdventureUtil.getComponentClass());
            } catch (Throwable t) {
                throw new RuntimeException("Failed to resolve setChatContent method", t);
            }
        }

        @Override
        public String name() {
            return "CHAT_MESSAGE";
        }

        @Override
        public void edit(WrapperPlayServerChatMessage wrappedPacket, Component component) {
            Object chatContent = AdventureUtil.fromComponent(component);
            Object chatMessage = wrappedPacket.getMessage();
            try {
                setChatContent.invoke(chatMessage, chatContent);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke setChatContent", t);
            }
        }

        @Override
        public @NotNull PacketContent<WrapperPlayServerChatMessage> unpack(WrapperPlayServerChatMessage wrappedPacket) {
            Object chatContent = wrappedPacket.getMessage().getChatContent();
            Component internal = AdventureUtil.toComponent(chatContent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };

    public static final PacketProcessor<WrapperPlayServerSystemChatMessage> SYSTEM_CHAT_MESSAGE = new PacketProcessor<>() {
        private final Method setMessage;

        {
            try {
                setMessage = WrapperPlayServerSystemChatMessage.class.getMethod("setMessage", AdventureUtil.getComponentClass());
            } catch (Throwable t) {
                throw new RuntimeException("Failed to resolve setMessage method", t);
            }
        }

        @Override
        public String name() {
            return "SYSTEM_CHAT_MESSAGE";
        }

        @Override
        public void edit(WrapperPlayServerSystemChatMessage wrappedPacket, Component component) {
            Object externalComponent = AdventureUtil.fromComponent(component);
            try {
                setMessage.invoke(wrappedPacket, externalComponent);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke setMessage", t);
            }
        }

        @Override
        public @NotNull PacketContent<WrapperPlayServerSystemChatMessage> unpack(WrapperPlayServerSystemChatMessage wrappedPacket) {
            Object externalComponent = wrappedPacket.getMessage();
            Component internal = AdventureUtil.toComponent(externalComponent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };

    public static final PacketProcessor<WrapperPlayServerDisconnect> DISCONNECT = new PacketProcessor<>() {
        private final Method setReason;

        {
            try {
                setReason = WrapperPlayServerDisconnect.class.getMethod("setReason", AdventureUtil.getComponentClass());
            } catch (Throwable t) {
                throw new RuntimeException("Failed to resolve setReason method", t);
            }
        }

        @Override
        public String name() {
            return "DISCONNECT";
        }

        @Override
        public void edit(WrapperPlayServerDisconnect wrappedPacket, Component component) {
            Object externalComponent = AdventureUtil.fromComponent(component);
            try {
                setReason.invoke(wrappedPacket, externalComponent);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke setReason", t);
            }
        }

        @Override
        public @NotNull PacketContent<WrapperPlayServerDisconnect> unpack(WrapperPlayServerDisconnect wrappedPacket) {
            Object externalComponent = wrappedPacket.getReason();
            Component internal = AdventureUtil.toComponent(externalComponent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };
}
