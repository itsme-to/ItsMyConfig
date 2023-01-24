package ua.realalpha.itsmyconfig.marshal;

import org.bukkit.configuration.ConfigurationSection;
import ua.realalpha.itsmyconfig.model.Model;

public interface ModelMarshal {

    Model unMarshal(ConfigurationSection configurationSection);

}
