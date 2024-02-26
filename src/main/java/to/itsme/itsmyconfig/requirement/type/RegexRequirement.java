package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;
import to.itsme.itsmyconfig.requirement.checker.RegexCheck;

public class RegexRequirement extends Requirement<String> {

    public RegexRequirement() {
        this.syntax("regex matches", new RegexCheck());
    }

    @Override
    public boolean validate(String identifier, String input, String output) {
        return this.isValid("regex matches", input, output);
    }

    @Override
    public boolean matchIdentifier(String identifier) {
        return identifier.equals("regex matches");
    }

    @Override
    public String[] identifiers() {
        return new String[] {"regex matches"};
    }

}
