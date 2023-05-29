package ua.realalpha.itsmyconfig.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.config.placeholder.PlaceholderData;
import ua.realalpha.itsmyconfig.config.placeholder.RequirementData;
import ua.realalpha.itsmyconfig.requirement.type.NumberRequirement;
import ua.realalpha.itsmyconfig.requirement.type.RegexRequirement;
import ua.realalpha.itsmyconfig.requirement.type.StringRequirement;

import java.util.Arrays;
import java.util.List;

public class RequirementManager {

    private final List<Requirement<?>> requirements = Arrays.asList(
            new NumberRequirement(),
            new RegexRequirement(),
            new StringRequirement()
    );


    public Requirement<?> getRequirementByType(String type) {
        return this.requirements.stream().filter(requirement -> requirement.matchIdentifier(type)).findAny().orElse(null);
    }

    public String getDenyMessage(PlaceholderData data, Player player) {
        for (RequirementData requirementData : data.getRequirements()) {
            Requirement<?> requirement = this.getRequirementByType(requirementData.getIdentifier());
            if (requirement == null) continue;
            if (requirement.validate(requirementData.getIdentifier(),
                    PlaceholderAPI.setPlaceholders(player, requirementData.getInput()),
                    PlaceholderAPI.setPlaceholders(player, requirementData.getOutput())
            )) continue;
            return ChatColor.translateAlternateColorCodes('&', requirementData.getDeny());
        }

        return null;
    }
}
