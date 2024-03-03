package to.itsme.itsmyconfig.config.placeholder;

import org.bukkit.configuration.ConfigurationSection;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PlaceholderData {

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");

    private final PlaceholderType type;
    private final Set<Integer> arguments = new HashSet<>();
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
        if (params.length >= 1) {
            String output = message;

            for (final Integer argument : this.arguments) {
                if (argument >= params.length) {
                    continue;
                }
                output = output.replaceAll(Pattern.quote("{" + argument + "}"), params[argument].replace("$", "\\$"));
            }

            return output;
        } else {
            return message;
        }
    }

    public String replaceArguments(final String[] params, final String message, List<Integer> arguments) {
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

}
