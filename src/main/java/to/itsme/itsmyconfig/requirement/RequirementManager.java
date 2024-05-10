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
    private final List<Requirement<?>> requirements = Arrays.asList(
            new NumberRequirement(),
            new RegexRequirement(),
            new StringRequirement()
    );

    public Requirement<?> getRequirementByType(final String type) {
        return this.requirements.stream().filter(requirement -> requirement.matchIdentifier(type)).findAny().orElse(null);
    }

    public String getDenyMessage(
            final PlaceholderData data,
            final Player player,
            final String[] params
    ) {
        Optional<String> denyMessage = data.getRequirements().stream().map(requirementData -> processRequirementData(requirementData, data, player, params))
                .filter(Objects::nonNull).findFirst();

        return denyMessage.orElse(null);
    }

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

    private String getParameters(Player player, PlaceholderData data, String parameter, String[] params) {
        return PlaceholderAPI.setPlaceholders(player, data.replaceArguments(params, parameter));
    }
}
