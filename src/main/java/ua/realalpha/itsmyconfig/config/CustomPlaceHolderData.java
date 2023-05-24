package ua.realalpha.itsmyconfig.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPlaceHolderData {

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{([0-9]+)}");

    private final String message;
    private final List<Integer> arguments;

    public CustomPlaceHolderData(String message) {
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

    public String replaceArguments(String[] params) {
        System.out.println(this.arguments);
        if (params.length > 2) {
            String output = this.message;
            for (Integer argument : this.arguments) {
                int index = argument + 2;
                if (index >= params.length) continue;
                output = output.replaceAll("\\{" + argument + "}", params[index]);
            }

            return output;
        } else {
            return this.message;
        }
    }

}
