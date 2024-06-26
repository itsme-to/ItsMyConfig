package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.entity.Player;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

public final class MathPlaceholder extends Placeholder {

    private final CompiledExpression expression;

    public MathPlaceholder(final String value) {
        super(PlaceholderType.MATH);
        this.registerArguments(value);

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

        return String.valueOf(
                expression.evaluate(vals)
        );
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
