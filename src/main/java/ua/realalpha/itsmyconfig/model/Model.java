package ua.realalpha.itsmyconfig.model;

import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class Model {

    private final ModelType modelType;

    public Model(ModelType modelType) {
        this.modelType = modelType;;
    }

    public ModelType getModelType() {
        return modelType;
    }


    public abstract void apply(Player player, String message, Collection<String> tags);
}
