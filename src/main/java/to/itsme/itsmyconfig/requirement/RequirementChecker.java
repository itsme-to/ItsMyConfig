package to.itsme.itsmyconfig.requirement;

public interface RequirementChecker<T> {

    boolean check(final T input, final T output);

}
