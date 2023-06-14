package ua.realalpha.itsmyconfig.progress;

public class ProgressBar {

    private String key;
    private String pattern;
    private String completedColor;
    private String progressColor;
    private String remainingColor;

    public ProgressBar(String key, String pattern, String completedColor, String progressColor, String remainingColor) {
        this.key = key;
        this.pattern = pattern;
        this.completedColor = completedColor;
        this.progressColor = progressColor;
        this.remainingColor = remainingColor;
    }

    public String getKey() {
        return key;
    }

    public String render(double value, double maxValue){
        double percent = value / maxValue;
        int completed = (int) Math.round(percent * pattern.length());
        if (completed > pattern.length()) completed = pattern.length();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(completedColor);
        if (completed != 0) {
            stringBuilder.append(pattern, 0, completed);
        }
        if (completed != pattern.length()){
            stringBuilder.append(progressColor);
            stringBuilder.append(pattern, completed, completed+1);
            stringBuilder.append(remainingColor);
            stringBuilder.append(pattern, completed+1, pattern.length());
        }


        return stringBuilder.toString();
    }
}
