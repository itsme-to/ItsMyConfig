package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;

import java.util.Objects;

public final class NumberRequirement extends Requirement<Double> {

    private static final String EQUAL_IDENTIFIER = "==";
    private static final String GREATER_IDENTIFIER = ">";
    private static final String LESSER_IDENTIFIER = "<";
    private static final String GREATER_OR_EQUAL_IDENTIFIER = ">=";
    private static final String LESSER_OR_EQUAL_IDENTIFIER = "<=";
    private static final String NOT_EQUAL_IDENTIFIER = "!=";

    private final String[] IDENTIFIERS = new String[]{
            EQUAL_IDENTIFIER,
            GREATER_IDENTIFIER,
            LESSER_IDENTIFIER,
            GREATER_OR_EQUAL_IDENTIFIER,
            LESSER_OR_EQUAL_IDENTIFIER,
            NOT_EQUAL_IDENTIFIER};

    public NumberRequirement() {
        this.addSyntaxRule(EQUAL_IDENTIFIER, Objects::equals);
        this.addSyntaxRule(GREATER_IDENTIFIER, (input, output) -> input > output);
        this.addSyntaxRule(LESSER_IDENTIFIER, (input, output) -> input < output);
        this.addSyntaxRule(GREATER_OR_EQUAL_IDENTIFIER, this.syntaxRules.get(GREATER_IDENTIFIER)[0], this.syntaxRules.get(EQUAL_IDENTIFIER)[0]);
        this.addSyntaxRule(LESSER_OR_EQUAL_IDENTIFIER, this.syntaxRules.get(LESSER_IDENTIFIER)[0], this.syntaxRules.get(EQUAL_IDENTIFIER)[0]);
        this.addSyntaxRule(NOT_EQUAL_IDENTIFIER, (input, output) -> !Objects.equals(input, output));
    }

    @Override
    public boolean validate(String identifier, String inputString, String outputString) {
        final Double input = this.transformString(inputString);
        final Double output = this.transformString(outputString);
        return areBothValuesNotNull(input, output) && this.isValid(identifier, input, output);
    }

    @Override
    public String[] identifiers() {
        return IDENTIFIERS;
    }

    private Double transformString(String value) {
        Double convertedValue = 0.0;
        try {
            convertedValue = Double.parseDouble(value);
        } catch (NumberFormatException ignored) { }
        if (convertedValue.equals(0.0)) {
            try {
                convertedValue = (double) Integer.parseInt(value);
            } catch (NumberFormatException ignored) { }
        }
        if (convertedValue.equals(0.0)) {
            try {
                convertedValue = (double) Float.parseFloat(value);
            } catch (NumberFormatException ignored) { }
        }
        return convertedValue;
    }

    private boolean areBothValuesNotNull(Double input, Double output) {
        return input != null && output != null;
    }
}
