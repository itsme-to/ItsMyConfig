package ua.realalpha.itsmyconfig.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.realalpha.itsmyconfig.ItsMyConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Pattern;

public final class Utilities {

    private static final ItsMyConfig plugin = ItsMyConfig.getInstance();
    public static final Pattern COLOR_FILTER = Pattern.compile("[§&][a-zA-Z0-9]");

    public static TagResolver papiTag(final Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    public static TagResolver titleTag(final Player player) {
        return TagResolver.resolver("title", (argumentQueue, context) -> {
            final List<Tag.Argument> args = new ArrayList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop());
            }

            if (args.size() == 1) {
                final Title title = Title.title(context.deserialize(args.get(0).value()), Component.empty());
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 2) {
                final Title title = Title.title(context.deserialize(args.get(0).value()), context.deserialize(args.get(1).value()));
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 4) {
                final Title.Times times = createTimes(
                        args.get(0).asInt(),
                        args.get(1).asInt(),
                        args.get(2).asInt()
                );
                final Title title = Title.title(
                        context.deserialize(args.get(3).value()),
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
                        context.deserialize(args.get(3).value()),
                        context.deserialize(args.get(4).value()),
                        times
                );
                plugin.adventure().player(player).showTitle(title);
            }

            return Tag.selfClosingInserting(Component.empty());
        });
    }

    public static TagResolver subtitleTag(final Player player) {
        return TagResolver.resolver("subtitle", (argumentQueue, context) -> {
            final List<Tag.Argument> args = new ArrayList<>();
            while (argumentQueue.hasNext()) {
                args.add(argumentQueue.pop());
            }

            if (args.size() == 1) {
                final Title title = Title.title(Component.empty(), context.deserialize(args.get(0).value()));
                plugin.adventure().player(player).showTitle(title);
            } else if (args.size() == 4) {
                final Title.Times times = createTimes(
                        args.get(0).asInt(),
                        args.get(1).asInt(),
                        args.get(2).asInt()
                );
                final Title title = Title.title(
                        Component.empty(),
                        context.deserialize(args.get(3).value()),
                        times
                );
                plugin.adventure().player(player).showTitle(title);
            }

            return Tag.selfClosingInserting(Component.empty());
        });
    }

    public static TagResolver actionbarTag(final Player player) {
        return TagResolver.resolver("actionbar", (argumentQueue, context) -> {
            final String bar = argumentQueue.popOr("").value();
            plugin.adventure().player(player).sendActionBar(context.deserialize(bar));
            return Tag.selfClosingInserting(Component.empty());
        });
    }

    @SuppressWarnings("all")
    private static Title.Times createTimes(
            @NotNull OptionalInt in,
            @NotNull OptionalInt  s,
            @NotNull OptionalInt  out
    ) {
        Duration fadeIn = Ticks.duration(in.orElse(10));
        Duration stay = Ticks.duration(s.orElse(70));
        Duration fadeOut = Ticks.duration(out.orElse(20));
        return Title.Times.times(fadeIn, stay, fadeOut);
    }


}
