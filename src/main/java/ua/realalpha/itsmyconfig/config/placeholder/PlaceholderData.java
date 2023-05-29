package ua.realalpha.itsmyconfig.config.placeholder;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderData {

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");

    private final String message;
    private final List<Integer> arguments;
    private final List<RequirementData> requirements = new ArrayList<>();

    public PlaceholderData(String message) {
        this.message = message;
        List<Integer> arguments = null;
        Matcher matcher = ARGUMENT_PATTERN.matcher(message);
        while (matcher.find()) {
            if (arguments == null) {
                arguments = new ArrayList<>();
            }

            arguments.add(Integer.parseInt(matcher.group(1)));
        }

        this.arguments = arguments;
    }

    public void registerRequirement(ConfigurationSection section) {
        String identifier = section.getString("type");
        String input = section.getString("input");
        String output = section.getString("output");
        String deny = section.getString("deny");
        this.requirements.add(new RequirementData(identifier, input, output, deny));
    }

    public String replaceArguments(String[] params) {
        return this.replaceArguments(params, this.message);
    }

    public String replaceArguments(String[] params, String message) {
        if (params.length > 1) {
            String output = message;
            for (Integer argument : this.arguments) {
                int index = argument + 1;
                if (index >= params.length) continue;
                output = output.replaceAll("\\{" + argument + "}", params[index]);
            }

            return output;
        } else {
            return this.message;
        }
    }

    public List<RequirementData> getRequirements() {
        return requirements;
    }
}
