package to.itsme.itsmyconfig.requirement.type;

import to.itsme.itsmyconfig.requirement.Requirement;

import java.util.regex.Pattern;

/**
 * The RegexRequirement class is a concrete implementation of the Requirement class.
 * It represents a requirement that checks if a regular expression matches a given string input.
 *
 * @param <V> the type of the input and output, which is String in this case
 */
public final class RegexRequirement extends Requirement<String> {

    private static final String REGEX_MATCHES_IDENTIFIER = "regex matches";

    public RegexRequirement() {
        this.addSyntaxRule(REGEX_MATCHES_IDENTIFIER, (input, output) -> Pattern.compile(output).matcher(input).matches());
    }

    @Override
    public boolean validate(final String identifier, final String input, final String output) {
        return this.isValid(REGEX_MATCHES_IDENTIFIER, input, output);
    }

    @Override
    public boolean matchIdentifier(final String identifier) {
        return identifier.equals(REGEX_MATCHES_IDENTIFIER);
    }

    @Override
    public String[] identifiers() {
        return new String[]{REGEX_MATCHES_IDENTIFIER};
    }
}
