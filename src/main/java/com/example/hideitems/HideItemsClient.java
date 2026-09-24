package com.example.hideitems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HideItemsClient implements ClientModInitializer {
	public static final String MOD_ID = "hideitems";

	private static boolean hidden = false;
	private static KeyBinding toggleKey;
	private static Path stateFile;

	/** Read by the mixin every time the game decides whether to draw an entity. */
	public static boolean isHidden() {
		return hidden;
	}

	@Override
	public void onInitializeClient() {
		stateFile = FabricLoader.getInstance().getConfigDir().resolve("hideitems.txt");
		load();

		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.hideitems.toggle",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				KeyBinding.Category.create(Identifier.of(MOD_ID, "main"))
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.wasPressed()) {
				hidden = !hidden;
				save();
				if (client.player != null) {
					client.player.sendMessage(
							Text.translatable(hidden ? "message.hideitems.hidden" : "message.hideitems.shown"),
							true // show in the action bar instead of chat
					);
				}
			}
		});
	}

	private static void load() {
		try {
			if (Files.exists(stateFile)) {
				hidden = Boolean.parseBoolean(Files.readString(stateFile).trim());
			}
		} catch (IOException ignored) {
		}
	}

	private static void save() {
		try {
			Files.writeString(stateFile, Boolean.toString(hidden));
		} catch (IOException ignored) {
		}
	}
}
