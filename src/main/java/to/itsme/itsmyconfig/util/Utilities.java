package to.itsme.itsmyconfig.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.ItsMyConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Pattern;

public final class Utilities {

    public static final MiniMessage MM = MiniMessage.miniMessage();

    private static final ItsMyConfig plugin = ItsMyConfig.getInstance();
    private static final Pattern COLOR_FILTER = Pattern.compile("[§&][a-zA-Z0-9]");

    private static final Field TEXT_COMPONENT_CONTENT;

    static {
        try {
            Class<?> textComponentImpClazz = Class.forName("net.kyori.adventure.text.TextComponentImpl");
            Field contentField = textComponentImpClazz.getDeclaredField("content");
            contentField.setAccessible(true);
            TEXT_COMPONENT_CONTENT = contentField;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes color codes from a string.
     *
     * @param text The text to remove color codes from.
     * @return The text without color codes.
     */
    public static String colorless(final String text) {
        return COLOR_FILTER.matcher(text).replaceAll("");
    }

    /**
     * Resolves all possible tags
     *
     * @param player The player for whom the resolver is being created.
     * @return All {@link TagResolver}s of th player combined.
     */
    public static TagResolver playerTag(final Player player) {
        return TagResolver.resolver(
                papiTag(player), titleTag(player),
                subtitleTag(player), actionbarTag(player)
        );
    }

    /**
     * Provides a PlaceholderAPI tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The PlaceholderAPI tag resolver.
     */
    public static TagResolver papiTag(final Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');
            final Component componentPlaceholder = MM.deserialize(parsedPlaceholder.replace("§", "&"));
            applyChatColors(componentPlaceholder);
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    /**
     * Provides a title tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The title tag resolver.
     */
    public static TagResolver titleTag(final Player player) {
        return TagResolver.resolver("title", (argumentQueue, context) -> {
            final List<Tag.Argument> args = new ArrayList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop());
            }

            if (args.size() == 1) {
                final Title title = Title.title(MM.deserialize(args.get(0).value(), playerTag(player)), Component.empty());
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 2) {
                final Title title = Title.title(MM.deserialize(args.get(0).value()), MM.deserialize(args.get(1).value(), playerTag(player)));
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 4) {
                final Title.Times times = createTimes(
                        args.get(0).asInt(),
                        args.get(1).asInt(),
                        args.get(2).asInt()
                );
                final Title title = Title.title(
                        MM.deserialize(args.get(3).value(), playerTag(player)),
                        Component.empty(),
                        times
                );
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 5) {
                final Title.Times times = createTimes(
                        args.get(0).asInt(),
                        args.get(1).asInt(),
                        args.get(2).asInt()
                );
                final Title title = Title.title(
                        MM.deserialize(args.get(3).value(), playerTag(player)),
                        MM.deserialize(args.get(4).value(), playerTag(player)),
                        times
                );
                plugin.adventure().player(player).showTitle(title);
            } else {
                return Tag.preProcessParsed("Invalid title tag arguments");
            }

            return Tag.selfClosingInserting(Component.empty());
        });
    }

    /**
     * Provides a subtitle tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The subtitle tag resolver.
     */
    public static TagResolver subtitleTag(final Player player) {
        return TagResolver.resolver("subtitle", (argumentQueue, context) -> {
            final List<Tag.Argument> args = new ArrayList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop());
            }

            if (args.size() == 1) {
                final Title title = Title.title(Component.empty(), MM.deserialize(args.get(0).value(), playerTag(player)));
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 4) {
                final Title.Times times = createTimes(
                        args.get(0).asInt(),
                        args.get(1).asInt(),
                        args.get(2).asInt()
                );
                final Title title = Title.title(
                        Component.empty(),
                        MM.deserialize(args.get(3).value(), playerTag(player)),
                        times
                );
                plugin.adventure().player(player).showTitle(title);
            } else {
                return Tag.preProcessParsed("Invalid subtitle tag arguments");
            }

            return Tag.selfClosingInserting(Component.empty());
        });
    }

    /**
     * Provides an action bar tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The action bar tag resolver.
     */
    public static TagResolver actionbarTag(final Player player) {
        return TagResolver.resolver("actionbar", (argumentQueue, context) -> {
            final String bar = argumentQueue.popOr("Invalid actionbar value").value();
            plugin.adventure().player(player).sendActionBar(MM.deserialize(bar, playerTag(player)));
            return Tag.selfClosingInserting(Component.empty());
        });
    }

    /**
     * Applies chat colors to a root component and its children recursively.
     *
     * @param rootComponent The root component to apply chat colors to.
     */
    public static void applyChatColors(final Component rootComponent) {
        if (rootComponent instanceof TextComponent) {
            final TextComponent textComponent = (TextComponent) rootComponent;
            final String translateAlternateColorCodes = ChatColor.translateAlternateColorCodes('&', textComponent.content());
            modifyContentOfTextComponent(textComponent, translateAlternateColorCodes);
            for (final Component component : rootComponent.children()) {
                applyChatColors(component);
            }
        }
    }

    /**
     * Converts a list of objects to a string, where each object is represented on a new line.
     *
     * @param list The list of objects to be converted to a string.
     * @return The string representation of the list.
     */
    public static String toString(@NotNull List<?> list) {
        return String.join(System.lineSeparator(), list.stream().map(Object::toString).toArray(String[]::new));
    }

    /**
     * Helper method to create Title times with optional values.
     *
     * @param fadeIn  The fade in time.
     * @param stay    The stay time.
     * @param fadeOut The fade out time.
     * @return Title.Times object with specified times.
     */
    @SuppressWarnings("all")
    private static Title.Times createTimes(
            @NotNull OptionalInt fadeIn,
            @NotNull OptionalInt stay,
            @NotNull OptionalInt fadeOut
    ) {
        return Title.Times.times(
                Ticks.duration(fadeIn.orElse(10)),
                Ticks.duration(stay.orElse(70)),
                Ticks.duration(fadeOut.orElse(20))
        );
    }

    /**
     * Modifies the content of a TextComponent instance.
     *
     * @param textComponent The TextComponent instance to modify.
     * @param content       The new content for the TextComponent.
     */
    private static void modifyContentOfTextComponent(TextComponent textComponent, String content) {
        try {
            TEXT_COMPONENT_CONTENT.set(textComponent, content);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
