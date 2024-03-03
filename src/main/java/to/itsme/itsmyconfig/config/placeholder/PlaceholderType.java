package to.itsme.itsmyconfig.config.placeholder;

import java.util.Locale;

public enum PlaceholderType {

    STRING,
    COLOR,
    ANIMATED,
    RANDOM;

    public static PlaceholderType find(final String type) {
        try {
            return PlaceholderType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (final Exception exception) {
            return STRING;
        }
    }

}
