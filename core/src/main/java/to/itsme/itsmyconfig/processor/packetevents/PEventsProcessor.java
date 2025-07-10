package to.itsme.itsmyconfig.processor.packetevents;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.processor.PacketContent;
import to.itsme.itsmyconfig.processor.PacketProcessor;
import to.itsme.itsmyconfig.util.AdventureUtil;
import to.itsme.itsmyconfig.util.IMCSerializer;
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.Method;

public class PEventsProcessor {

    public static final PacketProcessor<WrapperPlayServerChatMessage> CHAT_MESSAGE = new PacketProcessor<>() {

        @Override
        public String name() {
            return "CHAT_MESSAGE";
        }

        @Override
        public void edit(WrapperPlayServerChatMessage wrappedPacket, Component component) {
            wrappedPacket.getMessage().setChatContentJson(
                    wrappedPacket.getClientVersion(), Utilities.GSON_SERIALIZER.serialize(component)
            );
        }

        @Override
        public @NotNull PacketContent<WrapperPlayServerChatMessage> unpack(WrapperPlayServerChatMessage wrappedPacket) {
            return new PacketContent<>(
                    wrappedPacket, this, IMCSerializer.toMiniMessage(
                    wrappedPacket.getMessage().getChatContentJson(wrappedPacket.getClientVersion()))
            );
        }
    };

    public static final PacketProcessor<WrapperPlayServerSystemChatMessage> SYSTEM_CHAT_MESSAGE = new PacketProcessor<>() {

        @Override
        public String name() {
            return "SYSTEM_CHAT_MESSAGE";
        }

        @Override
        @SuppressWarnings("deprecation")
        public void edit(WrapperPlayServerSystemChatMessage wrappedPacket, Component component) {
            wrappedPacket.setMessageJson(
                IMCSerializer.toMiniMessage(component)
            );
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull PacketContent<WrapperPlayServerSystemChatMessage> unpack(WrapperPlayServerSystemChatMessage wrappedPacket) {
            final String json = wrappedPacket.getMessageJson();
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(json));
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
                externalComponent = getReason.invoke(wrappedPacket);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke WrapperPlayServerDisconnect#getReason", t);
            }
            Component internal = AdventureUtil.toComponent(externalComponent);
            return new PacketContent<>(wrappedPacket, this, IMCSerializer.toMiniMessage(internal));
        }
    };

}
