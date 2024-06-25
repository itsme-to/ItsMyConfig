package to.itsme.itsmyconfig.placeholder.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        final Component component = Utilities.MM.deserialize(value);
        // legacy text creation
        this.legacyText = LegacyComponentSerializer.legacyAmpersand().serialize(
                component
        );

        this.consoleText = LegacyComponentSerializer.legacySection().serialize(
                component
        );
    }

    @Override
    public String getResult(final Player player, final String[] args) {
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
