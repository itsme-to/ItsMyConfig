package ua.realalpha.itsmyconfig.model;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.util.Collection;

public final class ActionBarModel extends Model {

    private final ItsMyConfig itsMyConfig;
    public ActionBarModel(ItsMyConfig itsMyConfig) {
        super(ModelType.ACTIONBAR);
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public void apply(Player player, String message, Collection<String> tokenKeys) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component parsed = miniMessage.deserialize(Tag.messageWithoutTag(ModelType.ACTIONBAR.getTagName(), message));
        Audience audience = itsMyConfig.adventure().player(player);
        ItsMyConfig.applyingChatColor(parsed);
        audience.sendActionBar(parsed);
    }
}
