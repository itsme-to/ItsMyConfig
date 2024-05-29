package to.itsme.itsmyconfig.tag.impl;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.tag.api.ArgumentsTag;
import to.itsme.itsmyconfig.util.Scheduler;
import to.itsme.itsmyconfig.util.Utilities;

public class BossbarTag extends ArgumentsTag {

    @Override
    public String name() {
        return "bossbar";
    }

    @Override
    public int minArguments() {
        return 4;
    }

    @Override
    public int maxArguments() {
        return 5;
    }

    @Override
    public String process(
            final Player player,
            final String[] arguments
    ) {
        final Component component = Utilities.translate(arguments[0], player);
        final double progress = Double.parseDouble(arguments[1]);
        final BossBar.Color color = BossBar.Color.NAMES.value(arguments[2]);
        final BossBar.Overlay overlay = BossBar.Overlay.NAMES.value(arguments[3]);
        final BossBar bar = BossBar.bossBar(
                component,
                (float) Math.min(progress, 1.0F),
                color == null ? BossBar.Color.PINK : color,
                overlay == null ? BossBar.Overlay.PROGRESS : overlay
        );

        long delay = 20;
        if (arguments.length > 4) {
            delay = Integer.parseInt(arguments[4]);
        }

        plugin.adventure().player(player).showBossBar(bar);
        Scheduler.runLaterAsync(() -> plugin.adventure().player(player).hideBossBar(bar), delay);
        return "";
    }

}
