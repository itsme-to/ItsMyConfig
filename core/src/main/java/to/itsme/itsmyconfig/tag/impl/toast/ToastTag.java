package to.itsme.itsmyconfig.tag.impl.toast;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.tag.api.ArgumentsTag;
import to.itsme.itsmyconfig.util.Utilities;

public class ToastTag extends ArgumentsTag {

    private final ToastSender toastSender = new BukkitToastSender();

    @Override
    public String name() {
        return "toast";
    }

    @Override
    public int minArguments() {
        return 3;
    }

    @Override
    public int maxArguments() {
        return 3;
    }

    @Override
    public String process(final Player player, final String[] arguments) {
        String titleRaw = arguments[0];
        String descriptionRaw = arguments[1];
        String iconRaw = arguments[2];

        Component title = Utilities.translate(titleRaw, player);
        Component description = Utilities.translate(descriptionRaw, player);

        Material icon;
        try {
            icon = Material.valueOf(iconRaw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            icon = Material.BOOK; // fallback icon
        }

        toastSender.sendToast(player, title, description, icon);

        return "";
    }
}
