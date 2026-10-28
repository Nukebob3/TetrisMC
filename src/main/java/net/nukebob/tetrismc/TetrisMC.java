package net.nukebob.tetrismc;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.game.tetris.mino.Mino;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TetrisMC implements ClientModInitializer {
	public static final String MOD_ID = "tetrismc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static TetrisConfig CONFIG;

	private static KeyMapping.Category KEY_CATEGORY;

	public static KeyMapping RIGHT;
	public static KeyMapping UP;
	public static KeyMapping LEFT;
	public static KeyMapping DOWN;
	public static KeyMapping DROP;

	private static KeyMapping registerKey(String name, int defaultKey) {
		return KeyMappingHelper.registerKeyMapping(
				new KeyMapping(
						"key." + MOD_ID + "." + name,
						InputConstants.Type.KEYBOARD,
						defaultKey,
						KEY_CATEGORY
				));
	}

	@Override
	public void onInitializeClient() {
		CONFIG = TetrisConfig.loadConfig();

		Mino.BLOCKS = new ArrayList<>(BuiltInRegistries.BLOCK.stream().toList());

		// Keybinds
		KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "keybinds"));

		RIGHT = registerKey("right", InputConstants.KEY_D);
		UP = registerKey("up", InputConstants.KEY_W);
		LEFT = registerKey("left", InputConstants.KEY_A);
		DOWN = registerKey("down", InputConstants.KEY_S);
		DROP = registerKey("hard_drop", InputConstants.KEY_SPACE);
	}
}