package to.itsme.itsmyconfig.requirement;

import java.util.Map;
import java.util.HashMap;

/**
 * The Requirement class is an abstract class that represents a requirement. It provides utility methods to add syntax rules and check if an input and output satisfy the requirement
 * .
 *
 * @param <V> the type of the input and output
 */
public abstract class Requirement<V> {
    protected final Map<String, RequirementChecker<V>[]> syntaxRules = new HashMap<>();

    @SafeVarargs
    protected final void addSyntaxRule(final String syntax, final RequirementChecker<V>... requirementCheckers) {
        this.syntaxRules.put(syntax, requirementCheckers);
    }

    protected boolean isValid(final String syntax, final V input, final V output) {
        return checkRequirement(syntax, input, output);
    }

    public boolean matchIdentifier(final String identifier) {
        return patternMatchesIdentifier(identifier);
    }

    public abstract boolean validate(final String identifier, final String input, final String output);

    public abstract String[] identifiers();

    private boolean patternMatchesIdentifier(String identifier) {
        String firstValueOfIdentifier = identifier.split(" ")[0];
        for (String requiredIdentifier : this.identifiers()) {
            if (firstValueOfIdentifier.equals(requiredIdentifier)) return true;
        }
        return false;
    }

    private boolean checkRequirement(String syntax, V input, V output) {
        RequirementChecker<V>[] checkers = this.syntaxRules.get(syntax);
        if (checkers == null) {
            return true;
        }
        for (RequirementChecker<V> checker : checkers) {
            if (checker.check(input, output)) {
                return true;
            }
        }
        return false;
    }
}
