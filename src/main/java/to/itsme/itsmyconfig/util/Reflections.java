package to.itsme.itsmyconfig.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflections {

    private static Object SERIALIZER;
    private static Method DESERIALIZE;

    static {
        try {
            final Class<?> gsonSerializer = Class.forName(
                    // if we use "." directly, the shade plugin will change it as well!
                    "net{}kyori{}adventure{}text{}serializer{}gson{}GsonComponentSerializer".replace("{}", ".")
            );

            SERIALIZER = gsonSerializer.getMethod("gson").invoke(null);
            DESERIALIZE = gsonSerializer.getMethod("deserialize", Object.class);
        } catch (final Throwable ignored) {}
    }

    public static Object fromJsonToComponent(final String json) {
        try {
            return DESERIALIZE.invoke(SERIALIZER, json);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
