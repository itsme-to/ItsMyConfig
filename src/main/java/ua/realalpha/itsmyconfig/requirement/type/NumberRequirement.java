package ua.realalpha.itsmyconfig.requirement.type;

import ua.realalpha.itsmyconfig.requirement.Requirement;
import ua.realalpha.itsmyconfig.requirement.checker.LowerCheck;
import ua.realalpha.itsmyconfig.requirement.checker.NumberEqualsCheck;
import ua.realalpha.itsmyconfig.requirement.checker.UpperCheck;

public class NumberRequirement extends Requirement<Double> {

    public NumberRequirement() {
        this.syntax("==", new NumberEqualsCheck(false));
        this.syntax(">", new UpperCheck());
        this.syntax("<", new LowerCheck());
        this.syntax(">=", new UpperCheck(), new NumberEqualsCheck(false));
        this.syntax("<=", new LowerCheck(), new NumberEqualsCheck(false));
        this.syntax("!=", new NumberEqualsCheck(true));
    }

    @Override
    public boolean validate(String identifier, String inputString, String outputString) {
        Double input = this.transformString(inputString);
        Double output = this.transformString(outputString);
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
