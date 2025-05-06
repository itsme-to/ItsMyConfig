package to.itsme.itsmyconfig.font;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import org.jetbrains.annotations.NotNull;

public final class FontTag implements Modifying {

    private final Font font;

    public FontTag(final Font font) {
        this.font = font;
    }

    @Override
    public Component apply(final @NotNull Component current, final int depth) {
        if (depth == 0) {
            return font.apply(current);
        }
        return Component.empty();
    }

}
