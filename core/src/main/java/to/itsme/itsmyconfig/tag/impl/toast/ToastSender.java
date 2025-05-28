package to.itsme.itsmyconfig.tag.impl.toast;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Sends custom toast messages to players using the Minecraft advancements system.
 * <p>
 * A toast message appears in the top-right corner of the screen, similar to when
 * advancements are normally triggered. Implementations of this interface may vary
 * between Bukkit APIs, packet-based systems, or Adventure integrations.
 */
public interface ToastSender {

    /**
     * Sends a toast message to the specified player with the given title, description, and icon.
     *
     * @param player      The player who should receive the toast.
     * @param title       The main title of the toast (appears in bold, gold text by default).
     * @param description A short description shown under the title (gray by default).
     * @param icon        The item icon to display on the left side of the toast.
     */
    void sendToast(Player player, Component title, Component description, Material icon);
}
