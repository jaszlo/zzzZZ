package de.jasper.zzzzz;

import de.jasper.zzzzz.gui.EpilogueMessageSelectionScreen;
import de.jasper.zzzzz.gui.PrologMessageSelectionScreen;
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

import java.io.File;
import java.nio.file.Path;

public class ModInit implements ModInitializer {

	public static final String MOD_ID = "zzzzz";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "::client");

	public static final String ZZZZZ_FOLDER_PATH = Path.of(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "zzzzz").toString();
	public static final String MESSAGES_FOLDER_PATH = Path.of(ModInit.ZZZZZ_FOLDER_PATH, "messages").toString();
	public static final String OPTIONS_FOLDER_PATH = Path.of(ModInit.ZZZZZ_FOLDER_PATH, "options").toString();


	public static String[] REQUIRED_FOLDERS = {
		ZZZZZ_FOLDER_PATH,
		MESSAGES_FOLDER_PATH,
		OPTIONS_FOLDER_PATH

	};

	KeyBinding openSettings = new KeyBinding(
			"zzzzz.keybindings.openSettings",
			GLFW.GLFW_KEY_O,
			"zzzzz.keybindings.category"
	);

	@Override
	public void onInitialize() {

		// Create all required folders
		for (String path : REQUIRED_FOLDERS) {
			File required = new File(path);
			if (!required.exists()) {
				boolean failed = required.mkdirs();
				// Do not initialize mod if failed to create folder (should not happen)
				if (!failed) {
					LOGGER.error("Failed to create folder {}. zzZZZ-Mod will not be initialized", required.getName());
					return;
				}
			}
		}

		// Create these two screens once so that their lists are initialized
		{
			new PrologMessageSelectionScreen(Text.of(""), null, ZzzzzSettings.PROLOG_MESSAGES_FILE);
			new EpilogueMessageSelectionScreen(Text.of(""), null, ZzzzzSettings.EPILOGUE_MESSAGES_FILE);
		}


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