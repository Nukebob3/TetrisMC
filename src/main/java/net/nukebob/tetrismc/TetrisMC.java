package net.nukebob.tetrismc;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.game.tetris.mino.Mino;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TetrisMC implements ClientModInitializer {
	public static final String MOD_ID = "tetrismc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static TetrisConfig CONFIG;

	@Override
	public void onInitializeClient() {
		CONFIG = TetrisConfig.loadConfig();

		Mino.BLOCKS = new ArrayList<>(BuiltInRegistries.BLOCK.stream().toList());
	}
}