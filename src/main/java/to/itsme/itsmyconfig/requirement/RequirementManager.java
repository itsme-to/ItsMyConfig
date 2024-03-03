package to.itsme.itsmyconfig.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.config.placeholder.RequirementData;
import to.itsme.itsmyconfig.requirement.type.NumberRequirement;
import to.itsme.itsmyconfig.requirement.type.RegexRequirement;
import to.itsme.itsmyconfig.requirement.type.StringRequirement;

import java.util.Arrays;
import java.util.List;

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
        for (final RequirementData requirementData : data.getRequirements()) {
            final Requirement<?> requirement = this.getRequirementByType(requirementData.getIdentifier());
            if (requirement == null) continue;
            String input = PlaceholderAPI.setPlaceholders(player, data.replaceArguments(params, requirementData.getInput()));
            String output = PlaceholderAPI.setPlaceholders(player, data.replaceArguments(params, requirementData.getOutput()));
            if (requirement.validate(requirementData.getIdentifier(), input, output)) continue;
            return data.replaceArguments(params, requirementData.getDeny());
        }

        return null;
    }

}
