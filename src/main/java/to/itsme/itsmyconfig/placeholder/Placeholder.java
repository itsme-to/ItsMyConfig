package to.itsme.itsmyconfig.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.placeholder.type.ColorPlaceholder;
import to.itsme.itsmyconfig.requirement.RequirementData;
import to.itsme.itsmyconfig.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The PlaceholderData class is an abstract class that represents the basic structure of a placeholder data object.
 * It provides methods for registering requirements, obtaining the placeholder type, and generating the placeholder result.
 */
public abstract class Placeholder {

    /**
     * Represents the plugin variable for ItsMyConfig.
     * ItsMyConfig is a JavaPlugin that manages configuration, placeholders, and requirements.
     *
     * @see ItsMyConfig
     */
    private final ItsMyConfig plugin = ItsMyConfig.getInstance();

    /**
     * Represents the type of a placeholder.
     */
    private final PlaceholderType type;
    /**
     *
     */
    protected final List<Integer> arguments = new ArrayList<>();
    /**
     * Represents a list of requirement data.
     */
    private final List<RequirementData> requirements = new ArrayList<>();

    /**
     * Represents a placeholder data object.
     */
    public Placeholder(final PlaceholderType type) {
        this.type = type;
    }

    /**
     * Registers a requirement based on the provided ConfigurationSection.
     *
     * @param section The ConfigurationSection containing requirement data.
     */
    public void registerRequirement(final ConfigurationSection section) {
        final String identifier = section.getString("type");
        this.registerArgumentsFor(section, identifier);
        this.requirements.add(
                new RequirementData(
                        identifier,
                        section.getString("input"),
                        section.getString("output"),
                        section.getString("deny")
                )
        );
    }

    /**
     * Registers the arguments for a given configuration section and argument name.
     *
     * @param section       the ConfigurationSection to retrieve the argument value from
     * @param argumentName  the name of the argument in the ConfigurationSection
     */
    private void registerArgumentsFor(ConfigurationSection section, String argumentName) {
        final String argumentValue = section.getString(argumentName);
        this.registerArguments(argumentValue);
    }

    /**
     * Converts the given Player and arguments to a formatted string.
     *
     * @param player The Player object.
     * @param args   The array of strings.
     * @return The formatted string.
     */
    public String asString(final Player player, final String[] args) {
        final String deny = getColorTranslatedMessage(player, args);
        if (deny != null) {
            return deny;
        }

        final String result = PlaceholderAPI.setPlaceholders(player, this.getResult(player, args));
        return needColorTranslation() ? ChatColor.translateAlternateColorCodes('&', result) : result;
    }

    /**
     * Translates a color-coded message using the given player and arguments,
     * using the deny message obtained from the plugin's requirement manager.
     *
     * @param player The player to translate the message for.
     * @param args   The arguments to use in the translation.
     * @return The translated message if a deny message is found, null otherwise.
     */
    private String getColorTranslatedMessage(final Player player, final String[] args) {
        final String deny = this.plugin.getRequirementManager().getDenyMessage(this, player, args);
        return deny != null ? ChatColor.translateAlternateColorCodes('&', deny) : null;
    }

    /**
     * Determines if a color translation is needed based on the type of placeholder data.
     *
     * @return true if color translation is needed, false otherwise
     */
    private boolean needColorTranslation() {
        return !(this instanceof ColorPlaceholder);
    }

    /**
     * This method is used to retrieve the result of a placeholder evaluation.
     *
     * @param args The arguments used for the placeholder evaluation.
     * @return The result of the placeholder evaluation as a string.
     */
    public abstract String getResult(final Player player, final String[] args);

    /**
     * Replaces arguments in a given message string.
     *
     * @param params     The array of parameters to use for replacement.
     * @param message    The message string to replace arguments in.
     * @return The message string with replaced arguments.
     */
    public String replaceArguments(final String[] params, final String message) {
        return this.replaceArguments(params, message, this.arguments);
    }

    /**
     * Replaces placeholders in a given message with the provided arguments.
     *
     * @param params     The array of parameters to replace the placeholders with.
     * @param message    The message string containing the placeholders.
     * @param arguments  The list of integer arguments representing the indices of parameters to replace.
     * @return The updated message string with placeholders replaced by the corresponding parameters.
     */
    public String replaceArguments(
            final String[] params,
            final String message,
            final List<Integer> arguments
    ) {
        if (params.length >= 1) {
            String output = message;

            for (final Integer argument : arguments) {
                int index = argument;
                if (index >= params.length) continue;
                // Dollar signs are quoted before using replaceAll
                output = output.replaceAll(Pattern.quote("{" + argument + "}"), params[index].replace("$", "\\$"));
            }

            return output;
        } else {
            return message;
        }
    }

    /**
     * Retrieves a list of RequirementData objects representing the requirements for a PlaceholderData object.
     *
     * @return a list of RequirementData objects representing the requirements
     */
    public List<RequirementData> getRequirements() {
        return requirements;
    }

    /**
     * Registers arguments for the PlaceholderData object.
     * This method adds the arguments obtained from the given string to the existing list of arguments.
     *
     * @param string The string containing the arguments to be registered.
     */
    protected void registerArguments(final String string) {
        this.arguments.addAll(Strings.getArguments(string));
    }

    /**
     * Retrieves the type of the placeholder.
     *
     * @return The type of the placeholder.
     */
    public PlaceholderType getType() {
        return type;
    }

}
