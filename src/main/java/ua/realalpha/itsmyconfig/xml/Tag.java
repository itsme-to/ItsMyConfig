package ua.realalpha.itsmyconfig.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tag {

    public static String getContent(String tag, String message) {
        String tagStart = "<" + tag;
        String tagEnd = "</" + tag + ">";
        int start = message.indexOf(tagStart);
        int end = message.indexOf(tagEnd);

        if (start == -1 && end == -1) return null;
        if (end == -1) end = message.length() - tagEnd.length();

        return message.substring(start, end + tagEnd.length());
    }

    public static String removeContentInside(String tag, String message) {
        String tagStart = "<" + tag;
        String tagEnd = "</" + tag + ">";
        int start = message.indexOf(tagStart);
        int end = message.indexOf(tagEnd);
        return message.substring(0, start) + message.substring(end + tagEnd.length());
    }


    public static String[] getParameters(String message) {
        int border = message.indexOf('>');
        String token = message.substring(0, border + 1);
        String[] parameters = token.substring(1, token.length() - 1).split(":");
        return Arrays.copyOfRange(parameters, 1, parameters.length);
    }

    public static List<String> getTags(String message) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("<([^/][^>\\s]+)[^>]*>");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String token = matcher.group(1);
            int i = token.indexOf(":");
            if (i != -1) {
                token = token.substring(0, i);
            }
            tokens.add(token);
        }
        return tokens;
    }

    //remove all tags from message
    public static String messageWithoutTag(String tag, String message) {
        return message.substring(getFirstIndex(message), getLastIndex(tag, message));
    }

    private static int getLastIndex(String tag, String message){
        String tagEnd = "</" + tag + ">";
        int end = message.indexOf(tagEnd);
        if (end == -1) end = message.length();
        return end;
    }

    private static int getFirstIndex(String message){
        return message.indexOf(">")+1;
    }


}

