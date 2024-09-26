package to.itsme.itsmyconfig.util;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.relocation.Relocation;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.util.reflect.Reflections;

import java.util.function.Supplier;

public enum LibraryLoader {

    // ========================================================= //
    // Adventure Components & MiniMessage //
    ADVENTURE(
            "net.kyori",
            "adventure-text-minimessage",
            BuildParameters.ADVENTURE_VERSION,
            () -> !Reflections.findClass("net.kyori.adventure.text.Component")
    ),
    // ========================================================= //
    // Adventure Bukkit Platfrom //
    @SuppressWarnings("ConstantConditions")
    ADVENTURE_PLATFORM(
            "net.kyori",
            "adventure-platform-bukkit",
            BuildParameters.ADVENTURE_PLATFORM_VERSION,
            () -> !Audience.class.isAssignableFrom(CommandSender.class)
    ),
    // ========================================================= //
    // Adventure Components & MiniMessage //
    ADVENTURE_BUNGEE_SERIALIZER(
            "net.kyori",
            "adventure-text-serializer-bungeecord",
            BuildParameters.ADVENTURE_PLATFORM_VERSION,
            () -> !Reflections.findClass("net.kyori.adventure.text.serializer.bungeecord")
    )
    // ========================================================= //
    ;

    private static final LibraryManager MANAGER = new BukkitLibraryManager(ItsMyConfig.getInstance());

    static {
        MANAGER.addMavenCentral();
    }

    private final Library library;
    private final boolean shouldLoad;

    LibraryLoader(
            final String groupID,
            final String artifactID,
            final String version,
            final Supplier<Boolean> load,
            final Relocation... relocations
    ) {
        this(groupID, artifactID, version, null, load, relocations);
    }

    LibraryLoader(
            final String groupID,
            final String artifactID,
            final String version,
            final String repo,
            final Supplier<Boolean> load,
            final Relocation... relocations
    ) {
        final Library.Builder builder = Library.builder()
                .groupId(groupID)
                .artifactId(artifactID)
                .version(version)
                .resolveTransitiveDependencies(true);

        if (repo != null) {
            builder.repository(repo);
        }

        for (final Relocation relocation : relocations) {
            builder.relocate(relocation);
        }

        this.library = builder.build();
        this.shouldLoad = load.get();
    }

    public static void loadLibraries() {
        for (final LibraryLoader value : values()) {
            if (value.shouldLoad) MANAGER.loadLibrary(value.library);
        }
    }

    public boolean shouldLoad() {
        return shouldLoad;
    }

}
