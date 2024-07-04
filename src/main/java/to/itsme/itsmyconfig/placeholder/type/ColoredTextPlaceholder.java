package to.itsme.itsmyconfig.placeholder.type;

import dev.dejvokep.boostedyaml.block.implementation.Section;
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

    private final String miniText;

    public ColoredTextPlaceholder(final Section section) {
        super(section, PlaceholderType.COLORED_TEXT);
        this.miniText = section.getString("value", "");
        this.registerArguments(this.miniText);
    }

    @Override
    public String getResult(final Player player, final String[] args) {
        if (args.length == 0) {
            return this.miniText;
        }

        final String firstArg = args[0].toLowerCase(Locale.ROOT);
        switch (firstArg) {
            case "legacy":
                return this.replaceArguments(
                        args,
                        AMPERSAND_SERIALIZER.serialize(
                                Utilities.translate(this.miniText, player)
                        ), 1
                );
            case "console":
                return this.replaceArguments(
                        args,
                        SECTION_SERIALIZER.serialize(
                                Utilities.translate(this.miniText, player)
                        ), 1
                );
            case "mini":
                return this.replaceArguments(args, this.miniText, 1);
            default:
                return this.replaceArguments(args, this.miniText);
        }
    }

}
