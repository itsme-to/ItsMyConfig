package to.itsme.itsmyconfig.command.handler;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.SelfHandledException;
import revxrsal.commands.exception.context.ErrorContext;
import to.itsme.itsmyconfig.message.AudienceResolver;
import to.itsme.itsmyconfig.util.Utilities;

public class SelectorException extends RuntimeException implements SelfHandledException<BukkitCommandActor> {

    private final String name;

    public SelectorException(final String name) {
        this.name = name;
    }

    @Override
    public void handle(@NotNull ErrorContext<BukkitCommandActor> context) {
        AudienceResolver.resolve(context.actor()).sendMessage(Utilities.MM.deserialize("<red>Player(s) <yellow>(" + name + ")</yellow> was/were not found.</red>"));
    }

}