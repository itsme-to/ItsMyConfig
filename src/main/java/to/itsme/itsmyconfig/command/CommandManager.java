package to.itsme.itsmyconfig.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.command.annotation.ModifiablePlaceholder;
import to.itsme.itsmyconfig.command.handler.ExceptionHandler;
import to.itsme.itsmyconfig.command.handler.PlaceholderException;
import to.itsme.itsmyconfig.command.handler.SelectorException;
import to.itsme.itsmyconfig.command.impl.ItsMyConfigCommand;
import to.itsme.itsmyconfig.command.util.PlayerSelector;
import to.itsme.itsmyconfig.placeholder.Placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandManager {

    private final ItsMyConfig plugin;
    private final Lamp<BukkitCommandActor> handler;

    public CommandManager(final ItsMyConfig plugin) {
        this.plugin = plugin;
        this.handler = BukkitLamp.builder(plugin)
                .parameterTypes(builder -> {
                    builder.addParameterType(Placeholder.class, (input, context) -> {
                        final String name = input.readString();
                        final Placeholder placeholder = plugin.getPlaceholderManager().get(name);
                        if (placeholder != null) {
                            return placeholder;
                        }

                        throw new PlaceholderException(name);
                    });
                    builder.addParameterType(PlayerSelector.class, (input, context) -> {
                        final String name = input.readString();
                        if (name.equals("*")) {
                            return PlayerSelector.all();
                        }

                        final Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            return PlayerSelector.of(player);
                        }

                        throw new SelectorException(name);
                    });
                })
                .suggestionProviders(builder -> {
                    builder.addProvider(Placeholder.class, (input, context) ->
                            plugin.getPlaceholderManager().getPlaceholdersMap().keySet());

                    builder.addProviderForAnnotation(
                            ModifiablePlaceholder.class,
                            annotation -> (input, context) ->
                                    plugin.getPlaceholderManager().getPlaceholdersMap().keySet().stream().filter(name -> {
                                final Placeholder data = plugin.getPlaceholderManager().get(name);
                                return data.getConfigurationSection().contains("value");
                            }).collect(Collectors.toSet())
                    );

                    builder.addProvider(PlayerSelector.class, (input, context) -> {
                        final List<String> names = new ArrayList<>();
                        names.add("*");
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(names::add);
                        return names;
                    });
                })
                .exceptionHandler(new ExceptionHandler())
                .build();
        this.registerCommands();
    }

    public void registerCommands() {
        this.handler.register(new ItsMyConfigCommand(this.plugin));
    }

}
