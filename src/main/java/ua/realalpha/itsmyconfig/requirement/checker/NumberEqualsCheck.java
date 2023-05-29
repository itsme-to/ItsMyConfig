package ua.realalpha.itsmyconfig.requirement.checker;

import ua.realalpha.itsmyconfig.requirement.RequirementChecker;

import java.util.Objects;

public class NumberEqualsCheck implements RequirementChecker<Double> {

    private final boolean reverse;

    public NumberEqualsCheck(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public boolean check(Double input, Double output) {
        return reverse != Objects.equals(input, output);
    }
}
