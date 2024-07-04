package to.itsme.itsmyconfig.placeholder.type;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.util.Strings;

import java.util.List;

public class ListPlaceholder extends Placeholder {

    private final List<String> list;

    public ListPlaceholder(final ConfigurationSection section) {
        super(section, PlaceholderType.LIST);
        this.list = section.getStringList("values");
    }

    @Override
    public String getResult(
            final Player player, final String[] args
    ) {
        if (args.length == 0) {
            return "";
        }

        final int line = Strings.intOrDefault(args[0], 1) - 1;
        if (line >= list.size()) {
            return "";
        }

        return list.get(line);
    }

}
