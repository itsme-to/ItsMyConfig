package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;

import java.util.Objects;

public final class NumberRequirement extends Requirement<Double> {

    public NumberRequirement() {
        this.syntax("==", Objects::equals);
        this.syntax(">", (input, output) -> input > output);
        this.syntax("<", (input, output) -> input < output);
        this.syntax(">=", this.syntaxMap.get(">")[0], this.syntaxMap.get("==")[0]);
        this.syntax("<=", this.syntaxMap.get("<")[0], this.syntaxMap.get("==")[0]);
        this.syntax("!=", (input, output) -> !Objects.equals(input, output));
    }

    @Override
    public boolean validate(String identifier, String inputString, String outputString) {
        final Double input = this.transformString(inputString);
        final Double output = this.transformString(outputString);
        if (input == null || output == null) return false;
        return this.isValid(identifier, input, output);
    }

    @Override
    public String[] identifiers() {
        return new String[] {
                "==",
                ">",
                "<",
                ">=",
                "<=",
                "!="
        };
    }

    private Double transformString(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {}

        try {
            return (double) Integer.parseInt(value);
        } catch (NumberFormatException ignored) {}

        try {
            return (double) Float.parseFloat(value);
        } catch (NumberFormatException ignored) {}

        return null;
    }

}
