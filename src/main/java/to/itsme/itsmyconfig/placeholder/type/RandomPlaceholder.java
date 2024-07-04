package to.itsme.itsmyconfig.placeholder.type;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

import java.util.*;

/**
 * The RandomPlaceholderData class is a concrete implementation of the PlaceholderData class
 * that generates random placeholder data based on provided messages.
 */
public final class RandomPlaceholder extends Placeholder {

    /**
     * An instance of the Random class used for generating random numbers.
     */
    private final Random random = new Random();
    /**
     *
     */
    private final List<String> messages = new ArrayList<>();

    /**
     * Constructs a RandomPlaceholderData object with the given messages.
     */
    public RandomPlaceholder(final Section section) {
        super(section, PlaceholderType.RANDOM);
        final List<String> messages = section.getStringList("values");
        for (final String message : messages) {
            this.messages.add(message);
            this.registerArguments(message);
        }
    }

    /**
     * Generates a random entry from the messages list.
     *
     * @return The random entry as an AbstractMap.SimpleEntry object,
     *         or null if the messages list is empty.
     */
    public String getRandomEntry() {
        if (messages.isEmpty()) {
            return null;
        }

        return messages.get(random.nextInt(messages.size()));
    }

    /**
     * Retrieves the result of placeholder evaluation.
     *
     * @param params The array of parameters used for the evaluation.
     * @return The result of the evaluation as a string.
     */
    @Override
    public String getResult(final Player player, final String[] params) {
        final String entry = this.getRandomEntry();
        return (entry == null) ? null : this.replaceArguments(params, entry);
    }

}
