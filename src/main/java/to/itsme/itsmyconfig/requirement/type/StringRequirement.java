package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Arrays;

public final class StringRequirement extends Requirement<String> {

    public StringRequirement() {
        this.syntax("equals", String::equals);
        this.syntax("contains", String::contains);
    }

    @Override
    public boolean validate(String identifier, final String inputString, final String outputString) {
        String input = inputString;
        String output = outputString;

        if (identifier.contains("ignorecase")) {
            identifier = identifier.replace("ignorecase", "");
            input = inputString.toLowerCase();
            output = outputString.toLowerCase();
        }

        if (identifier.contains("ignorecolor")) {
            identifier = identifier.replace("ignorecolor", "");
            input = Utilities.colorless(inputString);
            output = Utilities.colorless(outputString);
        }

        final boolean reverse = identifier.startsWith("!");
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
