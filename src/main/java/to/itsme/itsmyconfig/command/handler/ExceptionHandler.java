package to.itsme.itsmyconfig.command.handler;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.exception.BukkitExceptionAdapter;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.TooManyArgumentsException;
import to.itsme.itsmyconfig.util.Message;

public class ExceptionHandler extends BukkitExceptionAdapter {

    @Override
    public void noPermission(@NotNull CommandActor actor, @NotNull NoPermissionException exception) {
        Message.NO_PERMISSION.send(actor);
    }

    @Override
    public void tooManyArguments(@NotNull CommandActor actor, @NotNull TooManyArgumentsException exception) {
        final ExecutableCommand command = exception.getCommand();
        final String usage = (command.getPath().toRealString() + " " + command.getUsage()).trim();
        Message.INVALID_USE.send(actor, Placeholder.parsed("usage", usage));
    }

}
