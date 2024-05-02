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


    public static final class Adapter implements JsonSerializer<KeybindedComponent>, JsonDeserializer<KeybindedComponent> {

        @Override
        public JsonElement serialize(
                final KeybindedComponent component,
                final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObject = new JsonObject();

            // add whether it's null or not to make sure it's a keybind component
            jsonObject.addProperty("keybind", component.keybind);

            if (!component.extra.isEmpty()) {
                final JsonArray extraArray = new JsonArray();
                for (final AbstractComponent extraComponent : component.extra) {
                    extraArray.add(context.serialize(extraComponent));
                }
                jsonObject.add("extra", extraArray);
            }

            return jsonObject;
        }

        @Override
        public KeybindedComponent deserialize(
                final JsonElement json,
                final Type type,
                final JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
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
