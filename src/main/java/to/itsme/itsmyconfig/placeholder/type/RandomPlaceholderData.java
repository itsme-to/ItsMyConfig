package to.itsme.itsmyconfig.placeholder.type;

import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class RandomPlaceholderData extends PlaceholderData {

    private final Random random = new Random();
    private final HashMap<String, List<Integer>> messages = new HashMap<>();

    public RandomPlaceholderData(final List<String> messages) {
        super(PlaceholderType.RANDOM);
        for (final String message : messages) {
            this.messages.put(message, Utilities.getArguments(message));
        }
    }

    public Map.Entry<String, List<Integer>> getRandomEntry() {
        if (messages.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(messages.size());
        int currentIndex = 0;
        for (Map.Entry<String, List<Integer>> entry : messages.entrySet()) {
            if (currentIndex == randomIndex) {
                return entry;
            }
            currentIndex++;
        }
        return null;
    }

    @Override
    public String getResult(final String[] params) {
        final Map.Entry<String, List<Integer>> entry = this.getRandomEntry();
        if (entry == null) {
            return "";
        }

        return this.replaceArguments(params, entry.getKey(), entry.getValue());
    }

}
