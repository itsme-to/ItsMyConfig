package to.itsme.itsmyconfig.component.event;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
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
                this.value = AbstractComponent.parse((Component) event.value());
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
        if (this.value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<hover:").append(this.action).append(":\"");
        if (this.value instanceof String) {
            builder.append(this.value);
        } else if (this.value instanceof AbstractComponent component) {
            builder.append(component.toMiniMessage());
        } else if (this.value instanceof ShowItem item) {
            builder.append(item.toMMArg());
        } else if (this.value instanceof ShowEntity entity) {
            builder.append(entity.toMMArg());
        }
        return builder.append("\">").toString();
    }

    public static final class ShowItem {
        private String id;
        private String tag;
        private int count;

        public ShowItem() {
        }

        public ShowItem(net.kyori.adventure.text.event.HoverEvent.ShowItem value) {
            this.id = value.item().value();
            this.tag = value.nbt().string();
            this.count = value.count();
        }

        public String toMMArg() {
            return this.id + ":" + this.count + ":\"" + this.tag + "\"";
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
            final JsonElement element;
            if (jsonObject.has("value")) {
                element = jsonObject.get("value");
            } else if (jsonObject.has("contents")) {
                element = jsonObject.get("contents");
            } else {
                return event;
            }

            switch (event.action) {
                case "show_achievement":
                    event.value = element.getAsString();
                    break;
                case "show_item":
                    event.value = context.deserialize(element, HoverEvent.ShowItem.class);
                    break;
                case "show_entity":
                    event.value = context.deserialize(element, HoverEvent.ShowEntity.class);
                    break;
                default:
                    if (element.isJsonPrimitive()) {
                        event.value = new TextfulComponent(element.getAsString());
                    } else if (element.isJsonArray()) {
                        event.value = AbstractComponent.parse(element.getAsJsonArray());
                    } else if (element.isJsonObject()) {
                        event.value = context.deserialize(element, TextfulComponent.class);
                    }
                    break;
            }
            return event;
        }
    }

}
