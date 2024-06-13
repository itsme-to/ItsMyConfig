package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.ChatColor;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Strings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

public class ColoredTextPlaceholder extends Placeholder {

    /**
     * Represents a map of decoration properties.
     */
    private final static Map<String, String> DECORATIONS_PROPERTIES = new HashMap<String, String>() {
        {
            for (final ChatColor color : ChatColor.values()) {
                final String name = color.name().toLowerCase();
                put("<" + name + ">", "&" + color.getChar());

                if (name.contains("_")) {
                    put("<" + name.replaceAll("_", "") + ">", "&" + color.getChar());
                }
            }
        }
    };

    private final String miniText, legacyText, consoleText;

    public ColoredTextPlaceholder(final String value) {
        super(PlaceholderType.COLORED_TEXT);
        this.miniText = value;

        // legacy text creation
        String copy = value;

        Matcher matcher = Strings.TAG_PATTERN.matcher(copy);
        while (matcher.find()) {
            final String found = matcher.group();
            final String replacement = DECORATIONS_PROPERTIES.get(found.toLowerCase());
            if (replacement != null) {
                copy = copy.replace(found, replacement);
                matcher = Strings.TAG_PATTERN.matcher(copy);
            } else if (Strings.TAGGED_HEX_PATTERN.matcher(copy).matches()) {
                copy = copy.replace(found, "&#" + found.replace('<', ' ').replace('>', ' '));
                matcher = Strings.TAG_PATTERN.matcher(copy);
            }
        }

        this.legacyText = copy;


        Matcher hexMatcher = Strings.HEX_PATTERN.matcher(copy);
        while (matcher.find()) {
            final String found = hexMatcher.group();
            final String hexColor = found.substring(1);
            final StringBuilder minecraftFormat = new StringBuilder("§x");
            for (int i = 0; i < hexColor.length(); i++) {
                minecraftFormat.append("§").append(hexColor.charAt(i));
            }

            copy = copy.replace(found, minecraftFormat);
            hexMatcher = Strings.HEX_PATTERN.matcher(copy);
        }


        this.consoleText = copy;
    }

    @Override
    public String getResult(final String[] args) {
        if (args.length == 0) {
            return miniText;
        }

        final String firstArg = args[0].toLowerCase(Locale.ROOT);
        switch (firstArg) {
            case "mini":
                return this.miniText;
            case "legacy":
                return this.legacyText;
            case "console":
                return this.consoleText;
        }

        return "";
    }

}
