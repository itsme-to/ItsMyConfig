package to.itsme.itsmyconfig.tag.impl;

import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.tag.api.ArgumentsTag;
import to.itsme.itsmyconfig.util.Scheduler;
import to.itsme.itsmyconfig.util.StringUtil;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.concurrent.atomic.AtomicInteger;

public class RepeatTag extends ArgumentsTag {

    @Override
    public String name() {
        return "repeat";
    }

    @Override
    public int minArguments() {
        return 2;
    }

    @Override
    public int maxArguments() {
        return 3;
    }

    @Override
    public String process(
            final Player player,
            final String[] arguments
    ) {
        final String text = arguments[0];
        final int amount = StringUtil.intOrDefault(arguments[1], 1);

        int delayInTicks = 20;

        if (arguments.length > 2) {
            delayInTicks = StringUtil.intOrDefault(arguments[2], 20);
        }

        final AtomicInteger times = new AtomicInteger();
        Scheduler.runTimerAsync(task -> {
            plugin.adventure().player(player).sendMessage(
                    Utilities.translate(text.replaceAll("<repeat_left>", String.valueOf(amount)), player)
            );
            if (times.getAndIncrement() >= amount) task.cancel();
        }, 0L, delayInTicks);

        return "";
    }

}
