package to.itsme.itsmyconfig.config.placeholder;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderData {
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");

    private final String message;
    private final PlaceholderType type;
    private final Set<Integer> arguments = new HashSet<>();
    private final List<RequirementData> requirements = new ArrayList<>();

    public PlaceholderData(final String message, final String type) {
        this.message = message;
        this.type = PlaceholderType.find(type);
        registerArguments(message);
    }

    public void registerRequirement(ConfigurationSection section) {
        String identifier = section.getString("type");
        String input = section.getString("input");
        String output = section.getString("output");
        String deny = section.getString("deny");

        registerArguments(input);
        registerArguments(output);
        registerArguments(deny);
        this.requirements.add(new RequirementData(identifier, input, output, deny));
    }

    public String replaceArguments(String[] params) {
        return this.replaceArguments(params, this.message);
    }

    public String replaceArguments(String[] params, String message) {
        if (params.length >= 1) {
            String output = message;

            for (Integer argument : this.arguments) {
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

    private void registerArguments(String string) {
        Matcher matcher = ARGUMENT_PATTERN.matcher(string);
        while (matcher.find()) {
            arguments.add(Integer.parseInt(matcher.group(1)));
        }
    }
}
