package to.itsme.itsmyconfig.tag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.tag.api.ArgumentsTag;
import to.itsme.itsmyconfig.tag.api.Cancellable;
import to.itsme.itsmyconfig.tag.api.Tag;
import to.itsme.itsmyconfig.tag.impl.BossbarTag;
import to.itsme.itsmyconfig.tag.impl.DelayTag;
import to.itsme.itsmyconfig.tag.impl.RepeatTag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TagManager {

    private static final Pattern ARG_TAG_PATTERN = Pattern.compile("<(\\w+)((?::\"([^\"]*)\"|:(?:(?!<\\w+).)*)*)>");
    private static final Pattern ARG_PATTERN = Pattern.compile(":\"([^\"]*)\"|:([^:\"]*)");

    private static final Map<String, Tag> tags = new ConcurrentHashMap<>();

    static {
        Arrays.asList(
                new BossbarTag(), new RepeatTag(), new DelayTag()
        ).forEach(tag -> tags.put(tag.name(), tag));
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
            final Matcher argMatcher = ARG_PATTERN.matcher(arguments);
            final ArrayList<String> args = new ArrayList<>();
            while (argMatcher.find()) {
                if (argMatcher.group(1) != null) {
                    args.add(argMatcher.group(1)); // Quoted argument
                } else if (argMatcher.group(2) != null) {
                    args.add(argMatcher.group(2)); // Unquoted argument
                }
            }

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

}