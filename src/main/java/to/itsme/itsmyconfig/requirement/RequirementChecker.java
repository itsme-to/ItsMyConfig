package to.itsme.itsmyconfig.requirement;

/**
 * The RequirementChecker interface represents a generic requirement checker that can be used to check
 * if a given input and output satisfy a specific requirement.
 *
 * @param <T> the type of the input and output
 */
public interface RequirementChecker<T> {

    boolean check(final T input, final T output);

}
