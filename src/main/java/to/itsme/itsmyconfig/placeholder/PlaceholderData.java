package to.itsme.itsmyconfig.placeholder;

import org.bukkit.configuration.ConfigurationSection;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PlaceholderData {

    private final PlaceholderType type;
    private final List<Integer> arguments = new ArrayList<>();
    private final List<RequirementData> requirements = new ArrayList<>();

    public PlaceholderData(
            final PlaceholderType type
    ) {
        this.type = type;
    }

    public void registerRequirement(final ConfigurationSection section) {
        String identifier = section.getString("type");
        String input = section.getString("input");
        String output = section.getString("output");
        String deny = section.getString("deny");

        registerArguments(input);
        registerArguments(output);
        registerArguments(deny);
        this.requirements.add(new RequirementData(identifier, input, output, deny));
    }

    public abstract String getResult(final String[] params);

    public String replaceArguments(final String[] params, final String message) {
        return this.replaceArguments(params, message, this.arguments);
    }

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

    public List<RequirementData> getRequirements() {
        return requirements;
    }

    protected void registerArguments(final String string) {
        this.arguments.addAll(Utilities.getArguments(string));
    }

    public PlaceholderType getType() {
        return type;
    }

}
