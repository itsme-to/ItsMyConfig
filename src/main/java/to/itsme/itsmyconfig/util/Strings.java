package to.itsme.itsmyconfig.util;

import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {

    public static String symbolPrefix;
    public static Pattern symbolPrefixPattern;

    public static final String DEBUG_HYPHEN = "###############################################";

    public static final Pattern LETTERS_PATTERN = Pattern.compile("[A-Za-zÀ-ÿ]");
    public static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    public static final Pattern COLOR_SYMBOL_PATTERN = Pattern.compile(Pattern.quote("§"));
    public static final Pattern TAG_PATTERN = Pattern.compile("<(\\w+)(?::\"([^\"]*)\"|:([^<]*))*>");

    private static final Pattern COLOR_FILTER = Pattern.compile("[§&][a-zA-Z0-9]");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");
    private static final Pattern DIACRITICAL_MARKS_PATTERN = Pattern.compile("\\p{M}");
    private static final Pattern QUOTE_PATTERN = Pattern.compile("<quote(?::([^>]*))?>(.*)</quote>");

    /**
     * An array of integer values used for converting numbers to Roman numerals.
     */
    private static final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    /**
     * Class: DynamicPlaceHolder
     * Variable: romanLiterals
     * <p>
     * Description:
     * The `romanLiterals` variable is an array of strings representing the Roman numerals.
     * It contains the following literals: ["M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"].
     * These literals are used in the `integerToRoman` method to convert an integer to its Roman numeral representation.
     */
    private static final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    /**
     * Sets the symbol prefix that is used for editing Strings
     */
    public static void setSymbolPrefix(final String symbolPrefix) {
        Strings.symbolPrefix = symbolPrefix;
        Strings.symbolPrefixPattern = Pattern.compile(Pattern.quote(symbolPrefix));
    }

    /**
     * Processes a given text and escapes tags based on specified properties.
     *
     * @param text The text to be processed for escaping tags.
     * @return A string with escaped tags based on the given text.
     */
    public static String quote(final String text) {
        final Matcher matcher = QUOTE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }

        StringBuilder builder = new StringBuilder();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            builder.append(text, lastEnd, matcher.start());

            final String propertyString = matcher.group(1) != null ? matcher.group(1) : "";
            final Set<String> properties = propertyString.isEmpty() ? Collections.emptySet() : Set.of(propertyString.toLowerCase().split(":"));
            final String content = matcher.group(2) != null ? matcher.group(2) : "";

            builder.append(escapeTags(content, properties));

            lastEnd = matcher.end();
        }

        builder.append(text.substring(lastEnd));
        return builder.toString();
    }

    /**
     * Extracts integer arguments enclosed within curly braces from the given string.
     *
     * @param string The string from which to extract integer arguments.
     * @return A List of Integer containing the extracted integer arguments.
     */
    public static Collection<Integer> getArguments(final String string) {
        if (string == null || string.length() < 3) {
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
    private static String escapeTags(final String text, final Set<String> properties) {
        final Matcher matcher = TAG_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            final String found = matcher.group();
            final String content = found.substring(1, found.length() - 1);

            boolean skip = false;
            if (properties.contains("ignorecolors") && isColor(content)) {
                skip = true;
            } else if (properties.contains("ignoredecorations") && isDecoration(content)) {
                skip = true;
            }

            builder.append(text, lastEnd, matcher.start());
            if (!skip) {
                builder.append('\\');
            }
            builder.append(found);
            lastEnd = matcher.end();
        }

        builder.append(text.substring(lastEnd));
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

    /**
     * Checks if the given string is a valid number.
     *
     * @param str the string to check
     * @return {@code true} if the string is a valid number, {@code false} otherwise
     */
    public static boolean isNumber(final String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Converts a string to an integer, returning a default value if the conversion fails.
     *
     * @param text the string to convert
     * @param defaultInt the default value to return if conversion fails
     * @return the converted integer or the default value
     */
    public static int intOrDefault(final String text, final int defaultInt) {
        try {
            return Integer.parseInt(textless(text));
        } catch (final Throwable ignored) { return defaultInt; }
    }

    /**
     * Converts a string to a long, returning a default value if the conversion fails.\
     *
     * @param text the string to convert
     * @param defaultFloat the default value to return if conversion fails
     * @return the converted float or the default value
     */
    public static float floatOrDefault(final String text, final float defaultFloat) {
        try {
            return Float.parseFloat(textless(text));
        } catch (final Throwable ignored) { return defaultFloat; }
    }

    /**
     * Converts an integer to a Roman numeral representation.
     *
     * @param num The integer to convert.
     * @return The Roman numeral representation of the given integer.
     */
    public static String integerToRoman(int num) {
        final StringBuilder roman = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }

    /**
     * Converts a list of strings to a string, where each string is represented on a new line.
     *
     * @param  collection The collection of strings to be converted to a string.
     * @return The string representation of the list.
     */
    public static String toString(final @NotNull Collection<String> collection) {
        return String.join(System.lineSeparator(), collection);
    }

    /**
     * Converts a string containing accented characters into a string with plain
     * English characters by removing diacritical marks (accents).
     *
     * <p>This method normalizes the input text by decomposing accented characters
     * into their base characters followed by diacritical marks. It then removes
     * the diacritical marks, resulting in a string composed of basic Latin letters.</p>
     *
     * <p>For example, the input string "À la carte" would be converted to "A la carte".</p>
     *
     * @param text the input string potentially containing accented characters
     * @return a new string with the accented characters converted to plain English characters
     */
    public static String englishify(final String text) {
        final String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return DIACRITICAL_MARKS_PATTERN.matcher(normalized).replaceAll("");
    }

    /**
     * Checks if the provided message starts with the "$" symbol
     * @param message the checked message
     */
    public static boolean startsWithSymbol(final String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        int tagDepth = 0;
        for (var i = 0; i < message.length(); i++) {
            char character = message.charAt(i);
            if (character == '&' || character == '§') {
                i++;
                continue;
            }
            if (character == '<') {
                tagDepth++;
                continue;
            } else if (character == '>' && tagDepth > 0) {
                tagDepth--;
                continue;
            }

            if (tagDepth > 0 || Character.isWhitespace(character)) {
                continue;
            }

            return message.startsWith(symbolPrefix, i);
        }

        return false;
    }

    /**
     * Removes the '§' symbol and replaces it with '&'
     * <br>
     * Also removes the first '$' symbol it meets
     *
     * @param message the provided message
     */
    public static String processMessage(final String message) {
        return Strings.COLOR_SYMBOL_PATTERN.matcher(symbolPrefixPattern.matcher(message).replaceFirst("")).replaceAll("&");
    }

}
