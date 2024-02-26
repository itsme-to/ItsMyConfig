package to.itsme.itsmyconfig.requirement.checker;

import to.itsme.itsmyconfig.requirement.RequirementChecker;

import java.util.Objects;

public class NumberEqualsCheck implements RequirementChecker<Double> {

    private final boolean reverse;

    public NumberEqualsCheck(final boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public boolean check(final Double input, final Double output) {
        return reverse != Objects.equals(input, output);
    }

}
