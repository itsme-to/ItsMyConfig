package ua.realalpha.itsmyconfig.requirement.checker;

import ua.realalpha.itsmyconfig.requirement.RequirementChecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCheck implements RequirementChecker<String> {

    @Override
    public boolean check(String input, String output) {
        Pattern pattern = Pattern.compile(output);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
