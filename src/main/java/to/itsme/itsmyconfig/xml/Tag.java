package to.itsme.itsmyconfig.xml;

import java.util.regex.Pattern;

public class Tag {

    private static final Pattern TAG_PATTERN = Pattern.compile("<([^/][^>\\s]+)[^>]*>");

    public static boolean hasTagPresent(String message) {
        return TAG_PATTERN.matcher(message).find();
    }

}

