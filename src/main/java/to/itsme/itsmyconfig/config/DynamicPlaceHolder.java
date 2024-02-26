package to.itsme.itsmyconfig.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.progress.ProgressBarBucket;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DynamicPlaceHolder extends PlaceholderExpansion {

    private final ItsMyConfig plugin;
    private final Map<String, PlaceholderData> identifierToResult = new HashMap<>();
    private final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private final String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    private final String[] smallCaps = new String[]
            {"ᴀ", "ʙ", "ᴄ", "ᴅ", "ᴇ", "ғ", "ɢ", "ʜ", "ɪ", "ᴊ", "ᴋ", "ʟ", "ᴍ", "ɴ", "ᴏ", "ᴘ", "ǫ", "ʀ", "s", "ᴛ", "ᴜ", "ᴠ", "ᴡ", "x", "ʏ"};

    private final ProgressBarBucket progressBarBucket;

    public DynamicPlaceHolder(ItsMyConfig plugin, ProgressBarBucket progressBarBucket) {
        this.plugin = plugin;
        this.progressBarBucket = progressBarBucket;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getConfig().getString("identifier", "itsmyconfig");
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        params = PlaceholderAPI.setPlaceholders(player, params.replaceAll("\\$\\((.*?)\\)\\$", "%$1%"));

        String[] strings = params.split("_");
        if (strings.length == 0) return "Illegal Argument";

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
                return messageToSmallCaps(message);
            }

            return "ERROR";
        } else if (strings.length >= 4 && strings[0].equalsIgnoreCase("progress")) {
            String identifier = strings[1];
            double value;
            double maxValue;
            try {
                value = Double.parseDouble(strings[2]);
                maxValue = Double.parseDouble(strings[3]);
            } catch (NumberFormatException e) {
                return "Illegal Number Format";
            }
            ProgressBar progressBar = progressBarBucket.getProgressBar(identifier);

            if (progressBar == null) {
                return String.format("Not Found Progress Bar(%s)", identifier);
            }

            return ChatColor.translateAlternateColorCodes('&', progressBar.render(value, maxValue));
        } else {
            if (!identifierToResult.containsKey(strings[0])) {
                return "Not Found Custom PlaceHolder";
            }

            final PlaceholderData data = identifierToResult.get(strings[0]);
            final String result = PlaceholderAPI.setPlaceholders(player, data.replaceArguments(getArgs(strings).split("::")));
            final String deny = this.plugin.getRequirementManager().getDenyMessage(data, player, getArgs(strings).split("::"));
            return ChatColor.translateAlternateColorCodes('&', deny != null ? deny : result);
        }
    }

    private String getArgs(String[] strings) {
        if (strings.length < 2) {
            return "";
        }

        StringBuilder builder = new StringBuilder(strings[1]);

        for (int i = 2; i < strings.length; i++) {
            builder.append("_").append(strings[i]);
        }

        return builder.toString();
    }

    public PlaceholderData registerIdentifier(String key, String value, String type) {
        PlaceholderData data = new PlaceholderData(value, type);
        this.identifierToResult.put(key, data);
        return data;
    }

    public String messageToSmallCaps(String message) {
        byte[] bytes = message.toLowerCase().getBytes(StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        for (byte messageByte : bytes) {
            if (messageByte >= 97 && messageByte <= 122) {
                builder.append(smallCaps[messageByte - 97]);
            } else {
                builder.append((char) messageByte);
            }
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