package to.itsme.itsmyconfig.requirement.checker;

import to.itsme.itsmyconfig.requirement.RequirementChecker;

public class UpperCheck implements RequirementChecker<Double> {

    @Override
    public boolean check(Double input, Double output) {
        return input > output;
    }

}
