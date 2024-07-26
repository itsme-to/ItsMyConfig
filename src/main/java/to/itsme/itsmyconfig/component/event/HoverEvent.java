package to.itsme.itsmyconfig.component.event;

import com.google.gson.*;
import net.kyori.adventure.text.TextComponent;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.component.impl.TextfulComponent;

import java.lang.reflect.Type;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unused"})
public class HoverEvent {

    private String action;
    private Object value;

  /**
   * Empty Constructor
   */
    public HoverEvent() {
    }

  /**
   * {@link net.kyori.adventure.text.event.HoverEvent} convetrer to a {@link HoverEvent}
   */
    public HoverEvent(net.kyori.adventure.text.event.HoverEvent event) {
        this.action = event.action().toString();
        switch (this.action) {
            case "show_text":
                this.value = new TextfulComponent((TextComponent) event.value());
                break;
            case "show_achievement":
                this.value = new TextfulComponent(event.value().toString());
                break;
            case "show_item":
                this.value = new ShowItem((net.kyori.adventure.text.event.HoverEvent.ShowItem) event.value());
                break;
            case "show_entity":
                this.value = new ShowEntity((net.kyori.adventure.text.event.HoverEvent.ShowEntity) event.value());
                break;
        }
    }

    public String toMiniMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<hover:").append(this.action).append(":\"");
        if (this.value instanceof String) {
            builder.append(this.value);
        } else if (this.value instanceof AbstractComponent) {
            builder.append(((AbstractComponent) this.value).toMiniMessage());
        } else if (this.value instanceof ShowItem) {
            builder.append(((ShowItem) this.value).toMMArg());
        } else if (this.value instanceof ShowEntity) {
            builder.append(((ShowEntity) this.value).toMMArg());
        }
        return builder.append("\">").toString();
    }

    public static final class ShowItem {
        private String item;

        private int count;

        public ShowItem() {
        }

        public ShowItem(net.kyori.adventure.text.event.HoverEvent.ShowItem value) {
            this.item = value.item().value();
            this.count = value.count();
        }

        public String toMMArg() {
            return this.item + ":" + this.count;
        }
    }

    public static final class ShowEntity {

        private String type;
        private UUID id;
        private AbstractComponent name;

        public ShowEntity() {
        }

        public ShowEntity(net.kyori.adventure.text.event.HoverEvent.ShowEntity value) {
            this.type = value.type().value();
            this.id = value.id();
            if (value.name() != null)
                this.name = AbstractComponent.parse(value.name());
        }

        public String toMMArg() {
            return this.type + ":" + this.id + ":\"" + this.name.toMiniMessage() + "\"";
        }
    }

    public static final class Adapter implements JsonSerializer<HoverEvent>, JsonDeserializer<HoverEvent> {

        public JsonElement serialize(
                final HoverEvent event,
                final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", event.action);

            final Object value = event.value;
            if (value != null) {
                if (value instanceof String) {
                    jsonObject.addProperty("value", (String) event.value);
                } else if (value instanceof TextfulComponent) {
                    jsonObject.add("value", ((TextfulComponent) value).toJsonElement());
                } else if (value instanceof HoverEvent.ShowItem || value instanceof HoverEvent.ShowEntity) {
                    jsonObject.add("value", context.serialize(value));
                }
            }
            return jsonObject;
        }

        public HoverEvent deserialize(
                final JsonElement json,
                final Type type,
                final JsonDeserializationContext context
        ) throws JsonParseException {
            final HoverEvent event = new HoverEvent();
            final JsonObject jsonObject = json.getAsJsonObject();
            event.action = jsonObject.get("action").getAsString();
            if (jsonObject.has("value")) {
                final JsonElement element = jsonObject.get("value");
                switch (event.action) {
                    default:
                        if (element.isJsonPrimitive()) {
                            event.value = new TextfulComponent(element.getAsString());
                        } else if (element.isJsonArray()) {
                            event.value = AbstractComponent.parse(element.getAsJsonArray());
                        } else if (element.isJsonObject()) {
                            event.value = context.deserialize(element, TextfulComponent.class);
                        }
                        break;
                    case "show_achievement":
                        event.value = element.getAsString();
                        break;
                    case "show_item":
                        event.value = context.deserialize(element, HoverEvent.ShowItem.class);
                        break;
                    case "show_entity":
                        event.value = context.deserialize(element, HoverEvent.ShowEntity.class);
                        break;
                }
            } else if (jsonObject.has("contents")) {
                final JsonElement element = jsonObject.get("contents");
                if (element.isJsonPrimitive()) {
                    event.value = new TextfulComponent(element.getAsString());
                } else {
                    event.value = context.deserialize(element, TextfulComponent.class);
                }
            }
            return event;
        }
    }

}
