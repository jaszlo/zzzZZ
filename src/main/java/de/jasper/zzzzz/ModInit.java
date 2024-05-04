package de.jasper.zzzzz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ModInit implements ModInitializer {

	KeyBinding openSettings = new KeyBinding(
			"zzzzz.keybindings.openSettings",
			GLFW.GLFW_KEY_O,
			"zzzzz.keybindings.category"
	);

	@Override
	public void onInitialize() {

		// Register option keybind
		KeyBindingHelper.registerKeyBinding(openSettings);

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				return;
			}

			if (openSettings.wasPressed()) {
				client.setScreen(new ZzzzzSettings("", null));
			}
		});

		SleepPrinter.register();
	}
}