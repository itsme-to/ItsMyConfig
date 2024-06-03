package to.itsme.itsmyconfig.util;

import org.bukkit.Bukkit;

public final class Versions {

    public static final String VERSION_EXACT = Bukkit.getBukkitVersion().split("-")[0];
    public static final int INT_VER = Integer.parseInt(VERSION_EXACT.split("\\.")[1]);

}
