package to.itsme.itsmyconfig.requirement;

import java.util.HashMap;
import java.util.Map;

public abstract class Requirement<V> {

    private final Map<String, RequirementChecker<V>[]> syntaxMap = new HashMap<>();

    @SafeVarargs
    protected final void syntax(String syntax, RequirementChecker<V>... transformers) {
        this.syntaxMap.put(syntax, transformers);
    }

    protected boolean isValid(String syntax, V input, V output) {
        RequirementChecker<V>[] checkers = this.syntaxMap.get(syntax);
        if (checkers == null) return true;
        boolean valid = false;
        for (RequirementChecker<V> checker : checkers) {
            valid = checker.check(input, output);
            if (valid) break;
        }

        return valid;
    }

    public boolean matchIdentifier(String identifier) {
        String firstValueOfIdentifier = identifier.split(" ")[0];
        for (String requiredIdentifier : this.identifiers()) {
            if (firstValueOfIdentifier.equals(requiredIdentifier)) return true;
        }

        return false;
    }

    public abstract boolean validate(String identifier, String input, String output);

    public abstract String[] identifiers();

}
