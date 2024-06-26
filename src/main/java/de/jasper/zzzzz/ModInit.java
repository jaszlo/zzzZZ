package de.jasper.zzzzz;

import de.jasper.zzzzz.gui.ZzzzzSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModInit implements ModInitializer {

	public static final String MOD_ID = "zzzzz";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "::client");

	KeyBinding openSettings = new KeyBinding(
			"zzzzz.keybindings.openSettings",
			GLFW.GLFW_KEY_O,
			"zzzzz.keybindings.category"
	);

	@Override
	public void onInitialize() {

		// Register SleepTracker to track state and write to chat accordingly
		SleepTracker.register();

		// Register option keybind
		KeyBindingHelper.registerKeyBinding(openSettings);

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				return;
			}

			if (openSettings.wasPressed()) {
				client.setScreen(new ZzzzzSettings(client.currentScreen));
			}
		});
		// Register settings to initialize config file and gui elements
		MinecraftClient.getInstance().execute(ZzzzzSettings::register);
	}
}