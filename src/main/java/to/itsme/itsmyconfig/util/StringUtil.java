package to.itsme.itsmyconfig.util;

public final class StringUtil {

    public static int intOrDefault(final String text, final int defaultInt) {
        try {
            return Integer.parseInt(text);
        } catch (final Throwable ignored) { return defaultInt; }
    }

}
