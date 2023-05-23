package ua.realalpha.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.model.ModelType;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PacketChatListener extends PacketAdapter {

    private final ModelRepository modelRepository;
    private ItsMyConfig itsMyConfig;
    private final Pattern filtreColorSection = Pattern.compile("[§&][a-zA-Z0-9]");

    public PacketChatListener(ItsMyConfig itsMyConfig, ModelRepository modelRepository) {
        super(itsMyConfig, ListenerPriority.NORMAL, (!MinecraftVersion.CAVES_CLIFFS_2.isAtLeast(MinecraftVersion.getCurrentVersion()) ? PacketType.Play.Server.SYSTEM_CHAT : PacketType.Play.Server.CHAT));
        this.itsMyConfig = itsMyConfig;
        this.modelRepository = modelRepository;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        Player player = event.getPlayer();
        BaseComponent[] baseComponents;
        if (!MinecraftVersion.CAVES_CLIFFS_2.isAtLeast(MinecraftVersion.getCurrentVersion())) {
            String s = packetContainer.getStrings().readSafely(0);
            if (s == null) return;
            baseComponents = ComponentSerializer.parse(s);
        }else {
            WrappedChatComponent wrappedChatComponent = packetContainer.getChatComponents().readSafely(0);
            if (wrappedChatComponent == null || (packetContainer.getChatComponentArrays().readSafely(0) != null && packetContainer.getChatComponentArrays().readSafely(0).length != 0)) return;

            baseComponents = ComponentSerializer.parse(wrappedChatComponent.getJson());
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (BaseComponent baseComponent : baseComponents) {
            stringBuilder.append(baseComponent.toLegacyText());
        }
        String message = stringBuilder.toString();
        message = PlaceholderAPI.setPlaceholders(player, message);
        message = PlaceholderAPI.setBracketPlaceholders(player, message);
        String plainTextWithOutColor = message.replaceAll(filtreColorSection.pattern(), "");

        if (plainTextWithOutColor.startsWith(itsMyConfig.getSymbolPrefix())) {
            String plainTextWithOutSymbolPrefix = message.substring(message.indexOf(itsMyConfig.getSymbolPrefix()) + 1).replaceAll("§", "&");

            if (!plainTextWithOutSymbolPrefix.isEmpty()) {
                List<String> tags = Tag.getTags(plainTextWithOutSymbolPrefix);
                if (!tags.isEmpty()) {
                    List<String> texts = Tag.textsWithoutTags(plainTextWithOutSymbolPrefix);
                    if (!texts.isEmpty()) {
                        Audience audience = itsMyConfig.adventure().player(player);
                        MiniMessage miniMessage = MiniMessage.miniMessage();
                        texts.forEach(text -> {
                            Component parsed = miniMessage.deserialize(text);
                            ItsMyConfig.applyingChatColor(parsed);
                            audience.sendMessage(parsed);
                        });
                    }

                }
                List<ModelType> modelTypes = tags.stream().map(ModelType::getModelType).filter(modelRepository::hasModel).collect(Collectors.toList());
                for (ModelType modelType : modelTypes) {
                    modelRepository.getModel(modelType).apply(player, Tag.getContent(modelType.getTagName(), plainTextWithOutSymbolPrefix), tags);
                }

                if (modelTypes.isEmpty()) {
                    Audience audience = itsMyConfig.adventure().player(player);
                    MiniMessage miniMessage = MiniMessage.miniMessage();
                    Component parsed = miniMessage.deserialize(plainTextWithOutSymbolPrefix);
                    ItsMyConfig.applyingChatColor(parsed);
                    audience.sendMessage(parsed);
                }


            }

            event.setCancelled(true);
        }
    }

}
