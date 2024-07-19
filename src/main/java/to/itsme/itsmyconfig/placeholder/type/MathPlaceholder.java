package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class MathPlaceholder extends Placeholder {

    private static final NavigableMap<Long, String> GLOBAL_SUFFIXES = new TreeMap<>();

    static {
        final FileConfiguration config = ItsMyConfig.getInstance().getConfig();
        GLOBAL_SUFFIXES.put(1_000L, config.getString("formatting.thousands", "K"));
        GLOBAL_SUFFIXES.put(1_000_000L, config.getString("formatting.millions", "M"));
        GLOBAL_SUFFIXES.put(1_000_000_000L, config.getString("formatting.billions", "B"));
        GLOBAL_SUFFIXES.put(1_000_000_000_000L, config.getString("formatting.trillions", "T"));
        GLOBAL_SUFFIXES.put(1_000_000_000_000_000L, config.getString("formatting.quadrillions", "Q"));
    }

    private final CompiledExpression expression;

    private final int precision;
    private final RoundingMode mode;
    private final DecimalFormat fixedFormat = new DecimalFormat("#");
    private final DecimalFormat commasFormat = new DecimalFormat("#,###");

    public MathPlaceholder(
            final String filePath,
            final ConfigurationSection section
    ) {
        super(section, filePath, PlaceholderType.MATH);
        final String value = section.getString("value");
        this.registerArguments(value);

        this.precision = section.getInt("precision");
        this.mode = RoundingMode.valueOf(section.getString("mode", "HALF_UP"));

        String copy = value;
        for (final int argument : this.arguments) {
            copy = copy.replace("{" + argument + "}", "$" + (argument + 1));
        }

        this.expression = Crunch.compileExpression(copy);
    }

    @Override
    public String getResult(
            final Player player,
            final String[] args
    ) {
        final int provided = args.length;
        final int required = expression.getVariableCount();
        if (provided < required) {
            return String.format("Invalid variable count, provided: %d, required: %d", provided, required);
        }

        final double[] vals = this.convertArray(args, required);
        if (vals == null) {
            return "One of the arguments is an invalid number";
        }

        final double result = expression.evaluate(vals);

        if (provided > required) {
            final String type = args[required + 1];
            if ("commas".equals(type)) {
                return commasFormat.format(result);
            } else if ("fixed".equals(type)) {
                return fixedFormat.format(result);
            } else if ("formatted".equals(type)) {
                return formatNumber((long) result);
            } else if (type.endsWith("dp")) {
                try {
                    final double nearest = Double.parseDouble(type.substring(0, type.length() - 2));
                    return String.valueOf(Math.round(result / nearest));
                } catch (final Throwable ignored) { return "Invalid DP calculation"; }
            }
        }

        return new BigDecimal(result).setScale(this.precision, this.mode).stripTrailingZeros().toPlainString();
    }

    public double[] convertArray(final String[] args, final int limit) {
        final double[] doubleArgs = new double[limit];
        for (int i = 0; i < limit; i++) {
            try {
                doubleArgs[i] = Double.parseDouble(args[i]);
            } catch (final NumberFormatException e) {
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

}
