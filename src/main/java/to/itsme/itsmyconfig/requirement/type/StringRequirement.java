package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Arrays;

/**
 * The StringRequirement class is a final class that extends the Requirement class. It represents a requirement
 * for strings. It provides methods to add syntax rules for string comparison and validation of input and output.
 */
public final class StringRequirement extends Requirement<String> {

    public StringRequirement() {
        this.addSyntaxRule("equals", String::equals);
        this.addSyntaxRule("contains", String::contains);
    }

    @Override
    public boolean validate(String identifier, final String inputString,
                            final String outputString) {
        String[] modifiedValues = handleSpecialIdentifiers(identifier,
                inputString,
                outputString);
        identifier = modifiedValues[0];
        String input = modifiedValues[1];
        String output = modifiedValues[2];

        final boolean reverse = identifier.startsWith("!");
        String[] syntaxArguments = identifier.split(" ");
        syntaxArguments = Arrays.copyOfRange(syntaxArguments, 1,
                syntaxArguments.length);

        return validateStrings(reverse, syntaxArguments, input, output);
    }

    // Handle special identifiers and return the modified values
    private String[] handleSpecialIdentifiers(String identifier,
                                              String inputString,
                                              String outputString) {
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

        return new String[]{identifier, input, output};
    }

    // Validate input and output strings based on syntaxArguments and 'reverse' flag
    private boolean validateStrings(boolean reverse, String[] syntaxArguments,
                                    String input, String output) {

        boolean result = true;

        for (String syntax : syntaxArguments) {
            result = reverse != this.isValid(syntax, input, output);
            if (!result) break;
        }

        return result;
    }

    @Override
    public String[] identifiers() {
        return new String[]{
                "string",
                "!string"
        };
    }

}
