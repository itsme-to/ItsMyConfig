package to.itsme.itsmyconfig.util;

import org.bukkit.Bukkit;

public final class Versions {

    private static final String VERSION_EXACT = Bukkit.getBukkitVersion().split("-")[0];
    private static final int MAJOR_VER, PATCH_VER ;

    static {
        final String[] splitVersion = VERSION_EXACT.split("\\.");
        MAJOR_VER = Integer.parseInt(splitVersion[1]);
        if (splitVersion.length > 2) {
            PATCH_VER = Integer.parseInt(splitVersion[2]);
        } else PATCH_VER = 0;
    }

    public static boolean isOver(
            final int version
    ) {
        return MAJOR_VER > version;
    }

    public static boolean isOver(
            final int version,
            final int patch
    ) {
        return MAJOR_VER > version && PATCH_VER > patch;
    }

}
