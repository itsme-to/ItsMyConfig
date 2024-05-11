package to.itsme.itsmyconfig.placeholder.type;

import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.*;

/**
 * The RandomPlaceholderData class is a concrete implementation of the PlaceholderData class
 * that generates random placeholder data based on provided messages.
 */
public final class RandomPlaceholderData extends PlaceholderData {

    /**
     * An instance of the Random class used for generating random numbers.
     */
    private final Random random = new Random();
    /**
     *
     */
    private final List<AbstractMap.SimpleEntry<String, List<Integer>>> messages = new ArrayList<>();

    /**
     * Constructs a RandomPlaceholderData object with the given messages.
     *
     * @param messages the list of messages to be used for generating random placeholder data
     */
    public RandomPlaceholderData(final List<String> messages) {
        super(PlaceholderType.RANDOM);
        for (final String message : messages) {
            this.messages.add(new AbstractMap.SimpleEntry<>(message, Utilities.getArguments(message)));
        }
    }

    /**
     * Generates a random entry from the messages list.
     *
     * @return The random entry as an AbstractMap.SimpleEntry object,
     *         or null if the messages list is empty.
     */
    public AbstractMap.SimpleEntry<String, List<Integer>> getRandomEntry() {
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
    public String getResult(final String[] params) {
        final AbstractMap.SimpleEntry<String, List<Integer>> entry = this.getRandomEntry();
        return (entry == null) ? null : this.replaceArguments(params, entry.getKey(), entry.getValue());
    }

}
