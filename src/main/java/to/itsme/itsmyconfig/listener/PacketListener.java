package to.itsme.itsmyconfig.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import to.itsme.itsmyconfig.ItsMyConfig;

public abstract class PacketListener extends PacketAdapter {

    protected final ItsMyConfig plugin;

    public PacketListener(
            final ItsMyConfig plugin,
            final PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
    }

}
