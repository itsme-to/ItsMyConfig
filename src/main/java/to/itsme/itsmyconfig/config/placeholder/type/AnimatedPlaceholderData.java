package to.itsme.itsmyconfig.config.placeholder.type;

import org.bukkit.Bukkit;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AnimatedPlaceholderData extends PlaceholderData {

    private final HashMap<String, List<Integer>> messages = new HashMap<>();
    private int index = 0;

    public AnimatedPlaceholderData(
            final List<String> messages,
            final int interval
    ) {
        super(PlaceholderType.ANIMATION);
        for (final String message : messages) {
            this.messages.put(message, Utilities.getArguments(message));
        }

        final int max = this.messages.size();
        Bukkit.getScheduler().runTaskTimerAsynchronously(ItsMyConfig.getInstance(), () -> {
            if (max < index) {
                index++;
            } else {
                index = 0;
            }
        }, interval, interval);
    }

    public Map.Entry<String, List<Integer>> getNextEntry() {
        if (messages.isEmpty()) {
            return null;
        }

        int currentIndex = 0;
        for (Map.Entry<String, List<Integer>> entry : messages.entrySet()) {
            if (currentIndex == index) {
                return entry;
            }
            currentIndex++;
        }
        return null;
    }

    @Override
    public String getResult(final String[] params) {
        final Map.Entry<String, List<Integer>> entry = this.getNextEntry();
        if (entry == null) {
            return "";
        }

        return this.replaceArguments(params, entry.getKey(), entry.getValue());
    }

}
