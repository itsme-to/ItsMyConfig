package to.itsme.itsmyconfig.component.event;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ClickEvent {

    private String action;
    private String value;

    /**
     * Empty Constructor
     */
    public ClickEvent() {
    }

    /**
     * {@link net.kyori.adventure.text.event.ClickEvent} convetrer to a {@link ClickEvent}
     */
    public ClickEvent(net.kyori.adventure.text.event.ClickEvent event) {
        this.action = event.action().toString();
        this.value = event.value();
    }

    public String toMiniMessage() {
        return "<click:" + this.action + ":\"" + this.value + "\">";
    }

    public static final class Adapter implements JsonSerializer<ClickEvent>, JsonDeserializer<ClickEvent> {

        public JsonElement serialize(
                final ClickEvent event,
                final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", event.action);
            jsonObject.addProperty("value", event.value);
            return jsonObject;
        }

        public ClickEvent deserialize(
                final JsonElement json,
                final Type type,
                final JsonDeserializationContext context
        ) throws JsonParseException {
            final ClickEvent event = new ClickEvent();
            final JsonObject jsonObject = json.getAsJsonObject();
            event.action = jsonObject.has("action") ? jsonObject.get("action").getAsString() : null;
            event.value = jsonObject.has("value") ? jsonObject.get("value").getAsString() : null;
            return event;
        }

    }

}
