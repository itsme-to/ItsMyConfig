package to.itsme.itsmyconfig.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.requirement.type.NumberRequirement;
import to.itsme.itsmyconfig.requirement.type.RegexRequirement;
import to.itsme.itsmyconfig.requirement.type.StringRequirement;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for managing requirements and validating them.
 */
public final class RequirementManager {
    /**
     * The RequirementManager class is responsible for managing requirements and validating them.
     */
    private final List<Requirement<?>> requirements = Arrays.asList(
            new NumberRequirement(),
            new RegexRequirement(),
            new StringRequirement()
    );

    /**
     * Retrieves a Requirement object that matches the given type.
     *
     * @param type the type of the Requirement object to retrieve
     * @return the Requirement object that matches the given type, or null if no match is found
     */
    public Requirement<?> getRequirementByType(final String type) {
        for (final Requirement<?> requirement : this.requirements) {
            if (requirement.matchIdentifier(type)) {
                return requirement;
            }
        }
        return null;
    }

    /**
     * Retrieves the deny message for a placeholder data object.
     *
     * @param placeholder    The PlaceholderData object.
     * @param player  The Player object.
     * @param params  The array of strings.
     * @return The deny message as a string, or null if there is no deny message.
     */
    public String getDenyMessage(
            final Placeholder placeholder,
            final Player player,
            final String[] params
    ) {
        for (final RequirementData requirement : placeholder.getRequirements()) {
            final String deny = processRequirementData(requirement, placeholder, player, params);
            if (deny != null) {
                return deny;
            }
        }
        return null;
    }

    /**
     * Processes the requirement data and performs validation.
     *
     * @param requirementData The RequirementData object representing the requirement to be processed.
     * @param data The PlaceholderData object containing the data needed for processing.
     * @param player The Player object representing the player.
     * @param params The array of parameters to be used for substitution.
     * @return The deny message if the requirement is not met, or null if the requirement is met.
     */
    private String processRequirementData(
            final RequirementData requirementData,
            final Placeholder data,
            final Player player,
            final String[] params
    ) {
        final Requirement<?> requirement = this.getRequirementByType(requirementData.getIdentifier());

        if (requirement == null) {
            return null;
        }

        final String input = getParameters(player, data, requirementData.getInput(), params);
        final String output = getParameters(player, data, requirementData.getOutput(), params);

        if (requirement.validate(requirementData.getIdentifier(), input, output)) {
            return null;
        }

        return requirementData.getDeny();
    }

    /**
     * Retrieves the parameters for a placeholder evaluation by replacing arguments in a given message string.
     *
     * @param player     The Player object.
     * @param data       The PlaceholderData object.
     * @param parameter  The parameter to be replaced.
     * @param params     The array of parameters to use for replacement.
     * @return The message string with replaced arguments.
     */
    private String getParameters(
            final Player player,
            final Placeholder data,
            final String parameter,
            final String[] params
    ) {
        return PlaceholderAPI.setPlaceholders(player, data.replaceArguments(params, parameter));
    }

}
