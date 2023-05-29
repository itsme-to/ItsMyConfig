package ua.realalpha.itsmyconfig.requirement.type;

import ua.realalpha.itsmyconfig.requirement.Requirement;
import ua.realalpha.itsmyconfig.requirement.checker.ContainsCheck;
import ua.realalpha.itsmyconfig.requirement.checker.StringEqualsCheck;

import java.util.Arrays;

public class StringRequirement extends Requirement<String> {

    public StringRequirement() {
        this.syntax("equals", new StringEqualsCheck());
        this.syntax("contains", new ContainsCheck());
    }


    @Override
    public boolean validate(String identifier, String inputString, String outputString) {
        boolean ignoreCase = identifier.toLowerCase().contains("ignorecase");
        if (ignoreCase) {
            identifier = identifier.replace("ignorecase", "");
        }
        String input = ignoreCase ? inputString.toLowerCase() : inputString;
        String output = ignoreCase ? outputString.toLowerCase() : outputString;
        boolean reverse = identifier.startsWith("!");
        String[] syntaxArguments = identifier.split(" ");
        syntaxArguments = Arrays.copyOfRange(syntaxArguments, 1, syntaxArguments.length);
        boolean result = true;
        for (String syntax : syntaxArguments) {
            result = reverse != this.isValid(syntax, input, output);
            if (!result) break;
        }
        return result;
    }

    @Override
    public String[] identifiers() {
        return new String[] {
                "string",
                "!string"
        };
    }
}
