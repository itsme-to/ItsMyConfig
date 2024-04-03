package to.itsme.itsmyconfig.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.progress.ProgressBarBucket;
import to.itsme.itsmyconfig.font.Font;

public final class DynamicPlaceHolder extends PlaceholderExpansion {

    private final ItsMyConfig plugin;
    private final ProgressBarBucket progressBarBucket;
    private final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public DynamicPlaceHolder(
            final ItsMyConfig plugin,
            final ProgressBarBucket progressBarBucket
    ) {
        this.plugin = plugin;
        this.progressBarBucket = progressBarBucket;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "itsmyconfig";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(final Player player, @NotNull String params) {
        params = PlaceholderAPI.setPlaceholders(player, params.replaceAll("\\$\\((.*?)\\)\\$", "%$1%"));

        final String[] strings = params.split("_");
        if (strings.length == 0) {
            return "Illegal Argument";
        }

        if (strings.length >= 3 && strings[0].equalsIgnoreCase("font")) {
            if (strings[1].equalsIgnoreCase("latin")) {
                try {
                    int integer = Integer.parseInt(strings[2]);
                    return integerToRoman(integer);
                } catch (NumberFormatException e) {
                    return "Illegal Number Format";
                }
            } else if (strings[1].equalsIgnoreCase("smallcaps")) {
                String message = strings[2].toLowerCase();
                return Font.SMALL_CAPS.apply(message);
            }

            return "ERROR";
        }

        if (strings.length >= 4 && strings[0].equalsIgnoreCase("progress")) {
            String identifier = strings[1];
            double value;
            double maxValue;
            try {
                value = Double.parseDouble(strings[2]);
                maxValue = Double.parseDouble(strings[3]);
            } catch (NumberFormatException e) {
                return "Illegal Number Format";
            }

            final ProgressBar progressBar = progressBarBucket.getProgressBar(identifier);
            if (progressBar == null) {
                return String.format("Not Found Progress Bar(%s)", identifier);
            }

            return ChatColor.translateAlternateColorCodes('&', progressBar.render(value, maxValue));
        }

        final String placeholder = strings[0];
        if (!plugin.getPlaceholderManager().has(placeholder)) {
            return "Placeholder not found";
        }

        final PlaceholderData data = plugin.getPlaceholderManager().get(placeholder);
        final String[] args = getArgs(strings).split("::");
        return data.asString(player, args);
    }

    private String getArgs(String[] strings) {
        if (strings.length < 2) {
            return "";
        }

        final StringBuilder builder = new StringBuilder(strings[1]);
        for (int i = 2; i < strings.length; i++) {
            builder.append("_").append(strings[i]);
        }
        return builder.toString();
    }

    public String integerToRoman(int num) {
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }

}