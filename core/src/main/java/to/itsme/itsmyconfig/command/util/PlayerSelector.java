package to.itsme.itsmyconfig.command.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlayerSelector implements Iterable<Player> {

    private final Set<Player> players;

    public PlayerSelector(final Player player) {
        this.players = Set.of(player);
    }

    public PlayerSelector(final Player... players) {
        this.players = Set.of(players);
    }

    public PlayerSelector(final Collection<? extends Player> players) {
        this.players = new HashSet<>(players);
    }

    public static PlayerSelector of(final Player player) {
        return new PlayerSelector(player);
    }

    public static PlayerSelector of(final Player... player) {
        return new PlayerSelector(player);
    }

    public static PlayerSelector of(final Collection<? extends Player> collection) {
        return new PlayerSelector(collection);
    }

    public static PlayerSelector all() {
        return of(Bukkit.getOnlinePlayers());
    }

    @Override
    public @NotNull Iterator<Player> iterator() {
        return this.players.iterator();
    }

}
