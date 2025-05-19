package to.itsme.itsmyconfig.util;

import net.kyori.adventure.text.Component;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.component.AbstractComponent;

import java.util.function.Function;

@SuppressWarnings("all")
public class IMCSerializer {

    /**
     * A serializer that converts a String to a MiniMessage format.
     */
    public static Function<String, String> STRING_SERIALIZER;
    public static Function<Component, String> COMPONENT_SERIALIZER;

    static {
        UPDATE_SERIALIZERS();
    }

    public static void UPDATE_SERIALIZERS() {
        STRING_SERIALIZER = createStringSerializer(ItsMyConfig.getInstance().getMinimessageSerializer());
        COMPONENT_SERIALIZER = createComponentSerializer(ItsMyConfig.getInstance().getMinimessageSerializer());
    }

    public static String toMiniMessage(final String text) {
        return STRING_SERIALIZER.apply(text);
    }

    public static String toMiniMessage(final Component component) {
        return COMPONENT_SERIALIZER.apply(component);
    }

    private static Function<String, String> createStringSerializer(final String serializer) {
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
