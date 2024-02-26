package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.AdventureComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PacketChatListener extends PacketAdapter {

    private final ItsMyConfig plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final GsonComponentSerializer gson = GsonComponentSerializer.gson();
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public PacketChatListener(
            final ItsMyConfig plugin,
            final PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        final String message = this.processMessage(packetContainer);
        if (message == null) {
            return;
        }

        // If colorless message doesn't start with "$" => do nothing
        if (!Utilities.colorless(message).startsWith(plugin.getSymbolPrefix())) {
            return;
        }

        event.setCancelled(true);
        sendMessage(event.getPlayer(), message.substring(message.indexOf(plugin.getSymbolPrefix()) + 1).replaceAll("§", "&"));
    }

    private void sendMessage(
            final Player player,
            final String message
    ) {
        final Component parsed = replaceClickEvent(miniMessage.deserialize(
                message,
                Utilities.papiTag(player),
                Utilities.titleTag(player),
                Utilities.subtitleTag(player),
                Utilities.actionbarTag(player)
        ));
        Utilities.applyingChatColor(parsed);
        if (!parsed.equals(Component.empty())) {
            plugin.adventure().player(player).sendMessage(parsed);
        }
    }

    private Component replaceClickEvent(final Component component) {
        final ClickEvent event = component.clickEvent();
        Component copied = component;

        // Serialized then deserialized components with a click event have their value starting with "&f".
        if (event != null && event.value().startsWith("&f")) {
            copied = component.clickEvent(ClickEvent.clickEvent(event.action(), event.value().substring(2)));
        }

        copied = copied.children(copied.children().stream().map(this::replaceClickEvent).collect(Collectors.toList()));
        return copied;
    }

    private String processMessage(final PacketContainer container) {
        try {
            StructureModifier<?> modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());

            if (modifier.size() == 1) {
                WrappedChatComponent chatComponent = convertFromComponent(modifier.readSafely(0));
                return legacy.serialize(gson.deserialize(chatComponent.getJson()));
            }
        } catch (Throwable ignored) {}

        StructureModifier<TextComponent> textComponentModifier = container.getModifier().withType(TextComponent.class);

        if (textComponentModifier.size() == 1) {
            return textComponentModifier.readSafely(0).toLegacyText();
        }

        WrappedChatComponent chatComponent = container.getChatComponents().readSafely(0);
        if (chatComponent != null) {
            String jsonString = chatComponent.getJson();
            try {
                return legacy.serialize(gson.deserialize(jsonString));
            } catch (final Exception e) {
                throw new RuntimeException("An error happened while de/serializing " + jsonString, e);
            }
        }

        return parseString(container.getStrings().readSafely(0));
    }

    private WrappedChatComponent convertFromComponent(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AdventureComponentConverter.class.getDeclaredMethod(
                "fromComponent",
                AdventureComponentConverter.getComponentClass()
        );

        return (WrappedChatComponent) method.invoke(null, o);
    }

    private String parseString(String rawMessage) {
        if (rawMessage == null) {
            return null;
        }

        return processBaseComponents(net.md_5.bungee.chat.ComponentSerializer.parse(rawMessage));
    }

    private String processBaseComponents(BaseComponent[] components) {
        return Arrays.stream(components)
                .map(component -> component.toLegacyText())
                .reduce("", (s, s2) -> s + s2);
    }

}
