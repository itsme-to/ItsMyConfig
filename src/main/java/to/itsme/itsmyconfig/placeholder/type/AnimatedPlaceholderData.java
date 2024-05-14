package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.Bukkit;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Represents an animated placeholder data object that rotates between different messages at a specified interval.
 * Extends the PlaceholderData class.
 */
public final class AnimatedPlaceholderData extends PlaceholderData {

    private final BlockingQueue<Map.Entry<String, List<Integer>>> queue;

    /**
     * Represents an animated placeholder data object that rotates between different messages at a specified interval.
     * Extends the PlaceholderData class.
     */
    public AnimatedPlaceholderData(
            final List<String> messages,
            final int interval
    ) {
        super(PlaceholderType.ANIMATION);

        queue = new ArrayBlockingQueue<>(messages.size());

        if (messages.isEmpty()) return;

        for (final String message : messages) {
            queue.add(new AbstractMap.SimpleEntry<>(message, Utilities.getArguments(message)));
        }

        if (messages.size() > 1)
            Bukkit.getScheduler().runTaskTimerAsynchronously(ItsMyConfig.getInstance(), this::rotateMessage, interval, interval);
    }

    /**
     * Rotates the current message in the queue by moving the first message to the back of the queue.
     * If the queue is empty, no action is taken.
     */
    private void rotateMessage() {
        Map.Entry<String, List<Integer>> entry = queue.poll();

        if (entry != null) {
            queue.add(entry);
        }
    }

    /**
     * Retrieves the result of the placeholder evaluation as a string.
     *
     * @param args The arguments used for the placeholder evaluation.
     * @return The result of the placeholder evaluation as a string.
     */
    @Override
    public String getResult(final String[] args) {
        Map.Entry<String, List<Integer>> entry = queue.peek();

        if (entry == null) {
            return "";
        }

        return this.replaceArguments(args, entry.getKey(), entry.getValue());
    }
}
