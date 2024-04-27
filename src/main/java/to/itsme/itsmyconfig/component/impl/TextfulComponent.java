package to.itsme.itsmyconfig.component.impl;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.util.Utilities;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("all")
public final class TextfulComponent extends AbstractComponent {

    private String text;
    private String color;

    private boolean bold;
    private boolean italic;
    private boolean underlined;
    private boolean strikethrough;
    private boolean obfuscated;

    private String insertion;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private final List<AbstractComponent> extra = new LinkedList<>();

    /**
     * Empty Constructor
     */
    public TextfulComponent() {}

    /**
     * Only String component
     */
    public TextfulComponent(final String text) {
        this.text = text;
    }

    /**
     * {@link TextComponent} convetrer to a {@link TextfulComponent}
     */
    public TextfulComponent(final TextComponent component) {
        this.text = component.content();

        if (component.color() != null) {
            this.color = component.color().asHexString();
        }

        // decorations
        this.bold = component.style().hasDecoration(TextDecoration.BOLD);
        this.italic = component.style().hasDecoration(TextDecoration.ITALIC);
        this.underlined = component.style().hasDecoration(TextDecoration.UNDERLINED);
        this.strikethrough = component.style().hasDecoration(TextDecoration.STRIKETHROUGH);
        this.obfuscated = component.style().hasDecoration(TextDecoration.OBFUSCATED);

        // properties
        this.insertion = component.insertion();

        // events
        if (component.clickEvent() != null) {
            this.clickEvent = new ClickEvent();
            this.clickEvent.setValue(component.clickEvent().value());
            this.clickEvent.setAction(component.clickEvent().action().toString());
        }

        if (component.hoverEvent() != null) {
            this.hoverEvent = new HoverEvent();
            this.hoverEvent.setAction(component.hoverEvent().action().toString());

            switch (hoverEvent.action) {
                case "show_text":
                    this.hoverEvent.setValue(Utilities.MM.serialize((Component) component.hoverEvent().value()));
                    break;
                case "show_achievement":
                    this.hoverEvent.setValue(component.hoverEvent().value().toString());
                    break;
                case "show_item":
                    final net.kyori.adventure.text.event.HoverEvent.ShowItem item = (net.kyori.adventure.text.event.HoverEvent.ShowItem) component.hoverEvent().value();
                    this.hoverEvent.setValue(item.item().value());
                    break;
                case "show_entity":
                    final net.kyori.adventure.text.event.HoverEvent.ShowEntity entity = (net.kyori.adventure.text.event.HoverEvent.ShowEntity) component.hoverEvent().value();
                    this.hoverEvent.setValue(entity.type().value());
                    break;
            }
        }

        // children
        if (!component.children().isEmpty()) {
            for (final Component child : component.children()) {
                this.extra.add(AbstractComponent.parse(child));
            }
        }
    }

    @Override
    public String toMiniMessage() {
        final StringBuilder builder = new StringBuilder();
        if (color != null) {
            builder.append("<").append(color).append(">");
        }

        if (bold) {
            builder.append("<bold>");
        }

        if (italic) {
            builder.append("<italic>");
        }

        if (underlined) {
            builder.append("<underlined>");
        }

        if (strikethrough) {
            builder.append("<strikethrough>");
        }

        if (obfuscated) {
            builder.append("<obfuscated>");
        }

        if (insertion != null) {
            builder.append("<insert:").append(insertion).append(">");
        }

        if (clickEvent != null) {
            builder.append(clickEvent.toMM());
        }

        if (hoverEvent != null) {
            builder.append(hoverEvent.toMM());
        }

        if (text != null && !text.isEmpty()) {
            builder.append(text);
        }

        for (final AbstractComponent component : this.extra) {
            builder.append(component.toMiniMessage());
        }

        if (hoverEvent != null) {
            builder.append("</hover>");
        }

        if (clickEvent != null) {
            builder.append("</click>");
        }

        if (insertion != null) {
            builder.append("</insert>");
        }

        if (obfuscated) {
            builder.append("</obfuscated>");
        }

        if (strikethrough) {
            builder.append("</strikethrough>");
        }

        if (underlined) {
            builder.append("</underlined>");
        }

        if (italic) {
            builder.append("</italic>");
        }

        if (bold) {
            builder.append("</bold>");
        }

        if (color != null) {
            builder.append("</").append(color).append(">");
        }

        return builder.toString();
    }

    public static class ClickEvent {
        private String action;
        private String value;

        public void setAction(final String action) {
            this.action = action;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public String toMM() {
            return "<click:" + action + ":\"" + value + "\">";
        }

    }

    public static class HoverEvent {
        private String action;
        private String value;

        public void setAction(final String action) {
            this.action = action;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public String toMM() {
            return "<hover:" + action + ":\"" + value + "\">";
        }

    }

    public static final class MinecraftComponentDeserializer implements JsonDeserializer<TextfulComponent> {

        @Override
        public final TextfulComponent deserialize(
                final JsonElement json,
                final Type typeOfT,
                final JsonDeserializationContext context
        ) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final TextfulComponent component = new TextfulComponent();
            component.text = jsonObject.has("text") ? jsonObject.get("text").getAsString() : null;
            component.color = jsonObject.has("color") ? jsonObject.get("color").getAsString() : null;
            component.bold = jsonObject.has("bold") && jsonObject.get("bold").getAsBoolean();
            component.italic = jsonObject.has("italic") && jsonObject.get("italic").getAsBoolean();
            component.underlined = jsonObject.has("underlined") && jsonObject.get("underlined").getAsBoolean();
            component.strikethrough = jsonObject.has("strikethrough") && jsonObject.get("strikethrough").getAsBoolean();
            component.obfuscated = jsonObject.has("obfuscated") && jsonObject.get("obfuscated").getAsBoolean();
            component.insertion = jsonObject.has("insertion") ? jsonObject.get("insertion").getAsString() : null;
            component.clickEvent = jsonObject.has("clickEvent") ? context.deserialize(jsonObject.get("clickEvent"), ClickEvent.class) : null;
            component.hoverEvent = jsonObject.has("hoverEvent") ? context.deserialize(jsonObject.get("hoverEvent"), HoverEvent.class) : null;

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