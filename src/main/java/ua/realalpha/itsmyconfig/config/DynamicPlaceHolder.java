package ua.realalpha.itsmyconfig.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.realalpha.itsmyconfig.progress.ProgressBar;
import ua.realalpha.itsmyconfig.progress.ProgressBarBucket;

import java.util.HashMap;
import java.util.Map;

public class DynamicPlaceHolder extends PlaceholderExpansion {

    private final Map<String, String> identifierToResult = new HashMap<>();
    private final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    private ProgressBarBucket progressBarBucket;

    public DynamicPlaceHolder(ProgressBarBucket progressBarBucket) {
        this.progressBarBucket = progressBarBucket;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "itsme";
    }

    @Override
    public @NotNull String getAuthor() {
        return "RealAlphaUA";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        params = PlaceholderAPI.setPlaceholders(player, params.replaceAll("\\$\\((.*?)\\)\\$", "%$1%"));
        params = PlaceholderAPI.setBracketPlaceholders(player, params);

        String[] strings = params.split("_");
        if (strings.length == 0) return "Illegal Argument";

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("placeholder")) {
            if (!identifierToResult.containsKey(strings[1])) return "Not Found Custom PlaceHolder";
            return identifierToResult.get(strings[1]);
        }

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("latin")){
            try {
                int integer = Integer.parseInt(strings[1]);
                return integerToRoman(integer);
            }catch (NumberFormatException e){
                return "Illegal Number Format";
            }
        }

        if (strings.length >= 4 && strings[0].equalsIgnoreCase("progress")) {
            String identifier = strings[1];
            double value;
            double maxValue;
            try {
                value = Double.parseDouble(strings[2]);
                maxValue = Double.parseDouble(strings[3]);
            }catch (NumberFormatException e){
                return "Illegal Number Format";
            }
            ProgressBar progressBar = progressBarBucket.getProgressBar(identifier);

            if (progressBar == null){
                return String.format("Not Found Progress Bar(%s)", identifier);
            }

            return ChatColor.translateAlternateColorCodes('&', progressBar.render(value, maxValue));

        }

        return "ERROR";
    }

    public void registerIdentifier(String key, String value){
        this.identifierToResult.put(key, value);
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
