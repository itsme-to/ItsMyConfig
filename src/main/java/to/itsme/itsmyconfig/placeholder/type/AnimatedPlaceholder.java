package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Scheduler;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Represents an animated placeholder data object that rotates between different messages at a specified interval.
 * Extends the PlaceholderData class.
 */
public final class AnimatedPlaceholder extends Placeholder {

    private final BlockingQueue<String> queue;

    /**
     * Represents an animated placeholder data object that rotates between different messages at a specified interval.
     * Extends the PlaceholderData class.
     */
    public AnimatedPlaceholder(
            final List<String> messages,
            final int interval
    ) {
        super(PlaceholderType.ANIMATION);

        this.queue = new ArrayBlockingQueue<>(messages.size());

        if (messages.isEmpty()) return;

        for (final String message : messages) {
            this.queue.add(message);
            this.registerArguments(message);
        }

        if (messages.size() > 1) {
            Scheduler.runTimerAsync(this::rotateMessage, interval, interval);
        }
    }

    /**
     * Rotates the current message in the queue by moving the first message to the back of the queue.
     * If the queue is empty, no action is taken.
     */
    private void rotateMessage() {
        final String entry = queue.poll();

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
    public String getResult(final Player player, final String[] args) {
        final String entry = queue.peek();

        if (entry == null) {
            return "";
        }

        return this.replaceArguments(args, entry);
    }
}
