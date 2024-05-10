package to.itsme.itsmyconfig.progress;

/**
 * ProgressBar class represents a progress bar with customizable colors and pattern.
 */
public final class ProgressBar {
    private final String key, pattern, completedColor, progressColor, remainingColor;

    public ProgressBar(
            final String key,
            final String pattern,
            final String completedColor,
            final String progressColor,
            final String remainingColor
    ) {
        this.key = key;
        this.pattern = pattern;
        this.completedColor = completedColor;
        this.progressColor = progressColor;
        this.remainingColor = remainingColor;
    }

    public String getKey() {
        return key;
    }

    public String render(final double value, final double max) {
        int completed = calculateCompleted(value, max);
        return buildProgressBar(completed);
    }

    private int calculateCompleted(final double value, final double max) {
        final double percent = value / max;
        int completed = (int) Math.round(percent * pattern.length());
        return Math.min(completed, pattern.length());
    }

    private String buildProgressBar(int completed) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(completedColor);
        if (completed != 0) {
            stringBuilder.append(pattern, 0, completed);
        }
        if (completed != pattern.length()) {
            stringBuilder.append(progressColor);
            stringBuilder.append(pattern, completed, completed + 1);
            stringBuilder.append(remainingColor);
            stringBuilder.append(pattern, completed + 1, pattern.length());
        }
        return stringBuilder.toString();
    }
}
