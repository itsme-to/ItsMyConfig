package to.itsme.itsmyconfig.font;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.nio.charset.StandardCharsets;

public enum Font {
    SMALL_CAPS('ᴀ', 'ʙ', 'ᴄ', 'ᴅ', 'ᴇ', 'ꜰ', 'ɢ', 'ʜ', 'ɪ', 'ᴊ', 'ᴋ', 'ʟ', 'ᴍ', 'ɴ', 'ᴏ', 'ᴘ', 'ǫ', 'ʀ', 's', 'ᴛ', 'ᴜ', 'ᴠ', 'ᴡ', 'x', 'ʏ','ᴢ');

    private final char[] characters;
    private final TextReplacementConfig config;

    Font(final char... characters) {
        this.characters = characters;
        final TextReplacementConfig.Builder config = TextReplacementConfig.builder();
        config.match("[A-Za-zÀ-ÿ]").replacement((matchResult, builder) -> {
            final String text = this.apply(matchResult.group());
            return Component.text().content(text);
        });
        this.config = config.build();
    }

    public String apply(final String text) {
        final byte[] bytes = englishify(text).toLowerCase().getBytes(StandardCharsets.UTF_8);
        final StringBuilder builder = new StringBuilder();
        for (byte messageByte : bytes) {
            if (messageByte >= 97 && messageByte <= 122) {
                builder.append(characters[messageByte - 97]);
            } else {
                builder.append((char) messageByte);
            }
        }
        return builder.toString();
    }

    public String englishify(final String frenchText) {
        return frenchText
                .replace("à", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("î", "i")
                .replace("ô", "o")
                .replace("û", "u")
                .replace("ç", "c");
    }

    public Component apply(final Component component) {
        return component.replaceText(config);
    }

}
