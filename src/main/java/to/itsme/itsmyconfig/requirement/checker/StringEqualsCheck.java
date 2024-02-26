package to.itsme.itsmyconfig.requirement.checker;

import to.itsme.itsmyconfig.requirement.RequirementChecker;

public class StringEqualsCheck implements RequirementChecker<String> {

    @Override
    public boolean check(String input, String output) {
        return input.equals(output);
    }

}
