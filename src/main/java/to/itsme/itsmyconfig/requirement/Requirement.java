package to.itsme.itsmyconfig.requirement;

import java.util.HashMap;
import java.util.Map;

public abstract class Requirement<V> {

    private final Map<String, RequirementChecker<V>[]> syntaxMap = new HashMap<>();

    @SafeVarargs
    protected final void syntax(final String syntax, final RequirementChecker<V>... transformers) {
        this.syntaxMap.put(syntax, transformers);
    }

    protected boolean isValid(final String syntax, final V input, final V output) {
        RequirementChecker<V>[] checkers = this.syntaxMap.get(syntax);
        if (checkers == null) return true;
        boolean valid = false;
        for (RequirementChecker<V> checker : checkers) {
            valid = checker.check(input, output);
            if (valid) break;
        }

        return valid;
    }

    public boolean matchIdentifier(final String identifier) {
        final String firstValueOfIdentifier = identifier.split(" ")[0];
        for (String requiredIdentifier : this.identifiers()) {
            if (firstValueOfIdentifier.equals(requiredIdentifier)) return true;
        }

        return false;
    }

    public abstract boolean validate(final String identifier, final String input, final String output);

    public abstract String[] identifiers();

}
