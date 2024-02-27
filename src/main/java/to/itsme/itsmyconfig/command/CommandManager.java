package to.itsme.itsmyconfig.command;

import revxrsal.commands.bukkit.BukkitCommandHandler;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.command.impl.ItsMyConfigCommand;

public final class CommandManager {

    private final ItsMyConfig plugin;
    private final BukkitCommandHandler handler;

    public CommandManager(final ItsMyConfig plugin) {
        this.plugin = plugin;
        this.handler = BukkitCommandHandler.create(plugin);
        this.registerCommands();
        this.handler.registerBrigadier();
        this.handler.enableAdventure();
    }

    public void registerCommands() {
        this.handler.register(new ItsMyConfigCommand(this.plugin));
    }

}
