package to.itsme.itsmyconfig.placeholder.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Locale;

public class ColoredTextPlaceholder extends Placeholder {

    private final static LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer
            .builder()
            .character('§')
            .hexCharacter('#')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final static LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer
            .builder()
            .character('&')
            .hexCharacter('#')
            .hexColors()
            .build();

    private final String miniText, legacyText, consoleText;

    public ColoredTextPlaceholder(final String value) {
        super(PlaceholderType.COLORED_TEXT);
        this.miniText = value;

        final Component component = Utilities.MM.deserialize(value);
        // legacy text creation
        this.legacyText = AMPERSAND_SERIALIZER.serialize(
                component
        );

        this.consoleText = SECTION_SERIALIZER.serialize(
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
