package to.itsme.itsmyconfig.requirement;

/**
 * The RequirementChecker interface represents a generic requirement checker that can be used to check
 * if a given input and output satisfy a specific requirement.
 *
 * @param <T> the type of the input and output
 */
public interface RequirementChecker<T> {

    /**
     * Checks if the given input and output satisfy a specific requirement.
     *
     * @param input  the input value to check against the requirement
     * @param output the output value to check against the requirement
     * @return true if the input and output satisfy the requirement, false otherwise
     */
    boolean check(final T input, final T output);

}
