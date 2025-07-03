package to.itsme.itsmyconfig.command.parameter;

import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.command.parameters.type.BaseParameterType;
import dev.velix.imperat.context.ExecutionContext;
import dev.velix.imperat.context.internal.CommandInputStream;
import dev.velix.imperat.exception.ImperatException;
import dev.velix.imperat.resolvers.SuggestionResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.command.handler.PlaceholderException;
import to.itsme.itsmyconfig.placeholder.Placeholder;

public class PlaceholderParameter extends BaseParameterType<BukkitSource, Placeholder> {

    private final ItsMyConfig plugin;

    public PlaceholderParameter(final ItsMyConfig plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable Placeholder resolve(
            @NotNull ExecutionContext<BukkitSource> context,
            @NotNull CommandInputStream<BukkitSource> stream,
            @NotNull String input
    ) throws ImperatException {
        final Placeholder placeholder = plugin.getPlaceholderManager().get(input);
        if (placeholder != null) {
            return placeholder;
        }
        throw new PlaceholderException(input);
    }

    @Override
    public SuggestionResolver<BukkitSource> getSuggestionResolver() {
        return SuggestionResolver.plain(plugin.getPlaceholderManager().getPlaceholdersMap().keySet().toArray(new String[0]));
    }

}
