package to.itsme.itsmyconfig.util;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.enums.EntityTaskResult;
import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import to.itsme.itsmyconfig.ItsMyConfig;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("all")
public final class Scheduler {

    private static final ServerImplementation impl = new FoliaLib(ItsMyConfig.getInstance()).getImpl();

    public static CompletableFuture<Void> runNextTick(
            final @NotNull Consumer<WrappedTask> task
    ) {
        return impl.runNextTick(task);
    }

    public static CompletableFuture<Void> runAsync(
            final @NotNull Consumer<WrappedTask> task
    ) {
        return impl.runAsync(task);
    }

    public static WrappedTask runLater(
            final @NotNull Runnable task,
            final long delay
    ) {
        return impl.runLater(task, delay);
    }

    public static void runLater(
            final @NotNull Consumer<WrappedTask> task,
            final long delay
    ) {
        impl.runLater(task, delay);
    }

    public static WrappedTask runLater(
            final @NotNull Runnable task,
            final long delay,
            final TimeUnit unit
    ) {
        return impl.runLater(task, delay, unit);
    }

    public static void runLater(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final TimeUnit unit
    ) {
        impl.runLater(task, delay, unit);
    }

    public static WrappedTask runLaterAsync(
            final @NotNull Runnable task,
            final long delay
    ) {
        return impl.runLaterAsync(task, delay);
    }

    public static void runLaterAsync(
            final @NotNull Consumer<WrappedTask> task,
            final long delay
    ) {
        impl.runLaterAsync(task, delay);
    }

    public static WrappedTask runLaterAsync(
            final @NotNull Runnable task,
            final long delay,
            final TimeUnit unit
    ) {
        return impl.runLaterAsync(task, delay, unit);
    }

    public static void runLaterAsync(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final TimeUnit unit
    ) {
        impl.runLaterAsync(task, delay, unit);
    }

    public static WrappedTask runTimer(
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return impl.runTimer(task, delay, period);
    }

    public static void runTimer(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period
    ) {
        impl.runTimer(task, delay, period);
    }

    public static WrappedTask runTimer(
            final @NotNull Runnable task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        return impl.runTimer(task, delay, period, unit);
    }

    public static void runTimer(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        impl.runTimer(task, delay, period, unit);
    }

    public static WrappedTask runTimerAsync(
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return impl.runTimerAsync(task, delay, period);
    }

    public static void runTimerAsync(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period
    ) {
        impl.runTimerAsync(task, delay, period);
    }

    public static WrappedTask runTimerAsync(
            final @NotNull Runnable task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        return impl.runTimerAsync(task, delay, period, unit);
    }

    public static void runTimerAsync(
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        impl.runTimerAsync(task, delay, period, unit);
    }

    public static CompletableFuture<Void> runAtLocation(
            final Location location,
            final @NotNull Consumer<WrappedTask> task
    ) {
        return impl.runAtLocation(location, task);
    }

    public static WrappedTask runAtLocationLater(
            final Location location,
            final @NotNull Runnable task,
            final long delay
    ) {
        return impl.runAtLocationLater(location, task, delay);
    }

    public static void runAtLocationLater(
            final Location location,
            final @NotNull Consumer<WrappedTask> task,
            final long delay
    ) {
        impl.runAtLocationLater(location, task, delay);
    }

    public static WrappedTask runAtLocationLater(
            final Location location,
            final @NotNull Runnable task,
            final long delay,
            final TimeUnit unit
    ) {
        return impl.runAtLocationLater(location, task, delay, unit);
    }

    public static void runAtLocationLater(
            final Location location,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final TimeUnit unit
    ) {
        impl.runAtLocationLater(location, task, delay, unit);
    }

    public static WrappedTask runAtLocationTimer(
            final Location location,
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return impl.runAtLocationTimer(location, task, delay, period);
    }

    public static void runAtLocationTimer(
            final Location location,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period
    ) {
        impl.runAtLocationTimer(location, task, delay, period);
    }

