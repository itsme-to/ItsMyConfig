package to.itsme.itsmyconfig.config.placeholder.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Locale;

public final class ColorPlaceholderData extends PlaceholderData {

    private final boolean legacy;
    private final String color, nameValue, hexValue;
    private String properties = "", propertiesMiniPrefix = "", propertiesMiniSuffix = "";

    public ColorPlaceholderData(
            final String color,
            final ConfigurationSection properties
    ) {
        super(PlaceholderType.STRING);
        this.color = color;

        final NamedTextColor namedTextColor = NamedTextColor.NAMES.value(color);
        if (namedTextColor != null) {
            this.legacy = true;
            this.nameValue = color;
            this.hexValue = namedTextColor.asHexString();
        } else if (Utilities.HEX_PATTERN.matcher(color).matches()) {
            this.legacy = false;
            final TextColor textColor = TextColor.fromHexString(color);
            if (textColor != null) {
                this.hexValue = color;
                this.nameValue = NamedTextColor.nearestTo(textColor).toString();
            } else {
                this.nameValue = color;
                this.hexValue = "#ff0000";
            }
        } else {
            this.legacy = false;
            this.nameValue = color;
            this.hexValue = "#ff0000";
        }

        if (properties != null) {
            if (properties.getBoolean("bold")) {
                this.properties += "&l";
                this.propertiesMiniPrefix += "<bold>";
                this.propertiesMiniSuffix += "</bold>";
            }

            if (properties.getBoolean("italic")) {
                this.properties += "&o";
                this.propertiesMiniPrefix += "<italic>";
                this.propertiesMiniSuffix += "</italic>";
            }

            if (properties.getBoolean("obfuscated")) {
                this.properties += "&k";
                this.propertiesMiniPrefix += "<obfuscated>";
                this.propertiesMiniSuffix += "</obfuscated>";
            }

            if (properties.getBoolean("underlined")) {
                this.properties += "&n";
                this.propertiesMiniPrefix += "<underlined>";
                this.propertiesMiniSuffix += "</underlined>";
            }

            if (properties.getBoolean("strikethrough")) {
                this.properties += "&m";
                this.propertiesMiniPrefix += "<strikethrough>";
                this.propertiesMiniSuffix += "</strikethrough>";
            }
        }

    }

    @Override
    public String getResult(final String[] params) {
        if (params.length == 0) {
            return this.color + this.properties;
        }

        final String firstArg = params[0].toLowerCase(Locale.ROOT);
        switch (firstArg) {
            case "legacy":
                if (legacy) {
                    return '&' + ChatColor.valueOf(this.nameValue).getChar() + this.properties;
                }
                return '&' + this.hexValue + this.properties;
            case "console":
                if (legacy) {
                    return ChatColor.valueOf(this.nameValue) + this.properties.replace("&",  "§");
                }
                final StringBuilder minecraftFormat = new StringBuilder("§x");
                final String hexColor = this.hexValue.substring(1);
                for (int i = 0; i < hexColor.length(); i += 2) {
                    String pair = hexColor.substring(i, Math.min(i + 2, hexColor.length()));
                    minecraftFormat.append("§").append(pair.toUpperCase());
                }
                return minecraftFormat + this.properties.replace("&",  "§");
            case "mini":
                final String prefix = "<" + this.color + ">" + propertiesMiniPrefix;
                if (params.length > 1) {
                    final String suffix = "</" + this.color + ">" + propertiesMiniSuffix;
                    final StringBuilder result = new StringBuilder(prefix);
                    for (int i = 2; i < params.length; i++) {
                        result.append(params[i]);
                        if (i != params.length - 1) {
                            result.append(" ");
                        }
                    }
                    return result.append(suffix).toString();
                }
                return prefix;
        }
        return this.color;
    }

}
