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
        private final Method getChatContent, setChatContent;

        {
            try {
                getChatContent = ChatMessage.class.getMethod("getChatContent");
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
            Object chatContent;
            try {
                chatContent = getChatContent.invoke(wrappedPacket.getMessage());
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke getChatContent", t);
            }
            Component internal = AdventureUtil.toComponent(chatContent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };

    public static final PacketProcessor<WrapperPlayServerSystemChatMessage> SYSTEM_CHAT_MESSAGE = new PacketProcessor<>() {
        private final Method getMessage, setMessage;

        {
            try {
                getMessage = WrapperPlayServerSystemChatMessage.class.getMethod("getMessage");
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
            Object externalComponent;
            try {
                externalComponent = getMessage.invoke(wrappedPacket)
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke WrapperPlayServerSystemChatMessage#getMessage", t);
            }
            Component internal = AdventureUtil.toComponent(externalComponent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };

    public static final PacketProcessor<WrapperPlayServerDisconnect> DISCONNECT = new PacketProcessor<>() {
        private final Method getReason, setReason;

        {
            try {
                getReason = WrapperPlayServerDisconnect.class.getMethod("getReason");
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
            Object externalComponent;
            try {
                externalComponent = getReason.invoke(wrappedPacket)
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke WrapperPlayServerDisconnect#getReason", t);
            }
            Component internal = AdventureUtil.toComponent(externalComponent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };
}
