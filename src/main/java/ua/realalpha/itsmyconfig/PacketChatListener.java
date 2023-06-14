package ua.realalpha.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.AdventureComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
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

    private final ModelRepository modelRepository;
    private final ItsMyConfig itsMyConfig;
    private final Pattern colorFilter = Pattern.compile("[§&][a-zA-Z0-9]");

    public PacketChatListener(ItsMyConfig itsMyConfig, ModelRepository modelRepository) {
        super(itsMyConfig, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT, PacketType.Play.Server.DISGUISED_CHAT, PacketType.Play.Server.SYSTEM_CHAT);
        this.itsMyConfig = itsMyConfig;
        this.modelRepository = modelRepository;
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        Player player = event.getPlayer();
        String message = processMessage(packetContainer);

        if (message == null) return;

        // Parsing PlaceholderAPI placeholders
        message = PlaceholderAPI.setPlaceholders(player, message);
        message = PlaceholderAPI.setBracketPlaceholders(player, message);

        String withoutColors = message.replaceAll(colorFilter.pattern(), "");

        // If message doesn't start with "$" => do nothing
        if (!withoutColors.startsWith(itsMyConfig.getSymbolPrefix())) {
            return;
        }

        event.setCancelled(true);

        String withoutSymbol = message.substring(message.indexOf(itsMyConfig.getSymbolPrefix()) + 1)
                .replaceAll("§", "&");

        // If message's only "$" => cancel event and do nothing
        if (withoutSymbol.isEmpty()) {
            return;
        }

        List<String> tags = Tag.getTags(withoutSymbol);
        List<ModelType> modelTypes = tags.stream().map(ModelType::getModelType)
                .filter(modelRepository::hasModel)
                .collect(Collectors.toList());

        // If one or several models match, then apply them.
        if (!modelTypes.isEmpty()) {
            modelTypes.forEach(modelType -> modelRepository.getModel(modelType).apply(
                    player,
                    Tag.getContent(modelType.getTagName(), withoutSymbol),
                    tags
            ));
        } else {
            // Otherwise, just send the message.
            sendMessage(player, withoutSymbol);
        }

        String messageOutOfModels = modelTypes.stream()
                .map(ModelType::getTagName)
                .reduce(withoutSymbol, (s, s2) -> Tag.messageWithoutTagAndItsContent(s2, s));

        if (messageOutOfModels.isEmpty()) {
            return;
        }

        sendMessage(player, messageOutOfModels);
    }

    private void sendMessage(Player player, String message) {
        Audience audience = itsMyConfig.adventure().player(player);
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component parsed = miniMessage.deserialize(message);
        ItsMyConfig.applyingChatColor(parsed);
        audience.sendMessage(parsed);
    }

    private String processMessage(PacketContainer container) {
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.CAVES_CLIFFS_2)) { // At least 1.18
            try {
                StructureModifier modifier = container.getModifier().withType(AdventureComponentConverter.getComponentClass());

                if (modifier.size() == 1) {
                    WrappedChatComponent chatComponent = convert(modifier.readSafely(0));

                    return parseString(chatComponent.getJson());
                }
            } catch (Throwable ignored) { /* NO-OP: Not Paper */ }

            return parseString(container.getStrings().readSafely(0));
        } else { // Otherwise
            WrappedChatComponent chatComponent = container.getChatComponents().readSafely(0);

            if (chatComponent == null) {
                return null;
            }

            return parseString(chatComponent.getJson());
        }
    }

    private WrappedChatComponent convert(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

        return processBaseComponents(ComponentSerializer.parse(rawMessage));
    }

    private String processBaseComponents(BaseComponent[] components) {
        return Arrays.stream(components).map(component -> component.toLegacyText())
                .reduce("", (s, s2) -> s + s2);
    }
}
