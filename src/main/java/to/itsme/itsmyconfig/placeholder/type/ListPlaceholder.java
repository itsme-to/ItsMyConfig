package to.itsme.itsmyconfig.placeholder.type;

import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Strings;

import java.util.List;

public class ListPlaceholder extends Placeholder {

    private final List<String> list;

    public ListPlaceholder(final List<String> list) {
        super(PlaceholderType.LIST);
        this.list = list;
    }

    @Override
    public String getResult(
            final String[] args
    ) {
        if (args.length == 0) {
            return "";
        }

        final int line = Strings.intOrDefault(args[0], 1) - 1;
        if (line > list.size()) {
            return "";
        }

        return list.get(line);
    }

}
