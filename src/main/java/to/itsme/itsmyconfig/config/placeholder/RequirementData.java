package to.itsme.itsmyconfig.config.placeholder;

public final class RequirementData {

    private final String identifier;
    private final String input;
    private final String output;
    private final String deny;

    public RequirementData(String identifier, String input, String output, String deny) {
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.deny = deny;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getDeny() {
        return deny;
    }

}
