package to.itsme.itsmyconfig.processor;

import net.kyori.adventure.text.Component;

public record PacketContent<C>(PacketProcessor<C> form, String message) {

    public void save(final C container, final Component component) {
        this.form.edit(container, component);
    }

    public boolean isEmpty() {
        return message.isEmpty();
    }

}
