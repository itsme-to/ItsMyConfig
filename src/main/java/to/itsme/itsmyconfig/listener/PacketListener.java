package to.itsme.itsmyconfig.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.regex.Pattern;

public abstract class PacketListener extends PacketAdapter {

    protected final ItsMyConfig plugin;
    private final Pattern colorSymbolPattern, symbolPrefixPattern;
    private final Pattern linePattern = Pattern.compile("(?<!\\\\)<configline>");
    protected final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

    public PacketListener(
            final ItsMyConfig plugin,
            final PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
        this.colorSymbolPattern = Pattern.compile(Pattern.quote("§"));
        this.symbolPrefixPattern = Pattern.compile(Pattern.quote(plugin.getSymbolPrefix()));
    }

    /**
     * Removes the '§' symbol and replaces it with '&'
     * <br>
     * Also removes the first '$' symbol it meets
     *
     * @param message the provided message
     */
    protected String processMessage(final String message) {
        return linePattern.matcher(
                colorSymbolPattern.matcher(symbolPrefixPattern.matcher(message).replaceFirst("")).replaceAll("&")
        ).replaceAll("<br>");
    }

    /**
     * Checks if the provided message starts with the "$" symbol
     * @param message the checked message
     */
    protected boolean startsWithSymbol(final String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        return Utilities.TAG_PATTERN.matcher(Utilities.colorless(message)).replaceAll("").trim().startsWith(plugin.getSymbolPrefix());
    }

}
