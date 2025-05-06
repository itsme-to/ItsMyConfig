package to.itsme.itsmyconfig.processor;

import org.bukkit.plugin.PluginManager;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.processor.packetevents.PEventsListener;
import to.itsme.itsmyconfig.processor.protocollib.PLibListener;

public class ProcessorManager {

    private final ItsMyConfig plugin;
    private final PacketListener listener;

    public ProcessorManager(final ItsMyConfig plugin) {
        this.plugin = plugin;
        this.listener = decideHandler();
    }

    public PacketListener decideHandler() {
        final PluginManager manager = plugin.getServer().getPluginManager();

        // Decide the handler based on plugins at startup
        if (manager.getPlugin("ProtocolLib") != null) {
            return new PLibListener(plugin);
        }

        // Decide the handler based on plugins at startup
        if (manager.getPlugin("PacketEvents") != null) {
            return new PEventsListener();
        }

        return null; // No suitable handler found
    }

    public void load() {
        listener.load();
    }

    public void close() {
        listener.close();
    }

    public PacketListener getListener() {
        return listener;
    }

}
