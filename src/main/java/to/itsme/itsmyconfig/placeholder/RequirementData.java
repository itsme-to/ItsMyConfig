package to.itsme.itsmyconfig.placeholder;

/**
 * The RequirementData class represents the data for a requirement.
 * Each RequirementData object contains an identifier, input, output, and deny message.
 */
public final class RequirementData {

    /**
     * The identifier represents the unique identifier of a requirement.
     *
     * This identifier is used to uniquely identify a requirement in the system.
     *
     * Example Usage:
     * <pre>
     *     RequirementData requirement = new RequirementData("R001", "Input", "Output", "Deny Message");
     *     String identifier = requirement.getIdentifier();
     * </pre>
     */
    private final String identifier;
    /**
     * The input variable represents the input data for a requirement.
     * It is a private final String in the RequirementData class.
     * Each RequirementData object contains an identifier, input, output, and deny message.
     */
    private final String input;
    /**
     * The output variable represents the output of a RequirementData object.
     * It is a string value that contains the output data.
     *
     * This variable is a private final field and is accessible only within the containing class.
     * It is initialized through the constructor of the RequirementData class.
     *
     * Example usage:
     * RequirementData requirement = new RequirementData("REQ1", "input", "output", "deny");
     * System.out.println(requirement.getOutput()); // prints "output"
     */
    private final String output;
    /**
     * The deny message for a requirement.
     */
    private final String deny;

    /**
     * The RequirementData class represents the data for a requirement.
     * Each RequirementData object contains an identifier, input, output, and deny message.
     */
    public RequirementData(String identifier, String input, String output, String deny) {
        this.identifier = identifier;
        this.input = input;
        this.output = output;
        this.deny = deny;
    }

    /**
     * Retrieves the identifier of the RequirementData object.
     *
     * @return The identifier of the RequirementData object as a string.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the input value of a RequirementData object.
     *
     * @return The input value of the RequirementData object.
     */
    public String getInput() {
        return input;
    }

    /**
     * Retrieves the output data of a requirement.
     *
     * @return The output data of the requirement.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Returns the deny message for a RequirementData object.
     *
     * @return The deny message.
     */
    public String getDeny() {
        return deny;
    }

}
