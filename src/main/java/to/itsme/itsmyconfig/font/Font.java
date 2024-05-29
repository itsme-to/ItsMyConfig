package to.itsme.itsmyconfig.font;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import to.itsme.itsmyconfig.util.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum Font {
    SMALL_CAPS(
            "smallcaps",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
            "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ"
    ),
    UPSIDE_DOWN(
            "upsidedown",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
            "∀qƆpƎℲפHIſʞ˥WNOԀQɹS┴∩ΛMX⅄Zɐqɔpǝɟƃɥᴉɾʞlɯuodbɹsʇnʌʍxʎz"
    );

    private final Map<Character, Character> characterMap;
    private final TextReplacementConfig config;
    private final @TagPattern String name;

    Font(
            @TagPattern final String name,
            final String original,
            final String replacements
    ) {
        this.name = name;
        this.characterMap = createMapping(original, replacements);
        this.config = createConfig();
    }

    public @TagPattern String getName() {
        return name;
    }

    private TextReplacementConfig createConfig() {
        final TextReplacementConfig.Builder config = TextReplacementConfig.builder();
        config.match(Utilities.LETTERS_PATTERN).replacement((matchResult, builder) -> {
            final String text = this.apply(matchResult.group());
            return Component.text().content(text);
        });
        return config.build();
    }

    public String apply(final String text) {
        final byte[] bytes = this.englishify(text).getBytes(StandardCharsets.UTF_8);
        final StringBuilder builder = new StringBuilder();
        for (byte messageByte : bytes) {
            final char originalChar = (char) messageByte;
            final char styledChar = characterMap.getOrDefault(originalChar, originalChar);
            builder.append(styledChar);
        }
        return builder.toString();
    }

    public String englishify(final String text) {
        return text
                .replace("À", "A")
                .replace("Â", "A")
                .replace("à", "a")
                .replace("â", "a")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("Î", "I")
                .replace("î", "i")
                .replace("Ô", "O")
                .replace("ô", "o")
                .replace("Û", "U")
                .replace("û", "u")
                .replace("Ç", "C")
                .replace("ç", "c");
    }

    public Component apply(final Component component) {
        return component.replaceText(config);
    }

    private static Map<Character, Character> createMapping(final String original, final String replacements) {
        if (original.length() != replacements.length()) {
            throw new IllegalArgumentException("Original and replacement texts must be of the same length.");
        }

        final Map<Character, Character> mapping = new HashMap<>();
        for (int i = 0; i < original.length(); i++) {
            mapping.put(original.charAt(i), replacements.charAt(i));
        }
        return mapping;
    }

}
