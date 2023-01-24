package ua.realalpha.itsmyconfig.marshal;

import org.bukkit.configuration.ConfigurationSection;
import ua.realalpha.itsmyconfig.model.Model;

public class TitleModelMarshal implements ModelMarshal{
    @Override
    public Model unMarshal(ConfigurationSection configurationSection) {
        boolean removeInTchat = configurationSection.getBoolean("removeInChat", true);
        int duration = configurationSection.getInt("duration", 30);
        int fadeIn = configurationSection.getInt("fadeIn", 1);
        int fadeOut = configurationSection.getInt("fadeOut", 1);
        return null;
    }
}
