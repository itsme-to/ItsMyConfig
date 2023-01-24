package ua.realalpha.itsmyconfig.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ModelType {

    UNKNOWN,
    ACTIONBAR,
    TITLE,
    SUBTITLE,
    BOSS_BAR,
    TOAST;

    private static Map<String, ModelType> MODEL_TYPE_BY_NAME = new HashMap<>();

    static {
        for (ModelType modelType : ModelType.values()) {
            MODEL_TYPE_BY_NAME.put(modelType.name(), modelType);
        }
    }

    public static ModelType getModelType(String name){
        return MODEL_TYPE_BY_NAME.getOrDefault(name.toUpperCase(Locale.ROOT), UNKNOWN);
    }

    public String getTagName(){
        return name().toLowerCase(Locale.ROOT);
    }

}
