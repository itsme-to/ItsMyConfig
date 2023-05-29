package ua.realalpha.itsmyconfig.requirement.checker;

import ua.realalpha.itsmyconfig.requirement.RequirementChecker;

public class UpperCheck implements RequirementChecker<Double> {

    @Override
    public boolean check(Double input, Double output) {
        return input > output;
    }
}
