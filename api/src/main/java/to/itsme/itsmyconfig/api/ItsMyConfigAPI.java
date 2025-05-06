package to.itsme.itsmyconfig.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.processor.PacketListener;

public interface ItsMyConfigAPI {

    @NotNull PacketListener getPacketListener();
    @NotNull Component translate(String text, TagResolver... args);
    @NotNull Component translate(String text, Player player, TagResolver... args);
    @NotNull Component translate(String text, OfflinePlayer player, TagResolver... args);

}