    public static WrappedTask runAtLocationTimer(
            final Location location,
            final @NotNull Runnable task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        return impl.runAtLocationTimer(location, task, delay, period, unit);
    }

    public static void runAtLocationTimer(
            final Location location,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        impl.runAtLocationTimer(location, task, delay, period, unit);
    }

    public static CompletableFuture<EntityTaskResult> runAtEntity(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task
    ) {
        return impl.runAtEntity(entity, task);
    }

    public static CompletableFuture<EntityTaskResult> runAtEntityWithFallback(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final @Nullable Runnable fallback
    ) {
        return impl.runAtEntityWithFallback(entity, task, fallback);
    }

    public static WrappedTask runAtEntityLater(
            final Entity entity,
            final @NotNull Runnable task,
            final long delay
    ) {
        return impl.runAtEntityLater(entity, task, delay);
    }

    public static WrappedTask runAtEntityLater(
            final Entity entity,
            final @NotNull Runnable task,
            final @Nullable Runnable fallback,
            final long delay
    ) {
        return impl.runAtEntityLater(entity, task, fallback, delay);
    }

    public static void runAtEntityLater(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final long delay
    ) {
        impl.runAtEntityLater(entity, task, delay);
    }

    public static void runAtEntityLater(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final @Nullable Runnable fallback,
            final long delay
    ) {
        impl.runAtEntityLater(entity, task, fallback, delay);
    }

    public static WrappedTask runAtEntityLater(
            final Entity entity,
            final @NotNull Runnable task,
            final long delay,
            final TimeUnit unit
    ) {
        return impl.runAtEntityLater(entity, task, delay, unit);
    }

    public static void runAtEntityLater(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final TimeUnit unit
    ) {
        impl.runAtEntityLater(entity, task, delay, unit);
    }

    public static WrappedTask runAtEntityTimer(
            final Entity entity,
            final @NotNull Runnable task,
            final long delay,
            final long period
    ) {
        return impl.runAtEntityTimer(entity, task, delay, period);
    }

    public static WrappedTask runAtEntityTimer(
            final Entity entity,
            final @NotNull Runnable task,
            final Runnable fallback,
            final long delay,
            final long period
    ) {
        return impl.runAtEntityTimer(entity, task, fallback, delay, period);
    }

    public static void runAtEntityTimer(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period
    ) {
        impl.runAtEntityTimer(entity, task, delay, period);
    }

    public static void runAtEntityTimer(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final Runnable fallback,
            final long delay,
            final long period
    ) {
        impl.runAtEntityTimer(entity, task, fallback, delay, period);
    }

    public static WrappedTask runAtEntityTimer(
            final Entity entity,
            final @NotNull Runnable task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        return impl.runAtEntityTimer(entity, task, delay, period, unit);
    }

    public static void runAtEntityTimer(
            final Entity entity,
            final @NotNull Consumer<WrappedTask> task,
            final long delay,
            final long period,
            final TimeUnit unit
    ) {
        impl.runAtEntityTimer(entity, task, delay, period, unit);
    }

    public static void cancelTask(
            final WrappedTask task
    ) {
        impl.cancelTask(task);
    }

    public static void cancelAllTasks() {
        impl.cancelAllTasks();
    }

    public static List<WrappedTask> getAllTasks() {
        return impl.getAllTasks();
    }

    public static List<WrappedTask> getAllServerTasks() {
        return impl.getAllServerTasks();
    }

    public static Player getPlayer(
            final String name
    ) {
        return impl.getPlayer(name);
    }

    public static Player getPlayerExact(
            final String name
    ) {
        return impl.getPlayerExact(name);
    }

    public static Player getPlayer(
            final UUID uuid
    ) {
        return impl.getPlayer(uuid);
    }

    public static CompletableFuture<Boolean> teleportAsync(
            final Player player,
            final Location location
    ) {
        return impl.teleportAsync(player, location);
    }

    public static WrappedTask wrapTask(
            final Object task
    ) {
        return impl.wrapTask(task);
    }

}
