package to.itsme.itsmyconfig.requirement.checker;

import to.itsme.itsmyconfig.requirement.RequirementChecker;

import java.util.regex.Pattern;

public class RegexCheck implements RequirementChecker<String> {

    @Override
    public boolean check(final String input, final String output) {
        return Pattern.compile(output).matcher(input).matches();
    }

}
