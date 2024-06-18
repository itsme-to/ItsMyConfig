package to.itsme.itsmyconfig.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.font.Font;

/**
 * DynamicPlaceHolder class is a PlaceholderExpansion that handles dynamic placeholders for the ItsMyConfig plugin.
 * It provides methods for handling various types of placeholders, such as fonts, progress bars, and custom placeholders.
 * This class extends the PlaceholderExpansion class.
 */
public final class PAPIHook extends PlaceholderExpansion {

    /**
     * This variable is an instance of the ItsMyConfig class.
     */
    private final ItsMyConfig plugin;
    /**
     * An array of integer values used for converting numbers to Roman numerals.
     */
    private final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    /**
     * Class: DynamicPlaceHolder
     * Variable: romanLiterals
     * <p>
     * Description:
     * The `romanLiterals` variable is an array of strings representing the Roman numerals.
     * It contains the following literals: ["M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"].
     * These literals are used in the `integerToRoman` method to convert an integer to its Roman numeral representation.
     */
    private final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    /**
     * ILLEGAL_NUMBER_FORMAT_MSG represents the error message when an illegal number format is encountered.
     */
    public static final String ILLEGAL_NUMBER_FORMAT_MSG = "Illegal Number Format";
    /**
     * ILLEGAL_ARGUMENT_MSG represents a string constant that indicates an illegal argument has been provided.
     * This constant is used in various methods in the DynamicPlaceHolder class.
     */
    public static final String ILLEGAL_ARGUMENT_MSG = "Illegal Argument";
    /**
     * PLACEHOLDER_NOT_FOUND_MSG is a constant variable that represents the message displayed when a placeholder is not found.
     */
    public static final String PLACEHOLDER_NOT_FOUND_MSG = "Placeholder not found";

    /**
     * DynamicPlaceHolder is a class that represents a dynamic placeholder for a placeholder expansion.
     * It handles different types of placeholders and provides methods to handle font, progress, and custom placeholders.
     */
    public PAPIHook(
            final ItsMyConfig plugin
    ) {
        this.plugin = plugin;
    }

    /**
     * Returns the identifier for this object.
     *
     * @return the identifier for this object
     */
    @Override
    public @NotNull String getIdentifier() {
        return "itsmyconfig";
    }

    /**
     * Retrieves the author(s) of the plugin.
     *
     * @return The author(s) of the plugin as a string. If there are multiple authors,
     *         they are joined by commas.
     */
    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.plugin.getDescription().getAuthors());
    }

    /**
     * Retrieves the version of the plugin.
     *
     * @return The version of the plugin.
     */
    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    /**
     * This method is used to persist data.
     *
     * @return true if the data is successfully persisted, false otherwise.
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * This method handles placeholder requests for the DynamicPlaceHolder expansion.
     * It replaces placeholders in the given params string with actual values and returns the result.
     *
     * @param player The player for whom the placeholder is being requested.
     * @param params The placeholder parameters string.
     * @return The result of the placeholder request.
     */
    @Override
    public @Nullable String onPlaceholderRequest(final Player player, @NotNull String params) {
        params = PlaceholderAPI.setPlaceholders(player, params.replaceAll("\\$\\((.*?)\\)\\$", "%$1%"));
        params = PlaceholderAPI.setBracketPlaceholders(player, params);

        final String[] splitParams = params.split("_");
        if (splitParams.length == 0) {
            return ILLEGAL_ARGUMENT_MSG;
        }

        final String firstParam = splitParams[0].toLowerCase();
        if ("font".equals(firstParam) && splitParams.length >= 3) {
            return handleFont(splitParams);
        } else if ("progress".equals(firstParam) && splitParams.length >= 4) {
            return handleProgress(splitParams);
        }
        return handlePlaceholder(splitParams, player);
    }

    /**
     * Handles font-related operations based on the given parameters.
     *
     * @param splitParams The array of parameters, where the font type is at index 1 and additional parameters are at subsequent indices.
     * @return The processed font or an error message if the font type is unknown or if an error occurs during font processing.
     */
    private String handleFont(String[] splitParams) {
        String fontType = splitParams[1].toLowerCase();
        if ("latin".equals(fontType)) {
            try {
                int integer = Integer.parseInt(splitParams[2]);
                return integerToRoman(integer);
            } catch (NumberFormatException e) {
                return ILLEGAL_NUMBER_FORMAT_MSG;
            }
        } else if ("smallcaps".equals(fontType)) {
            String message = splitParams[2].toLowerCase();
            return Font.SMALL_CAPS.apply(message);
        }
        return "ERROR";
    }

    /**
     * Handles progress bar rendering and retrieval.
     *
     * @param splitParams The array of parameters containing the identifier, value, and maxValue of the progress bar.
     * @return The rendered progress bar as a string, or an error message if the progress bar is not found or the parameters are invalid.
     */
    private String handleProgress(final String[] splitParams) {
        final String identifier = splitParams[1];
        try {
            double value = Double.parseDouble(splitParams[2]);
            double maxValue = Double.parseDouble(splitParams[3]);
            final ProgressBar progressBar = plugin.getProgressBarBucket().getProgressBar(identifier);
            if (progressBar == null) {
                return String.format("Not Found Progress Bar(%s)", identifier);
            }
            return ChatColor.translateAlternateColorCodes('&', progressBar.render(value, maxValue));
        } catch (NumberFormatException e) {
            return ILLEGAL_NUMBER_FORMAT_MSG;
        }
    }

    /**
     * Handles the placeholder based on the params and player.
     *
     * @param params The array of split parameters.
     * @param player      The player object.
     * @return The formatted string.
     */
    private String handlePlaceholder(
            final String[] params,
            final Player player
    ) {
        final String placeholder = params[0];
        if (!plugin.getPlaceholderManager().has(placeholder)) {
            return PLACEHOLDER_NOT_FOUND_MSG;
        }

        final String[] copy = new String[params.length - 1];
        System.arraycopy(params, 1, copy, 0, params.length - 1);
        return plugin.getPlaceholderManager().get(placeholder).asString(player, copy);
    }

    /**
     * Converts an integer to a Roman numeral representation.
     *
     * @param num The integer to convert.
     * @return The Roman numeral representation of the given integer.
     */
    public String integerToRoman(int num) {
        final StringBuilder roman = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }

}