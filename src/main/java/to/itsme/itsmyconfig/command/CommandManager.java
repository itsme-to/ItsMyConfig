package to.itsme.itsmyconfig.command;

import revxrsal.commands.bukkit.BukkitCommandHandler;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.command.handler.ExceptionHandler;
import to.itsme.itsmyconfig.command.impl.ItsMyConfigCommand;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

import java.util.stream.Collectors;

public final class CommandManager {

    private final ItsMyConfig plugin;
    private final BukkitCommandHandler handler;

    public CommandManager(final ItsMyConfig plugin) {
        this.plugin = plugin;
        this.handler = BukkitCommandHandler.create(plugin);

        // set the help-writer format
        this.handler.setHelpWriter((cmd, actor) ->
                String.format(
                        "  <gray>• <white>/%s <gold>%s",
                        cmd.getPath().toRealString(),
                        cmd.getUsage().isEmpty() ? "" : cmd.getUsage() + " "
                )
        );

        this.handler.getAutoCompleter().registerSuggestion("placeholders", (args, sender, command) ->
                plugin.getPlaceholderManager().getPlaceholdersMap().keySet());

        this.handler.getAutoCompleter().registerSuggestion("singleValuePlaceholder", (args, sender, command) ->
                plugin.getPlaceholderManager().getPlaceholdersMap().keySet().stream().filter(name -> {
                    final PlaceholderData data = plugin.getPlaceholderManager().get(name);
                    return PlaceholderType.STRING.equals(data.getType()) || PlaceholderType.COLOR.equals(data.getType());
                }).collect(Collectors.toList()));

        this.handler.setExceptionHandler(new ExceptionHandler());
        this.handler.getAutoCompleter();
        this.registerCommands();
        this.handler.registerBrigadier();
        this.handler.enableAdventure();
    }

    public void registerCommands() {
        this.handler.register(new ItsMyConfigCommand(this.plugin));
    }

}
