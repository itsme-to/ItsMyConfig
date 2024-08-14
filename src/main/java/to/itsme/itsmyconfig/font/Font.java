package to.itsme.itsmyconfig.font;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import org.jetbrains.annotations.NotNull;

public interface Font {

    @TagPattern String getName();
    @NotNull String apply(final @NotNull String text);
    @NotNull Component apply(final @NotNull Component component);

}
