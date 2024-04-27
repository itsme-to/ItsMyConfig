package to.itsme.itsmyconfig.component.impl;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import to.itsme.itsmyconfig.component.AbstractComponent;

import java.lang.reflect.Type;

public final class KeybindedComponent extends AbstractComponent {

    private String keybind;

    /**
     * Empty Constructor
     */
    public KeybindedComponent() {}

    /**
     * {@link KeybindComponent} convetrer to a {@link KeybindedComponent}
     */
    public KeybindedComponent(final KeybindComponent component) {
        this.keybind = component.keybind();
        if (!component.children().isEmpty()) {
            for (final Component child : component.children()) {
                extra.add(AbstractComponent.parse(child));
            }
        }
    }

    @Override
    public String toMiniMessage() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<key:").append(keybind).append(">");
        for (final AbstractComponent extra : this.extra) {
            builder.append(extra.toMiniMessage());
        }
        return builder.toString();
    }


    public static final class KeybindedComponentDeserializer implements JsonDeserializer<KeybindedComponent> {

        @Override
        public KeybindedComponent deserialize(
                final JsonElement jsonElement,
                final Type type,
                final JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final KeybindedComponent component = new KeybindedComponent();
            component.keybind = jsonObject.has("keybind") ? jsonObject.get("keybind").getAsString() : null;

            if (jsonObject.has("extra")) {
                final JsonArray extraArray = jsonObject.getAsJsonArray("extra");
                for (final JsonElement element : extraArray) {
                    final AbstractComponent extraComponent = AbstractComponent.parse(element);
                    if (extraComponent != null) {
                        component.extra.add(extraComponent);
                    }
                }
            }

            return component;
        }

    }

}
