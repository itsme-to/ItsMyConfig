package to.itsme.itsmyconfig.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

/**
 * The {@code AdventureProvider} interface defines a mechanism for obtaining an
 * {@link Audience} based on a given source or context. It is designed to
 * facilitate communication or message broadcasting to a specific audience
 * within the application, typically in a context like a game server or
 * chat system where audiences are dynamically determined.
 *
 * @param <S> the type of the source from which the {@link Audience} can be derived
 */
public interface AdventureProvider<S> {
    
    /**
     * Obtains an {@link Audience} for the specified source.
     *
     * @param s the source from which the {@link Audience} is derived
     * @return the {@link Audience} corresponding to the given source
     */
    Audience audience(final S s);

    
    /**
     * Sends a message to the {@link Audience} derived from the specified source.
     *
     * @param sender    the source from which the {@link Audience} is derived
     * @param component the message to be sent, represented by a {@link ComponentLike} instance
     */
    default void send(final S sender, final ComponentLike component) {
        this.audience(sender).sendMessage(component);
    }
    
    /**
     * Performs any necessary cleanup or resource management. By default, this method
     * does nothing, but it can be overridden to provide specific close operations.
     */
    default void close() {
    }
    
}
