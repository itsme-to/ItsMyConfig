package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

/**
 * The StringPlaceholderData class represents a placeholder data object for strings.
 * It extends the PlaceholderData class and provides methods for registering arguments,
 * obtaining the result of a placeholder evaluation, and replacing arguments in a given message string.
 */
public final class StringPlaceholder extends Placeholder {

    /**
     * The message string for the placeholder data.
     */
    private final String message;

    /**
     * Represents a placeholder data object for strings.
     * It extends the PlaceholderData class and provides methods for registering arguments,
     * obtaining the result of a placeholder evaluation, and replacing arguments in a given message string.
     */
    public StringPlaceholder(final ConfigurationSection section) {
        super(section, PlaceholderType.STRING);
        this.message = section.getString("value", "");
        this.registerArguments(this.message);
    }

    /**
     * Replaces arguments in a given message string.
     *
     * @param params     The array of parameters to use for replacement.
     * @return The message string with replaced arguments.
     */
    @Override
    public String getResult(final Player player, final String[] params) {
        return this.replaceArguments(params, this.message);
    }

}
