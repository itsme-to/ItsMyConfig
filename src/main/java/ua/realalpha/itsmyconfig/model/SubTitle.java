package ua.realalpha.itsmyconfig.model;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.util.Collection;

public class SubTitle extends Model {

    private final ItsMyConfig itsMyConfig;

    public SubTitle(ItsMyConfig itsMyConfig) {
        super(ModelType.SUBTITLE);
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public void apply(Player player, String message, Collection<String> tags) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component parsed = miniMessage.deserialize(Tag.messageWithoutTag(ModelType.SUBTITLE.getTagName(), message));
        Audience audience = itsMyConfig.adventure().player(player);
        ItsMyConfig.applyingChatColor(parsed);

        if (tags.stream().noneMatch(tokenKey -> tokenKey.equalsIgnoreCase("title"))) {
            String[] parameters = Tag.getParameters(message);
            Title title = Title.title(Component.empty(), parsed, TitleModel.createTimes(parameters));
            audience.showTitle(title);
        } else {
            audience.sendTitlePart(TitlePart.SUBTITLE, parsed);
        }

    }

}