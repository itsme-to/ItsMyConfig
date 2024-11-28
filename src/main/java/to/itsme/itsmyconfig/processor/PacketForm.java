package to.itsme.itsmyconfig.processor;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.util.Utilities;
import to.itsme.itsmyconfig.util.Versions;

public enum PacketForm {

    SERVER_ADVENTURE {

        @Override
        public void edit(PacketContainer container, Component component) {
            container.getModifier().withType(Component.class).write(0, component);
        }

        @Override
        public UnpackedPacket unpack(PacketContainer container) {
            if (!Versions.IS_PAPER || Versions.MINOR < 16) {
                return null;
            }

            final StructureModifier<Component> modifier = container.getModifier().withType(Component.class);
            if (modifier.size() != 1) {
                return null;
            }

            final Component component = modifier.readSafely(0);
            if (component == null) {
                return null;
            }

            return this.of(AbstractComponent.parse(component).toMiniMessage());
        }
    },

    WRAPPED_COMPONENT {

        @Override
        public void edit(PacketContainer container, Component component) {
            container.getChatComponents().write(0, WrappedChatComponent.fromJson(
                    Utilities.GSON_SERIALIZER.serialize(component)
            ));
        }

        @Override
        public UnpackedPacket unpack(PacketContainer container) {
            final WrappedChatComponent wrappedComponent = container.getChatComponents().readSafely(0);
            if (wrappedComponent == null) {
                return null;
            }

            final String found = wrappedComponent.getJson();
            if (found.isEmpty()) {
                return null;
            }

            try {
                return this.of(AbstractComponent.parse(found).toMiniMessage());
            } catch (final Exception e) {
                Utilities.debug(() -> "An error happened while de/serializing " + found + ": ", e);
            }
            return null;
        }

    },

    @SuppressWarnings("deprecation") BUNGEE_COMPONENT {

        @Override
        public void edit(PacketContainer container, Component component) {
            container.getModifier().withType(TextComponent.class).write(0, new TextComponent(
                    Utilities.BUNGEE_SERIALIZER.serialize(component)
            ));
        }

        @Override
        public UnpackedPacket unpack(PacketContainer container) {
            final StructureModifier<TextComponent> textComponentModifier = container.getModifier().withType(TextComponent.class);
            if (textComponentModifier.size() == 1) {
                return this.of(processBaseComponents(textComponentModifier.readSafely(0)));
            }
            return null;
        }

        private String processBaseComponents(final BaseComponent... components) {
            return AbstractComponent.parse(Utilities.BUNGEE_SERIALIZER.deserialize(components)).toMiniMessage();
        }

    },

    JSON {

        @Override
        public void edit(PacketContainer container, Component component) {
            container.getStrings().write(0, Utilities.GSON_SERIALIZER.serialize(component));
        }

        @Override
        public UnpackedPacket unpack(final PacketContainer container) {
            final String rawMessage = container.getStrings().readSafely(0);
            if (rawMessage == null) {
                return null;
            }
            return this.of(AbstractComponent.parse(rawMessage).toMiniMessage());
        }

    };

    public abstract void edit(final PacketContainer container, final Component component);
    public abstract UnpackedPacket unpack(final PacketContainer container);

    UnpackedPacket of(final String message) {
        return new UnpackedPacket(this, message);
    }

}
