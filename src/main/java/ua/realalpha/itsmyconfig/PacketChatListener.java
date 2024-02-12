package ua.realalpha.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.AdventureComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.model.ModelType;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PacketChatListener extends PacketAdapter {

    private final ItsMyConfig plugin;
    private final ModelRepository modelRepository;
    private final Pattern colorFilter = Pattern.compile("[§&][a-zA-Z0-9]");

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final GsonComponentSerializer gson = GsonComponentSerializer.gson();
    private final ComponentSerializer<Component, net.kyori.adventure.text.TextComponent, String> serializer;

    public PacketChatListener(
            ItsMyConfig plugin,
            ModelRepository modelRepository,
            PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
        this.modelRepository = modelRepository;

        boolean legacy = plugin.getConfig().getBoolean("use-legacy-serializer");
        this.serializer = legacy ? LegacyComponentSerializer.legacySection() : PlainTextComponentSerializer.plainText();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        final Player player = event.getPlayer();
        String message = this.processMessage(packetContainer);

        if (message == null) {
            return;
        }

        // If message doesn't start with "$" => do nothing
        if (!colorFilter.matcher(message).replaceAll("").startsWith(plugin.getSymbolPrefix())) {
            return;
        }

        event.setCancelled(true);

        // Parsing PlaceholderAPI placeholders
        message = PlaceholderAPI.setPlaceholders(player, message);

        // Remove legacy color symbol
        final String withoutSymbol = message.substring(message.indexOf(plugin.getSymbolPrefix()) + 1).replaceAll("§", "&");

        // If message's only "$" => cancel event and do nothing
        if (withoutSymbol.isEmpty()) {
            return;
        }

        final List<String> tags = Tag.getTags(withoutSymbol);
        final List<ModelType> modelTypes = tags.stream().map(ModelType::getModelType)
                .filter(modelRepository::hasModel)
                .collect(Collectors.toList());

        // If one or several models match, then apply them.
        if (!modelTypes.isEmpty()) {
            modelTypes.forEach(modelType -> modelRepository.getModel(modelType).apply(
                    player,
                    Tag.getContent(modelType.getTagName(), withoutSymbol),
                    tags
            ));
        }

        final String messageOutOfModels = modelTypes.stream()
                .map(ModelType::getTagName)
                .reduce(withoutSymbol, (s, s2) -> Tag.messageWithoutTagAndItsContent(s2, s));

        if (messageOutOfModels.isEmpty()) {
            return;
        }

        sendMessage(player, messageOutOfModels);
    }

    private void sendMessage(Player player, String message) {
        Component parsed = replaceClickEvent(miniMessage.deserialize(message));
        ItsMyConfig.applyingChatColor(parsed);
        plugin.adventure().player(player).sendMessage(parsed);
    }

    private Component replaceClickEvent(final Component component) {
        Component copied = component;
        ClickEvent event = component.clickEvent();

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
                return serializer.serialize(gson.deserialize(chatComponent.getJson()));
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
                return serializer.serialize(gson.deserialize(jsonString));
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
        return Arrays.stream(components).map(component -> component.toLegacyText())
                .reduce("", (s, s2) -> s + s2);
    }

}
