package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;
import to.itsme.itsmyconfig.requirement.checker.ContainsCheck;
import to.itsme.itsmyconfig.requirement.checker.StringEqualsCheck;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Arrays;

public class StringRequirement extends Requirement<String> {

    public StringRequirement() {
        this.syntax("equals", new StringEqualsCheck());
        this.syntax("contains", new ContainsCheck());
    }

    @Override
    public boolean validate(String identifier, String inputString, String outputString) {
        boolean ignoreCase = identifier.toLowerCase().contains("ignorecase");
        boolean ignoreColor = identifier.toLowerCase().contains("ignorecolor");

        String input = inputString;
        String output = outputString;

        if (ignoreCase) {
            identifier = identifier.replace("ignorecase", "");
            input = inputString.toLowerCase();
            output = outputString.toLowerCase();
        }

        if (ignoreColor) {
            identifier = identifier.replace("ignorecolor", "");
            input = Utilities.colorless(inputString);
            output = Utilities.colorless(outputString);
        }

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
