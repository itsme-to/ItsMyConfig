package to.itsme.itsmyconfig.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

public class NoAdventure<S> implements AdventureProvider<S> {
    
    public NoAdventure() {
        throw new RuntimeException("Ensure that Kyori Adventure has been loaded correctly in your java runtime !");
    }
    
    @Override
    public Audience audience(final S sender) {
        throw new UnsupportedOperationException("No Adventure loaded");
    }
    
    
    @Override
    public void send(final S sender, final ComponentLike component) {
        throw new UnsupportedOperationException("No Adventure loaded");
    }
    
}
