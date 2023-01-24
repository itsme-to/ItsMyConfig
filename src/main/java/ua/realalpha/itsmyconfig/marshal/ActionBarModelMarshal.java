package ua.realalpha.itsmyconfig.marshal;

import org.bukkit.configuration.ConfigurationSection;
import ua.realalpha.itsmyconfig.model.Model;

public class ActionBarModelMarshal implements ModelMarshal{

    @Override
    public Model unMarshal(ConfigurationSection configurationSection) {
        boolean removeInTchat = configurationSection.getBoolean("removeInChat", true);
        return null;
    }

}
