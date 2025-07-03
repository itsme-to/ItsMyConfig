package to.itsme.itsmyconfig.command.parameter;

import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.command.parameters.type.BaseParameterType;
import dev.velix.imperat.context.ExecutionContext;
import dev.velix.imperat.context.internal.CommandInputStream;
import dev.velix.imperat.exception.ImperatException;
import dev.velix.imperat.resolvers.SuggestionResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.command.handler.SelectorException;
import to.itsme.itsmyconfig.command.util.PlayerSelector;

import java.util.ArrayList;
import java.util.List;

public class SelectorParameter extends BaseParameterType<BukkitSource, PlayerSelector> {

    @Override
    public @Nullable PlayerSelector resolve(
            @NotNull ExecutionContext<BukkitSource> context,
            @NotNull CommandInputStream<BukkitSource> stream,
            @NotNull String input
    ) throws ImperatException {
        if (input.equals("all")) {
            return PlayerSelector.all();
        }

        final Player player = Bukkit.getPlayer(input);
        if (player != null) {
            return PlayerSelector.of(player);
        }

        throw new SelectorException(input);
    }

    @Override
    public SuggestionResolver<BukkitSource> getSuggestionResolver() {
        final List<String> names = new ArrayList<>(Bukkit.getOnlinePlayers().size());
        names.add("all");
        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(names::add);
        return SuggestionResolver.plain(names);
    }

}
