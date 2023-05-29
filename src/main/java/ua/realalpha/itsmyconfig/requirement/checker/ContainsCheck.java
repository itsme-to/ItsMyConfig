package ua.realalpha.itsmyconfig.requirement.checker;

import ua.realalpha.itsmyconfig.requirement.RequirementChecker;

public class ContainsCheck implements RequirementChecker<String> {


    @Override
    public boolean check(String input, String output) {
        return input.contains(output);
    }
}
