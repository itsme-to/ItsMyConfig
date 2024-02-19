package ua.realalpha.itsmyconfig.config.placeholder;

public enum PlaceholderType {

    STRING,
    COLOR;

    public static PlaceholderType find(final String type) {
        try {
            return PlaceholderType.valueOf(type);
        } catch (final Exception exception) {
            return STRING;
        }
    }

}
