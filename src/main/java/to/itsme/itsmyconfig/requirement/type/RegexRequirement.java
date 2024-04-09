package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;

import java.util.regex.Pattern;

public final class RegexRequirement extends Requirement<String> {

    public RegexRequirement() {
        this.syntax("regex matches", (input, output) -> Pattern.compile(output).matcher(input).matches());
    }

    @Override
    public boolean validate(
            final String identifier,
            final String input,
            final String output
    ) {
        return this.isValid("regex matches", input, output);
    }

    @Override
    public boolean matchIdentifier(final String identifier) {
        return identifier.equals("regex matches");
    }

    @Override
    public String[] identifiers() {
        return new String[] {"regex matches"};
    }

}
