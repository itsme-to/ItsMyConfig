package to.itsme.itsmyconfig.command.handler;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.node.ParameterNode;
import to.itsme.itsmyconfig.message.Message;

public final class ExceptionHandler extends BukkitExceptionHandler {

    @Override
    public void onNoPermission(@NotNull NoPermissionException e, @NotNull BukkitCommandActor actor) {
        Message.NO_PERMISSION.send(actor);
    }

    @Override
    public void onMissingArgument(
            @NotNull MissingArgumentException e,
            @NotNull BukkitCommandActor actor,
            @NotNull ParameterNode<BukkitCommandActor, ?> parameter
    ) {
        final ExecutableCommand<CommandActor> command = e.command();
        final String usage = (command.path() + " " + command.usage()).trim();
        Message.INVALID_USE.send(actor, Placeholder.parsed("usage", usage));
    }

}
