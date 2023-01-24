package ua.realalpha.itsmyconfig;

import ua.realalpha.itsmyconfig.model.Model;
import ua.realalpha.itsmyconfig.model.ModelType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelRepository {

    private final Map<ModelType, Model> modelByName = new HashMap<>();


    public void registerModel(Model model){
        this.modelByName.put(model.getModelType(), model);
    }

    public boolean hasModel(ModelType modelType){
        return this.modelByName.containsKey(modelType);
    }

    public Model getModel(ModelType modelType){
        return this.modelByName.get(modelType);
    }

    public void clearModels(){
        this.modelByName.clear();
    }

    public Collection<Model> getModelByName() {
        return modelByName.values();
    }
}
