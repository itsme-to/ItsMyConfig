package to.itsme.itsmyconfig.requirement;

public interface RequirementChecker<T> {

    boolean check(T input, T output);

}
