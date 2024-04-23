package to.itsme.itsmyconfig.parser;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("all")
public class MinecraftComponent {

    private static final Gson GSON = new Gson();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

    private String text;
    private String color;
    private boolean bold;
    private boolean italic;
    private boolean underlined;
    private boolean strikethrough;
    private boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private List<MinecraftComponent> extra;

    /**
     * Parses a json string to a {@link MinecraftComponent}
     *
     * @param json The parsed json String.
     * @return an instance of {@link MinecraftComponent}.
     */
    public static MinecraftComponent parse(@NotNull final String json) {
        return GSON.fromJson(json, MinecraftComponent.class);
    }

    /**
     * Parses a {@link Component} string to a {@link MinecraftComponent}
     *
     * @param component The parsed {@link Component}.
     * @return an instance of {@link MinecraftComponent}.
     */
    public static MinecraftComponent parse(@NotNull final Component component) {
        if (component instanceof TextComponent) {
            return new MinecraftComponent((TextComponent) component);
        }
        return parse(GSON_SERIALIZER.serialize(component));
    }

    /**
     * Empty Constructor
     */
    public MinecraftComponent() {}

    /**
     * {@link TextComponent} convetrer to a Minecraft Component
     */
    private MinecraftComponent(final TextComponent component) {
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
            this.extra = new LinkedList<>();
            for (final Component child : component.children()) {
                if (component instanceof TextComponent) {
                    this.extra.add(new MinecraftComponent((TextComponent) child));
                }
            }
        }
    }

    public String toMiniMessage() {
        final StringBuilder builder = new StringBuilder();
        if (text != null && !text.isEmpty()) {
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

            if (clickEvent != null) {
                builder.append(clickEvent.toMM());
            }

            if (hoverEvent != null) {
                builder.append(hoverEvent.toMM());
            }

            builder.append(text);

            if (hoverEvent != null) {
                builder.append("</hover>");
            }

            if (clickEvent != null) {
                builder.append("</click>");
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
        }

        if  (this.extra != null) {
            for (final MinecraftComponent component : this.extra) {
                builder.append(component.toMiniMessage());
            }
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

}