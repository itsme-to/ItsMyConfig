package to.itsme.itsmyconfig.placeholder;

import java.util.Locale;

public enum PlaceholderType {

    STRING,
    COLOR,
    RANDOM,
    ANIMATION;

    public static PlaceholderType find(final String type) {
        try {
            return PlaceholderType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (final Exception exception) {
            return STRING;
        }
    }

}
