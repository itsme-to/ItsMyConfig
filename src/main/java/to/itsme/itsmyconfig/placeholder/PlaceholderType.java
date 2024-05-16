package to.itsme.itsmyconfig.placeholder;

import java.util.Locale;

/**
 * Represents the type of a placeholder.
 */
public enum PlaceholderType {

    /**
     * Represents the type of a placeholder.
     */
    STRING,
    /**
     * Represents the color type of a placeholder.
     */
    COLOR,
    /**
     * Represents a placeholder type for generating random values.
     * <p>
     * This class is an enumeration that defines different types of placeholders.
     * The RANDOM placeholder type can be used to generate random values.
     * <p>
     * Example usage:
     * <p>
     * PlaceholderType type = PlaceholderType.RANDOM;
     * <p>
     * PlaceholderType findType = PlaceholderType.find("RANDOM");
     * <p>
     * The PlaceholderType enum is defined in the PlaceholderType class.
     */
    RANDOM,
    /**
     * Represents the type of an animation placeholder.
     */
    ANIMATION;

    /**
     * Finds the PlaceholderType for the given type.
     *
     * @param type The string value representing the type of placeholder.
     * @return The PlaceholderType for the given type, or STRING if the type is not found or an exception occurs.
     */
    public static PlaceholderType find(final String type) {
        try {
            return convertToPlaceholderType(type);
        } catch (final Exception exception) {
            return STRING;
        }
    }

    /**
     * Converts a given string to a PlaceholderType enumeration value.
     *
     * @param type the string representing the placeholder type
     * @return the PlaceholderType enumeration value corresponding to the given type string
     * @throws IllegalArgumentException if the given type is not a valid PlaceholderType value
     */
    private static PlaceholderType convertToPlaceholderType(final String type) {
        return PlaceholderType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }

}
