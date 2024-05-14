package to.itsme.itsmyconfig.progress;

import java.util.HashMap;
import java.util.Map;

/**
 * ProgressBarBucket class represents a collection of ProgressBar objects.
 * It allows registering, retrieving, and clearing progress bars.
 */
public final class ProgressBarBucket {
    /**
     * progressBarByKey is a private final variable of type Map<String, ProgressBar>.
     * It represents a collection of ProgressBar objects stored as key-value pairs, where the key is of type String and the value is of type ProgressBar.
     * Each ProgressBar object represents a progress bar with customizable colors and pattern.
     * The map is initialized using the HashMap() constructor.
     * <p>
     * Example usage:
     * ProgressBarBucket progressBarBucket = new ProgressBarBucket();
     * ProgressBar progressBar1 = new ProgressBar("key1", "pattern1", "completedColor1", "progressColor1", "remainingColor1");
     * ProgressBar progressBar2 = new ProgressBar("key2", "pattern2", "completedColor2", "progressColor2", "remainingColor2");
     * progressBarBucket.registerProgressBar(progressBar1);
     * progressBarBucket.registerProgressBar(progressBar2);
     * <p>
     * ProgressBar progressBar = progressBarBucket.getProgressBar("key1");
     * String progressBarKey = progressBar.getKey();
     * <p>
     * progressBarBucket.clearAllProgressBars();
     * <p>
     * progressBarByKey.put(progressBar.getKey(), progressBar);
     * progressBarByKey.get("key1");
     */
    private final Map<String, ProgressBar> progressBarByKey = new HashMap<>();

    /**
     * Registers a ProgressBar in the ProgressBarBucket.
     *
     * @param progressBar The ProgressBar to register.
     */
    public void registerProgressBar(final ProgressBar progressBar) {
        this.progressBarByKey.put(progressBar.getKey(), progressBar);
    }

    /**
     * Retrieves the ProgressBar with the specified key.
     *
     * @param key The key of the ProgressBar.
     * @return The ProgressBar object with the specified key.
     */
    public ProgressBar getProgressBar(final String key) {
        return this.progressBarByKey.get(key);
    }

    /**
     * Clears all progress bars in the ProgressBarBucket.
     * This method removes all the progress bars from the ProgressBarBucket.
     * After calling this method, the ProgressBarBucket will be empty.
     * Use this method to clear all progress bars and start fresh.
     */
    public void clearAllProgressBars() {
        this.progressBarByKey.clear();
    }
}
