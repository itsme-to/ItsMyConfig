package to.itsme.itsmyconfig.tag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.tag.api.ArgumentsTag;
import to.itsme.itsmyconfig.tag.api.Cancellable;
import to.itsme.itsmyconfig.tag.api.Tag;
import to.itsme.itsmyconfig.tag.impl.*;
import to.itsme.itsmyconfig.tag.impl.title.SubtitleTag;
import to.itsme.itsmyconfig.tag.impl.title.TitleTag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TagManager {

    private static final Pattern ARG_TAG_PATTERN = Pattern.compile("<(\\w+)((?::\"([^\"]*)\"|:'([^']*)'|:([^<]*))*)>");
    private static final Pattern ARG_PATTERN = Pattern.compile(":\"([^\"]*)\"|:'([^']*)'|:([^:\"]*)");

    private static int INITIAL_CAPACITY;
    private static final Map<String, Tag> tags = new ConcurrentHashMap<>();

    static {
        final AtomicInteger defaultCapacity = new AtomicInteger();
        Arrays.asList(
                new RepeatTag(), new DelayTag(),
                new BossbarTag(), new ActiobarTag(),
                new TitleTag(), new SubtitleTag(), new SoundTag()
        ).forEach(tag -> {
            tags.put(tag.name(), tag);
            defaultCapacity.set(Math.max(tag.maxArguments(), defaultCapacity.get()));
        });

        INITIAL_CAPACITY = defaultCapacity.get();
    }

    public static String process(
            final Player player,
            @NotNull String text
    ) {
        // handle argument tags
        text = processArgumentTags(player, text);

        return text;
    }

    public static String processArgumentTags(
            final Player player,
            @NotNull String text
    ) {
        Matcher matcher = ARG_TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            final String match = matcher.group(0);

            final int index = text.indexOf(match);
            if (index > 0) {
                if (text.charAt(index - 1) == '\\') {
                    continue;
                }
            }

            final String tagName = matcher.group(1);
            final Tag tag = tags.get(tagName);
            if (!(tag instanceof ArgumentsTag)) {
                continue;
            }

            final String arguments = matcher.group(2);
            final ArrayList<String> args = getArguments(arguments);

            if (args.size() == 1 && args.get(0).equals("cancel")) {
                if (tag instanceof Cancellable) {
                    ((Cancellable) tag).cancelFor(player);
                    return "";
                }
            }

            final String replaced;
            final ArgumentsTag argumentsTag = (ArgumentsTag) tag;
            if (args.size() < argumentsTag.minArguments()) {
                replaced = "[Not enough argument for Tag: " + tagName + "]";
            } else if (args.size() > argumentsTag.maxArguments()) {
                replaced = "[Too much arguments for Tag: " + tagName + "]";
            } else {
                replaced = argumentsTag.process(player, args.toArray(new String[0]));
            }

            text = text.substring(0, index) + replaced + text.substring(matcher.end());
            matcher = ARG_TAG_PATTERN.matcher(text);
        }

        return text;
    }

    private static ArrayList<String> getArguments(final String arguments) {
        final Matcher argMatcher = ARG_PATTERN.matcher(arguments);
        final ArrayList<String> args = new ArrayList<>(INITIAL_CAPACITY);
        while (argMatcher.find()) {
            if (argMatcher.group(1) != null) {
                args.add(argMatcher.group(1)); // Double-quoted argument
            } else if (argMatcher.group(2) != null) {
                args.add(argMatcher.group(2)); // Single-quoted argument
            } else if (argMatcher.group(3) != null) {
                args.add(argMatcher.group(3)); // Unquoted argument
            }
        }

        if (INITIAL_CAPACITY < args.size()) {
            INITIAL_CAPACITY = args.size();
        }

        return args;
    }

}