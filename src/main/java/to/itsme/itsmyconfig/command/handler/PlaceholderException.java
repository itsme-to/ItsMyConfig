package to.itsme.itsmyconfig.command.handler;

import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SelfHandledException;
import to.itsme.itsmyconfig.util.Utilities;

public class PlaceholderException extends Exception implements SelfHandledException {

    private final String name;

    public PlaceholderException(final String name) {
        this.name = name;
    }

    @Override
    public void handle(final CommandActor actor) {
        ((BukkitCommandActor)actor).reply(Utilities.MM.deserialize("<red>Placeholder <yellow>" + name + "</yellow> was not found.</red>"));
    }

}
