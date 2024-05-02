package to.itsme.itsmyconfig.component.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.util.Utilities;

public final class PseudoComponent extends AbstractComponent {

    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();
    private final Component component;

    public PseudoComponent(final Component component) {
        this.component = component;
    }

    public PseudoComponent(final String json) {
        this.component = GSON_SERIALIZER.deserialize(json);
    }

    @Override
    public String toMiniMessage() {
        return Utilities.MM.serialize(component);
    }

}
