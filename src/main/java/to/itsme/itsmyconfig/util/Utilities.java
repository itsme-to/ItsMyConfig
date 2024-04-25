package to.itsme.itsmyconfig.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.font.Font;
import to.itsme.itsmyconfig.font.FontTag;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.type.ColorPlaceholderData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utilities {

    public static final MiniMessage MM, EMPTY_MM;
    public static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    private static final ItsMyConfig plugin = ItsMyConfig.getInstance();
    private static final Pattern COLOR_FILTER = Pattern.compile("[§&][a-zA-Z0-9]");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");

    private static final Field TEXT_COMPONENT_CONTENT;

    static {
        MM = MiniMessage.builder()
                .tags(
                        TagResolver.builder()
                                .resolvers(
                                        StandardTags.defaults(),
                                        TagResolver.resolver("smallcaps", new FontTag(Font.SMALL_CAPS))
                                ).build()
                ).build();
        EMPTY_MM = MiniMessage.builder().tags(TagResolver.empty()).build();
        try {
            final Class<?> textComponentImpClazz = Class.forName("net.kyori.adventure.text.TextComponentImpl");
            final Field contentField = textComponentImpClazz.getDeclaredField("content");
            contentField.setAccessible(true);
            TEXT_COMPONENT_CONTENT = contentField;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs debug information to the console if the debug mode is enabled.
     *
     * @param text The debug information to log.
     */
    public static void debug(final String text) {
        if (plugin.getConfig().getBoolean("debug")) {
            plugin.getLogger().info(text);
        }
    }

    /**
     * Logs debug information along with an exception stack trace to the console if the debug mode is enabled.
     *
     * @param text      The debug information to log.
     * @param exception The Exception object representing the exception to log.
     */
    public static void debug(final String text, final Exception exception) {
        if (plugin.getConfig().getBoolean("debug")) {
            plugin.getLogger().log(Level.SEVERE, text, exception);
        }
    }

    /**
     * Extracts integer arguments enclosed within curly braces from the given string.
     *
     * @param string The string from which to extract integer arguments.
     * @return A List of Integer containing the extracted integer arguments.
     */
    public static List<Integer> getArguments(final String string) {
        final List<Integer> args = new ArrayList<>();
        final Matcher matcher = ARGUMENT_PATTERN.matcher(string);
        while (matcher.find()) {
            args.add(Integer.parseInt(matcher.group(1)));
        }
        return args;
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
                itsMyConfigTag(player), papiTag(player), playerSubtags(player)
        );
    }

    /**
     * Resolves all possible tags except main ones
     *
     * @param player The player for whom the resolver is being created.
     * @return All {@link TagResolver}s of th player combined.
     */
    public static TagResolver playerSubtags(final Player player) {
        return TagResolver.resolver(
                titleTag(player), subtitleTag(player),
                actionbarTag(player), soundTag(player)
        );
    }

    /**
     * Provides a ItsMyConfig placeholders tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The ItsMyConfig placeholder tag resolver.
     */
    public static TagResolver itsMyConfigTag(final Player player) {
        return TagResolver.resolver("p", (argumentQueue, context) -> {
            if (!argumentQueue.hasNext()) {
                return Tag.preProcessParsed("Unknown Placeholder");
            }

            final String name = argumentQueue.popOr("").value();
            final PlaceholderData data = plugin.getPlaceholderManager().get(name);
            if (data == null) {
                return Tag.preProcessParsed("Unknown Placeholder");
            }

            if (data instanceof ColorPlaceholderData) {
                return Tag.styling(builder -> builder.merge(((ColorPlaceholderData) data).getStyle()));
            }

            final List<String> args = new LinkedList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop().value());
            }

            final String parsed = data.asString(player, args.toArray(new String[0]));
            return Tag.preProcessParsed(parsed == null ? "" : parsed);
        });
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
            return Tag.preProcessParsed(parsedPlaceholder.replace("§", "&"));
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
     * Provides a sound player tag resolver.
     *
     * @param player The player for whom the resolver is being created.
     * @return The sound tag resolver.
     */
    public static TagResolver soundTag(final Player player) {
        return TagResolver.resolver("sound", (argumentQueue, context) -> {
            final List<Tag.Argument> args = new ArrayList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop());
            }

            if (args.size() == 1) {
                final Sound sound = Sound.valueOf(args.get(0).value());
                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
            } else if (args.size() == 3) {
                final Sound sound = Sound.valueOf(args.get(0).value());
                final float volume = (float) args.get(1).asDouble().orElse(1.0D);
                final float pitch = (float) args.get(2).asDouble().orElse(1.0D);
                player.playSound(player.getLocation(), sound, volume, pitch);
            } else {
                return Tag.preProcessParsed("Invalid sound tag arguments");
            }

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
            modifyTextComponent(textComponent, translateAlternateColorCodes);
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
    public static String toString(final @NotNull List<?> list) {
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
            final @NotNull OptionalInt fadeIn,
            final @NotNull OptionalInt stay,
            final @NotNull OptionalInt fadeOut
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
     * @param component The TextComponent instance to modify.
     * @param content       The new content for the TextComponent.
     */
    private static void modifyTextComponent(
            final @NotNull TextComponent component,
            final @Nullable String content
    ) {
        try {
            TEXT_COMPONENT_CONTENT.set(component, content);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
