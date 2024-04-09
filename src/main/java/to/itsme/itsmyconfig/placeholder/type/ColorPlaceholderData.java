package to.itsme.itsmyconfig.placeholder.type;

import net.kyori.adventure.text.format.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.Locale;

public final class ColorPlaceholderData extends PlaceholderData {

    private final Style style;
    private final ChatColor legacyColor;
    private final boolean legacy, invalid;
    private final String value, nameValue, hexValue;
    private String properties = "", propertiesMiniPrefix = "", propertiesMiniSuffix = "";

    public ColorPlaceholderData(
            final ConfigurationSection properties
    ) {
        super(PlaceholderType.COLOR);
        this.value = properties.getString("value", "").toLowerCase();

        final NamedTextColor namedTextColor = NamedTextColor.NAMES.value(this.value);
        if (namedTextColor != null) {
            this.legacy = true;
            this.invalid = false;
            this.nameValue = this.value;
            this.hexValue = namedTextColor.asHexString();
        } else if (Utilities.HEX_PATTERN.matcher(this.value).matches()) {
            this.legacy = false;
            this.invalid = false;
            final TextColor textColor = TextColor.fromHexString(this.value);
            if (textColor != null) {
                this.hexValue = this.value;
                this.nameValue = NamedTextColor.nearestTo(textColor).toString();
            } else {
                this.nameValue = this.value;
                this.hexValue = "#ff0000";
            }
        } else {
            this.legacy = false;
            this.invalid = true;
            this.nameValue = "white";
            this.hexValue = "#ff0000";
        }

        this.legacyColor = ChatColor.valueOf(this.nameValue.toUpperCase());
        final Style.Builder builder = Style.style().color(TextColor.fromHexString(hexValue));
        if (properties.getBoolean("bold")) {
            this.properties += "&l";
            this.propertiesMiniPrefix += "<bold>";
            this.propertiesMiniSuffix += "</bold>";
            builder.decorate(TextDecoration.BOLD);
        }

        if (properties.getBoolean("italic")) {
            this.properties += "&o";
            this.propertiesMiniPrefix += "<italic>";
            this.propertiesMiniSuffix += "</italic>";
            builder.decorate(TextDecoration.ITALIC);
        }

        if (properties.getBoolean("obfuscated")) {
            this.properties += "&k";
            this.propertiesMiniPrefix += "<obfuscated>";
            this.propertiesMiniSuffix += "</obfuscated>";
            builder.decorate(TextDecoration.OBFUSCATED);
        }

        if (properties.getBoolean("underlined")) {
            this.properties += "&n";
            this.propertiesMiniPrefix += "<underlined>";
            this.propertiesMiniSuffix += "</underlined>";
            builder.decorate(TextDecoration.UNDERLINED);
        }

        if (properties.getBoolean("strikethrough")) {
            this.properties += "&m";
            this.propertiesMiniPrefix += "<strikethrough>";
            this.propertiesMiniSuffix += "</strikethrough>";
            builder.decorate(TextDecoration.STRIKETHROUGH);
        }

        this.style = builder.build();
    }

    @Override
    public String getResult(final String[] params) {
        if (this.invalid) {
            return "";
        }

        if (params.length == 0) {
            return this.value + this.properties;
        }

        final String firstArg = params[0].toLowerCase(Locale.ROOT);
        switch (firstArg) {
            case "closestname":
                return this.nameValue;
            case "legacy":
                return (legacy ? legacyColor.toString() + this.properties : '&' + this.hexValue + this.properties).replace("§",  "&");
            case "console":
                if (legacy) {
                    return legacyColor + this.properties.replaceAll("&",  "§");
                }
                final String hexColor = this.hexValue.substring(1);
                final StringBuilder minecraftFormat = new StringBuilder("§x");
                for (int i = 0; i < hexColor.length(); i++) {
                    minecraftFormat.append("§").append(hexColor.charAt(i));
                }
                return minecraftFormat + this.properties.replaceAll("&",  "§");
            case "mini":
                final String prefix = "<" + this.value + ">" + propertiesMiniPrefix;
                if (params.length > 1) {
                    final String suffix = "</" + this.value + ">" + propertiesMiniSuffix;
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
        return this.value;
    }

    public Style getStyle() {
        return this.style;
    }

}
