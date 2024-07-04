package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathPlaceholder extends Placeholder {

    private final CompiledExpression expression;

    private final int precision;
    private final RoundingMode mode;

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
        if (provided != required) {
            return String.format("Invalid variable count, provided: %d, required: %d", provided, required);
        }

        final double[] vals = this.convertArray(args);
        if (vals == null) {
            return "One of the arguments is an invalid number";
        }

        final double result = expression.evaluate(vals);
        return new BigDecimal(result).setScale(this.precision, this.mode).stripTrailingZeros().toPlainString();
    }

    public double[] convertArray(final String[] stringArgs) {
        final double[] doubleArgs = new double[stringArgs.length];
        for (int i = 0; i < stringArgs.length; i++) {
            try {
                doubleArgs[i] = Double.parseDouble(stringArgs[i]);
            } catch (final NumberFormatException e) {
                return null;
            }
        }
        return doubleArgs;
    }

}
