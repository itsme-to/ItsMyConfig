package to.itsme.itsmyconfig.progress;

import java.util.HashMap;
import java.util.Map;

public final class ProgressBarBucket {

    private final Map<String, ProgressBar> progressBarByKey = new HashMap<>();

    public void registerProgressBar(final ProgressBar progressBar) {
        this.progressBarByKey.put(progressBar.getKey(), progressBar);
    }

    public ProgressBar getProgressBar(final String key) {
        return this.progressBarByKey.get(key);
    }

    public void clearProgressBar() {
        this.progressBarByKey.clear();
    }

}
