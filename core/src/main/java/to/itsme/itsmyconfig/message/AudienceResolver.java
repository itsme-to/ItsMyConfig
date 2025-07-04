package to.itsme.itsmyconfig.message;

import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.context.Source;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import to.itsme.itsmyconfig.ItsMyConfig;

public class AudienceResolver {

    private static final Resolver AUDIENCE_RESOLVER = new BukkitResolver();

    /**
     * Dummy method to load resolvers onEnable, and just logs it.
     */
    @SuppressWarnings("all")
    public static void load(final ItsMyConfig plugin) {
        plugin.getLogger().fine("Loaded " + AUDIENCE_RESOLVER.getClass().getSimpleName() + "successfully!");
    }

    public static Audience resolve(final CommandSender sender) {
        return AUDIENCE_RESOLVER.resolve(sender);
    }

    public static Audience resolve(final Source source) {
        return AUDIENCE_RESOLVER.resolve((BukkitSource) source);
    }

    public static Audience resolve(final BukkitSource source) {
        return AUDIENCE_RESOLVER.resolve(source);
    }

    public static void send(final CommandSender sender, final ComponentLike component) {
        AUDIENCE_RESOLVER.resolve(sender).sendMessage(component);
    }

    public static void send(final BukkitSource source, final ComponentLike component) {
        AUDIENCE_RESOLVER.resolve(source).sendMessage(component);
    }

    public static void close() {
        AUDIENCE_RESOLVER.close();
    }

    public sealed interface Resolver permits BukkitResolver, PaperResolver {

        Audience resolve(final CommandSender sender);

        default Audience resolve(final BukkitSource source) {
            return resolve(source.origin());
        }

        default void close() {}

    }

    public static final class BukkitResolver implements Resolver {

        private final BukkitAudiences audiences;

        {
            audiences = BukkitAudiences.create(ItsMyConfig.getInstance());
        }

        @Override
        public Audience resolve(final CommandSender sender) {
            return audiences.sender(sender);
        }

        @Override
        public void close() {
            this.audiences.close();
        }

    }

    public static final class PaperResolver implements Resolver {

        @Override
        public Audience resolve(final CommandSender sender) {
            return sender;
        }

    }

}
