package to.itsme.itsmyconfig.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.RequirementData;
import to.itsme.itsmyconfig.requirement.type.NumberRequirement;
import to.itsme.itsmyconfig.requirement.type.RegexRequirement;
import to.itsme.itsmyconfig.requirement.type.StringRequirement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        return this.requirements.stream().filter(requirement -> requirement.matchIdentifier(type)).findAny().orElse(null);
    }

    /**
     * Retrieves the deny message for a placeholder data object.
     *
     * @param data    The PlaceholderData object.
     * @param player  The Player object.
     * @param params  The array of strings.
     * @return The deny message as a string, or null if there is no deny message.
     */
    public String getDenyMessage(
            final PlaceholderData data,
            final Player player,
            final String[] params
    ) {
        Optional<String> denyMessage = data.getRequirements().stream().map(requirementData -> processRequirementData(requirementData, data, player, params))
                .filter(Objects::nonNull).findFirst();

        return denyMessage.orElse(null);
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
            final PlaceholderData data,
            final Player player,
            final String[] params) {
        final Requirement<?> requirement = this.getRequirementByType(requirementData.getIdentifier());

        if (requirement == null) {
            return null;
        }

        final String input = getParameters(player, data, requirementData.getInput(), params);
        final String output = getParameters(player, data, requirementData.getOutput(), params);

        if (requirement.validate(requirementData.getIdentifier(), input, output)) {
            return null;
        }
        return data.replaceArguments(params, requirementData.getDeny());
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
    private String getParameters(Player player, PlaceholderData data, String parameter, String[] params) {
        return PlaceholderAPI.setPlaceholders(player, data.replaceArguments(params, parameter));
    }
}
