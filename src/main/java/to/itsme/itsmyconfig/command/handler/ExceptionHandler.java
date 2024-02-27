package to.itsme.itsmyconfig.command.handler;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.exception.BukkitExceptionAdapter;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.NoPermissionException;
import to.itsme.itsmyconfig.util.Message;

public class ExceptionHandler extends BukkitExceptionAdapter {

    @Override
    public void noPermission(@NotNull CommandActor actor, @NotNull NoPermissionException exception) {
        Message.NO_PERMISSION.send(actor);
    }

}
