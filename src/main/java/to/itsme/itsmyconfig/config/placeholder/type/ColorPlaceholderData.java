package to.itsme.itsmyconfig.config.placeholder.type;

import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderType;

public final class ColorPlaceholderData extends PlaceholderData {

    private final String message;

    public ColorPlaceholderData(final String message) {
        super(PlaceholderType.STRING);
        this.message = message;
        registerArguments(this.message);
    }

    @Override
    public String getResult(final String[] params) {
        return this.replaceArguments(params, this.message);
    }

}
