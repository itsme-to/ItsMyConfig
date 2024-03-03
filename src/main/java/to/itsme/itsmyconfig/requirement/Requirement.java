package to.itsme.itsmyconfig.requirement;

import java.util.HashMap;
import java.util.Map;

public abstract class Requirement<V> {

    public final Map<String, RequirementChecker<V>[]> syntaxMap = new HashMap<>();

    @SafeVarargs
    protected final void syntax(final String syntax, final RequirementChecker<V>... transformers) {
        this.syntaxMap.put(syntax, transformers);
    }

    protected boolean isValid(final String syntax, final V input, final V output) {
        final RequirementChecker<V>[] checkers = this.syntaxMap.get(syntax);
        if (checkers == null) {
            return true;
        }

        for (final RequirementChecker<V> checker : checkers) {
            if (checker.check(input, output)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchIdentifier(final String identifier) {
        final String firstValueOfIdentifier = identifier.split(" ")[0];
        for (final String requiredIdentifier : this.identifiers()) {
            if (firstValueOfIdentifier.equals(requiredIdentifier)) return true;
        }
        return false;
    }

    public abstract boolean validate(final String identifier, final String input, final String output);

    public abstract String[] identifiers();

}
