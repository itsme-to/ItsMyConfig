package to.itsme.itsmyconfig.processor;

import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;

public record UnpackedPacket(PacketForm form, String message) {

    public void save(final PacketContainer container, final Component component) {
        this.form.edit(container, component);
    }

    public boolean isEmpty() {
        return message.isEmpty();
    }

}
