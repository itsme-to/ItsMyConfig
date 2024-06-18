package to.itsme.itsmyconfig.util;

import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {

    public static final Pattern LETTERS_PATTERN = Pattern.compile("[A-Za-zÀ-ÿ]");
    public static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    public static final Pattern TAGGED_HEX_PATTERN = Pattern.compile("<(/?)#[a-fA-F0-9]{6}>");
    public static final Pattern TAG_PATTERN = Pattern.compile("<(\\w+)(?::\"([^\"]*)\"|:([^<]*))*>");

    private static final Pattern COLOR_FILTER = Pattern.compile("[§&][a-zA-Z0-9]");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");
    private static final Pattern QUOTE_PATTERN = Pattern.compile("<quote(?::([^>]*))?>(.*)</quote>");

    /**
     * Processes a given text and escapes tags based on specified properties.
     *
     * @param text The text to be processed for escaping tags.
     * @return A string with escaped tags based on the given text.
     */
    public static String quote(final String text) {
        final Matcher matcher = QUOTE_PATTERN.matcher(text);
        final StringBuilder result = new StringBuilder(text);

        while (matcher.find()) {
            final String properties = matcher.group(1) != null ? matcher.group(1) : "";
            final String matchedText = matcher.group(2) != null ? matcher.group(2) : "";
            final String escapedText = escapeTags(matchedText, Arrays.asList(
                    properties.toLowerCase().split(":"))
            );

            int start = matcher.start();
            int end = matcher.end();
            result.replace(start, end, escapedText);

            int offset = escapedText.length() - (end - start);
            matcher.region(end + offset, text.length() + offset);
        }

        return result.toString();
    }

    /**
     * Extracts integer arguments enclosed within curly braces from the given string.
     *
     * @param string The string from which to extract integer arguments.
     * @return A List of Integer containing the extracted integer arguments.
     */
    public static List<Integer> getArguments(final String string) {
        if (string == null || string.isEmpty()) {
            return Collections.emptyList();
        }

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
     * Escapes Tags based on the special properties provided.
     *
     * @param text The text that contains tags to be escaped.
     * @param properties The properties that the method should follow.
     *
     * @return The text after escaping tags.
     */
    private static String escapeTags(
            final String text,
            final List<String> properties
    ) {
        final Matcher matcher = TAG_PATTERN.matcher(text);
        final StringBuilder builder = new StringBuilder(text);

        int offset = 0;
        while (matcher.find()) {
            if (!properties.isEmpty()) {
                final String found = matcher.group();
                final String content = found.substring(1, found.length() - 1);
                if (properties.contains("ignorecolors")) {
                    if (isColor(content)) continue;
                }
                if (properties.contains("ignoredecorations")) {
                    if (isDecoration(content)) continue;
                }
            }
            final int foundIndex = matcher.start() + offset;
            if (foundIndex != -1 && builder.charAt(Math.max(0, foundIndex - 1)) != '\\') {
                builder.insert(foundIndex, '\\');
                offset++;
            }
        }

        return builder.toString();
    }

    /**
     * Checks if the provided tag content represents a color.
     *
     * @param tagContent The content of the tag to be checked for color.
     * @return {@code true} if the tag content represents a color, {@code false} otherwise.
     */
    public static boolean isColor(final String tagContent) {
        for (final String split : tagContent.split(":")) {
            if (StandardTags.color().has(split)) return true;
        }
        return false;
    }

    /**
     * Checks if the provided tag content represents a decoration.
     *
     * @param tagContent The content of the tag to be checked for decoration.
     * @return {@code true} if the tag content represents a decoration, {@code false} otherwise.
     */
    public static boolean isDecoration(final String tagContent) {
        for (final String split : tagContent.split(":")) {
            if (StandardTags.decorations().has(split)) return true;
        }
        return false;
    }

    public static String textless(final String text) {
        final StringBuilder builder = new StringBuilder();
        for (final char character : text.toCharArray()) {
            if (Character.isDigit(character) || (character == '.' && !builder.toString().contains("."))) {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    public static int intOrDefault(final String text, final int defaultInt) {
        try {
            return Integer.parseInt(textless(text));
        } catch (final Throwable ignored) { return defaultInt; }
    }

    public static float floatOrDefault(final String text, final float defaultDouble) {
        try {
            return Float.parseFloat(textless(text));
        } catch (final Throwable ignored) { return defaultDouble; }
    }

}
