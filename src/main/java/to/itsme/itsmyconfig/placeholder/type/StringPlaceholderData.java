package to.itsme.itsmyconfig.placeholder.type;

import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;

public final class StringPlaceholderData extends PlaceholderData {

    private final String message;

    public StringPlaceholderData(final String message) {
        super(PlaceholderType.STRING);
        this.message = message;
        registerArguments(this.message);
    }

    @Override
    public String getResult(final String[] params) {
        return this.replaceArguments(params, this.message);
    }

}
