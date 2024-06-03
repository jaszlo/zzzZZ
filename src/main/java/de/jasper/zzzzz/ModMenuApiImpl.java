package de.jasper.zzzzz;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.jasper.zzzzz.gui.ZzzzzSettings;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<ZzzzzSettings>) ZzzzzSettings::new;
    }

}