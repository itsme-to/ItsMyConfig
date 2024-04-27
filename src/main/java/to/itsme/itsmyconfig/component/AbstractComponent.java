package to.itsme.itsmyconfig.component;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import to.itsme.itsmyconfig.component.impl.KeybindedComponent;
import to.itsme.itsmyconfig.component.impl.TextfulComponent;
import to.itsme.itsmyconfig.component.impl.TranslatingComponent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractComponent {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(TextfulComponent.class, new TextfulComponent.MinecraftComponentDeserializer())
            .registerTypeAdapter(KeybindedComponent.class, new KeybindedComponent.KeybindedComponentDeserializer())
            .registerTypeAdapter(TranslatingComponent.class, new TranslatingComponent.TranslatingComponentDeserializer())
            .create();

    private static final JsonParser PARSER = new JsonParser();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

    /**
     * Parses a json string to an {@link AbstractComponent}
     *
     * @param json The parsed json String.
     * @return an instance of {@link AbstractComponent}.
     */
    public static AbstractComponent parse(@NotNull final String json) {
        try {
            return parse(PARSER.parse(json));
        } catch (final Throwable ignored) {
            return new TextfulComponent(json);
        }
    }

    /**
     * Parses a {@link Component} into an {@link AbstractComponent}
     *
     * @param component The parsed {@link Component}.
     * @return an instance of {@link AbstractComponent}.
     */
    public static AbstractComponent parse(@NotNull final Component component) {
        if (component instanceof TextComponent) {
            return new TextfulComponent((TextComponent) component);
        } else if (component instanceof KeybindComponent) {
            return new KeybindedComponent((KeybindComponent) component);
        } else if (component instanceof TranslatableComponent) {
            return new TranslatingComponent((TranslatableComponent) component);
        }
        return parse(GSON_SERIALIZER.serialize(component));
    }

    /**
     * Parses {@link JsonElement} to an {@link AbstractComponent}
     *
     * @param element The parsed {@link JsonElement}.
     * @return an instance of {@link AbstractComponent}.
     */
    public static AbstractComponent parse(@NotNull final JsonElement element) {
        if (element.isJsonObject()) {
            final JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("keybind")) {
                return GSON.fromJson(element, KeybindedComponent.class);
            } else if (jsonObject.has("translate")) {
                return GSON.fromJson(element, TranslatingComponent.class);
            }
            return GSON.fromJson(element, TextfulComponent.class);
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return new TextfulComponent(element.getAsString());
        }
        return null;
    }

    protected final List<AbstractComponent> extra = new LinkedList<>();

    public abstract String toMiniMessage();

}
