package to.itsme.itsmyconfig.requirement.checker;

import to.itsme.itsmyconfig.requirement.RequirementChecker;

public class ContainsCheck implements RequirementChecker<String> {

    @Override
    public boolean check(final String input, final String output) {
        return input.contains(output);
    }

}
