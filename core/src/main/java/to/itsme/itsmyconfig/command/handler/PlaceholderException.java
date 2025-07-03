package to.itsme.itsmyconfig.command.handler;

import dev.velix.imperat.ImperatConfig;
import dev.velix.imperat.context.Context;
import dev.velix.imperat.context.Source;
import dev.velix.imperat.exception.SelfHandledException;
import to.itsme.itsmyconfig.message.AudienceResolver;
import to.itsme.itsmyconfig.util.Utilities;

public class PlaceholderException extends SelfHandledException {

    private final String name;

    public PlaceholderException(final String name) {
        this.name = name;
    }

    @Override
    public <S extends Source> void handle(
            ImperatConfig<S> imperat,
            Context<S> context
    ) {
        AudienceResolver.resolve(context.source()).sendMessage(
                Utilities.MM.deserialize(
                        "<red>Placeholder <yellow>" + name + "</yellow> was not found.</red>"
                )
        );
    }

}
