package to.itsme.itsmyconfig.placeholder.type;

import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

/**
 * The StringPlaceholderData class represents a placeholder data object for strings.
 * It extends the PlaceholderData class and provides methods for registering arguments,
 * obtaining the result of a placeholder evaluation, and replacing arguments in a given message string.
 */
public final class StringPlaceholderData extends PlaceholderData {

    /**
     * The message string for the placeholder data.
     */
    private final String message;

    /**
     * Represents a placeholder data object for strings.
     * It extends the PlaceholderData class and provides methods for registering arguments,
     * obtaining the result of a placeholder evaluation, and replacing arguments in a given message string.
     *
     * @param message The message string for the placeholder data.
     */
    public StringPlaceholderData(final String message) {
        super(PlaceholderType.STRING);
        this.message = message;
        registerArguments(this.message);
    }

    /**
     * Replaces arguments in a given message string.
     *
     * @param params     The array of parameters to use for replacement.
     * @return The message string with replaced arguments.
     */
    @Override
    public String getResult(final String[] params) {
        return this.replaceArguments(params, this.message);
    }

}
