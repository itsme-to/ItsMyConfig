package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderDependancy;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public final class MathPlaceholder extends Placeholder {

    private static final NavigableMap<Long, String> GLOBAL_SUFFIXES = new TreeMap<>();

    static {
        UPDATE_FORMATTINGS();
    }

    public static void UPDATE_FORMATTINGS() {
        final FileConfiguration config = ItsMyConfig.getInstance().getConfig();
        GLOBAL_SUFFIXES.put(1_000L, config.getString("formatting.thousands", "k"));
        GLOBAL_SUFFIXES.put(1_000_000L, config.getString("formatting.millions", "M"));
        GLOBAL_SUFFIXES.put(1_000_000_000L, config.getString("formatting.billions", "B"));
        GLOBAL_SUFFIXES.put(1_000_000_000_000L, config.getString("formatting.trillions", "T"));
        GLOBAL_SUFFIXES.put(1_000_000_000_000_000L, config.getString("formatting.quadrillions", "Q"));
    }

    private final CompiledExpression expression;
    private final int variablesRequired;

    private final int precision;
    private final RoundingMode mode;
    private final DecimalFormat fixedFormat = new DecimalFormat("#");
    private final DecimalFormat commasFormat = new DecimalFormat("#,###");

    public MathPlaceholder(
            final String filePath,
            final ConfigurationSection section
    ) {
        super(section, filePath, PlaceholderType.MATH, PlaceholderDependancy.NONE);
        final String value = section.getString("value", "0");
        this.registerArguments(value);

        this.precision = section.getInt("precision");
        this.mode = RoundingMode.valueOf(section.getString("mode", "HALF_UP"));

        String copy = value;
        for (final int argument : this.arguments) {
            copy = copy.replace("{" + argument + "}", "$" + (argument + 1));
        }

        this.expression = Crunch.compileExpression(copy);
        this.variablesRequired = this.expression.getVariableCount();
    }

    @Override
    public String getResult(
            final OfflinePlayer player,
            final String[] args
    ) {
        final boolean prefixed = this.isPrefixed(args);
        final int providedArgs = prefixed ? args.length - 1 : args.length;
        if (providedArgs < variablesRequired) {
            return String.format("Invalid variable count, provided: %d, required: %d", providedArgs, variablesRequired);
        }

        final double[] vals = this.convertArray(args, variablesRequired, prefixed);
        if (vals == null) {
            return "One of the arguments is an invalid number";
        }

        final double result = expression.evaluate(vals);
        if (prefixed) {
            final String prefix = args[0];
            if (prefix.endsWith("dp")) {
                try {
                    final double nearest = Double.parseDouble(prefix.substring(0, prefix.length() - 2));
                    return String.valueOf(Math.round(result / nearest));
                } catch (final Throwable ignored) { return "Invalid DP calculation"; }
            } else switch (prefix) {
                case "commas":
                    return commasFormat.format(result);
                case "fixed":
                    return fixedFormat.format(result);
                case "formatted":
                    return formatNumber((long) result);
            }
        }

        return new BigDecimal(result).setScale(this.precision, this.mode).stripTrailingZeros().toPlainString();
    }

    public double[] convertArray(final String[] args, final int limit, final boolean prefixed) {
        final double[] doubleArgs = new double[limit];
        for (int i = 0; i < limit; i++) {
            final String arg = args[prefixed ? i + 1 : i];
            try {
                doubleArgs[i] = Double.parseDouble(arg);
            } catch (final NumberFormatException e) {
                Utilities.debug(() -> arg + " is not a number, returning null");
                return null;
            }
        }
        return doubleArgs;
    }

    @SuppressWarnings("all")
    private String formatNumber(long balance) {
        if (balance == Long.MIN_VALUE) {
            return formatNumber(Long.MIN_VALUE + 1);
        }
        if (balance < 0) {
            return "-" + formatNumber(-balance);
        }

        if (balance < 1000) {
            return Long.toString(balance);
        }

        final Map.Entry<Long, String> e = GLOBAL_SUFFIXES.floorEntry(balance);
        final Long divideBy = e.getKey();
        final String suffix = e.getValue();

        final long truncated = balance / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    private boolean isPrefixed(final String[] args) {
        if (args.length == 0) return false;

        final String arg = args[0].toLowerCase(Locale.ENGLISH);
        if (arg.endsWith("dp")) {
            return true;
        }

        return switch (arg) {
            case "commas", "fixed", "formatted" -> true;
            default -> false;
        };
    }

}
