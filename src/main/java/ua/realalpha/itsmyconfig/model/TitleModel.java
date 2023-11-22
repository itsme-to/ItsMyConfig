package ua.realalpha.itsmyconfig.model;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.time.Duration;
import java.util.Collection;

public class TitleModel extends Model {

    private final ItsMyConfig itsMyConfig;

    public TitleModel(ItsMyConfig itsMyConfig) {
        super(ModelType.TITLE);
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public void apply(Player player, String message, Collection<String> tokenKeys) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component parsed = miniMessage.deserialize(Tag.messageWithoutTag(ModelType.TITLE.getTagName(), message));
        Audience audience = itsMyConfig.adventure().player(player);
        ItsMyConfig.applyingChatColor(parsed);
        audience.showTitle(Title.title(parsed, Component.empty(), createTimes(Tag.getParameters(message))));
    }

    public static Title.Times createTimes(String[] parameters){
        Duration fadeIn = (parameters.length >= 1) ? Ticks.duration(Integer.parseInt(parameters[0])) : Ticks.duration(10);
        Duration stay = (parameters.length >= 2) ? Ticks.duration(Integer.parseInt(parameters[1])) : Ticks.duration(70);
        Duration fadeOut = (parameters.length >= 3) ? Ticks.duration(Integer.parseInt(parameters[2])) : Ticks.duration(20);
        return Title.Times.times(fadeIn, stay, fadeOut);
    }

}