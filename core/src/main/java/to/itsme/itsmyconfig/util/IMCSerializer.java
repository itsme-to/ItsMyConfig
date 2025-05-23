package to.itsme.itsmyconfig.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.component.AbstractComponent;

import java.util.function.Function;

@SuppressWarnings("all")
public class IMCSerializer {

    private static final boolean HAS_SUBSTITUTE;

    static {
        boolean hasMethod;
        try {
            Emitable.class.getDeclaredMethod("substitute");
            hasMethod = true;
        } catch (NoSuchMethodException e) {
            hasMethod = false;
        }
        HAS_SUBSTITUTE = hasMethod;
        UPDATE_SERIALIZERS();
    }
    
    /**
     * A serializer that converts a JSON String to MiniMessage format.
     */
    public static Function<String, String> JSON_SERIALIZER;

    /**
     * A serializer that converts a Component to MiniMessage format.
     */
    public static Function<Component, String> COMPONENT_SERIALIZER;

    public static void UPDATE_SERIALIZERS() {
        final String serializer = HAS_SUBSTITUTE ? "MM_COPY" : "JSON_SERIALIZER";
        JSON_SERIALIZER = createJsonSerializer(serializer);
        COMPONENT_SERIALIZER = createComponentSerializer(serializer);
    }

    public static String toMiniMessage(final String json) {
        return JSON_SERIALIZER.apply(json);
    }

    public static String toMiniMessage(final Component component) {
        return COMPONENT_SERIALIZER.apply(component);
    }

    private static Function<String, String> createJsonSerializer(final String serializer) {
        return switch (String.valueOf(serializer).toUpperCase()) {
            case "MM_COPY" -> text -> {
                if (text == null || text.isEmpty()) {
                    return "";
                }

                return toMiniMessage(
                        Utilities.GSON_SERIALIZER.deserialize(text)
                );
            };
            default -> text -> AbstractComponent.parse(text).toMiniMessage();
        };
    }

    private static Function<Component, String> createComponentSerializer(final String serializer) {
        return switch (String.valueOf(serializer).toUpperCase()) {
            case "MM_COPY" -> component -> MMSerializer.serialize(component, null, false);
            default -> component -> AbstractComponent.parse(component).toMiniMessage();
        };
    }

}
