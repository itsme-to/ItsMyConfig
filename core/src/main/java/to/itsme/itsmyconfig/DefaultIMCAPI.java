package to.itsme.itsmyconfig;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.api.ItsMyConfigAPI;
import to.itsme.itsmyconfig.processor.PacketListener;
import to.itsme.itsmyconfig.util.Utilities;

public class DefaultIMCAPI implements ItsMyConfigAPI {

    private final ItsMyConfig plugin;

    public DefaultIMCAPI(final ItsMyConfig plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull PacketListener getPacketListener() {
        return this.plugin.processorManager.getListener();
    }

    /* Adventure has been relocated *again* inside ItsMyConfig
    @Override
    public @NotNull Component translate(String text, TagResolver... args) {
        return Utilities.translate(text, args);
    }

    @Override
    public @NotNull Component translate(String text, OfflinePlayer player, TagResolver... args) {
        return Utilities.translate(text, player, args);
    }

    @Override
    public @NotNull Component translate(String text, Player player, TagResolver... args) {
        return Utilities.translate(text, player, args);
    }*/

}
